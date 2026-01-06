package com.example.greenmind.resource.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.dao.UserDao;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private TextInputEditText emailEditText, passwordEditText;
    private UserDao userDao;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userDao = new UserDao(requireContext());
        sessionManager = new SessionManager(requireContext());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView registerTextView = view.findViewById(R.id.registerTextView);
        TextView forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);

        loginButton.setOnClickListener(v -> handleLogin());

        registerTextView.setOnClickListener(v -> NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_registerFragment));

        forgotPasswordTextView.setOnClickListener(v -> NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }

    private void handleLogin() {
        if (sessionManager.isGlobalLocked()) {
            showLockoutDialog();
            return;
        }

        if (emailEditText.getText() == null || passwordEditText.getText() == null) return;

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Per favore, inserisci email e password", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao.AuthResult result = userDao.authenticate(email, password);

        switch (result) {
            case SUCCESS:
                completeLogin(userDao.getLastAuthenticatedUser());
                break;

            case NEED_SET_PIN:
                showSetupAdminDialog(email);
                break;

            case NEED_VERIFY_PIN:
                showVerifyPinDialog(email);
                break;

            case LOCKED:
                showLockoutDialog();
                break;

            case INVALID_CREDENTIALS:
                sessionManager.incrementGlobalFailures();
                if (sessionManager.isGlobalLocked()) {
                    showLockoutDialog();
                } else {
                    Toast.makeText(getContext(), "Email o password non corretti.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showSetupAdminDialog(String email) {
        final EditText passInput = new EditText(requireContext());
        passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passInput.setHint("Nuova Password");

        final EditText pinInput = new EditText(requireContext());
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setHint("Nuovo PIN (4+ cifre)");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(passInput);
        layout.addView(pinInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Configurazione Admin")
                .setMessage("Per sicurezza, imposta una nuova password e un PIN per il tuo primo accesso.")
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("Configura", (dialog, which) -> {
                    String newPass = passInput.getText().toString();
                    String newPin = pinInput.getText().toString();
                    if (newPass.length() >= 6 && newPin.length() >= 4) {
                        if (userDao.setupAdminAccount(email, newPass, newPin)) {
                            completeLogin(userDao.getByEmail(email));
                        }
                    } else {
                        Toast.makeText(getContext(), "Password (min 6) o PIN (min 4) troppo brevi", Toast.LENGTH_SHORT).show();
                        showSetupAdminDialog(email);
                    }
                })
                .show();
    }

    private void showVerifyPinDialog(String email) {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("PIN di sicurezza");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Verifica Admin")
                .setMessage("Inserisci il tuo PIN per accedere.")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Accedi", (dialog, which) -> {
                    String pin = input.getText().toString();
                    if (userDao.verifyAdminPin(email, pin)) {
                        completeLogin(userDao.getByEmail(email));
                    } else {
                        Toast.makeText(getContext(), "PIN Errato.", Toast.LENGTH_SHORT).show();
                        // Controllo se scatta il blocco
                        if (userDao.authenticate(email, "check") == UserDao.AuthResult.LOCKED) {
                            showLockoutDialog();
                        }
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void completeLogin(User user) {
        sessionManager.logout();
        sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getRole());
        sessionManager.resetGlobalFailures();
        Toast.makeText(getContext(), "Bentornato, " + user.getName() + "!", Toast.LENGTH_LONG).show();
        navigateToHome();
    }

    private void showLockoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sicurezza GreenMind")
                .setMessage("Troppi tentativi errati. Accesso bloccato per 15 minuti.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_lock_lock)
                .setCancelable(false)
                .show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(requireContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
