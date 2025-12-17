package com.example.greenmind.resource.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.dao.UserDao;
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

        // COMMENTATO PER TEST: Login automatico disabilitato
        /*
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }
        */

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
                User user = userDao.getLastAuthenticatedUser();
                sessionManager.createLoginSession(user.getId(), user.getName());
                sessionManager.resetGlobalFailures();
                Toast.makeText(getContext(), "Bentornato, " + user.getName() + "!", Toast.LENGTH_LONG).show();
                navigateToHome();
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

    private void showLockoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sicurezza GreenMind")
                .setMessage("Abbiamo rilevato troppi tentativi di accesso non riusciti.\n\nPer proteggere i tuoi dati, l'accesso da questo dispositivo è stato temporaneamente bloccato per 15 minuti.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_lock_lock)
                .setCancelable(false)
                .show();
    }

    private void navigateToHome() {
        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_homeActivity);
    }
}
