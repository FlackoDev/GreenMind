package com.example.greenmind.resource.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenmind.R;
import com.example.greenmind.data.db.dao.UserDao;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class ForgotPasswordFragment extends Fragment {

    private TextInputEditText emailEditText;
    private Button continueButton;
    private ImageButton backButton;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userDao = new UserDao(requireContext());
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailEditText);
        continueButton = view.findViewById(R.id.continueButton);
        backButton = view.findViewById(R.id.backButton);

        continueButton.setOnClickListener(v -> handlePasswordReset());

        // Torna indietro al Login
        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    private void handlePasswordReset() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Inserisci la tua email");
            return;
        }

        if (userDao.getByEmail(email) == null) {
            Toast.makeText(getContext(), "Se l'email è presente nei nostri sistemi, riceverai un codice.", Toast.LENGTH_LONG).show();
            return;
        }

        String verificationCode = generateVerificationCode();
        showVerificationDialog(email, verificationCode);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void showVerificationDialog(String email, String code) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Codice di Verifica")
                .setMessage("Abbiamo simulato l'invio di un'email a " + email + ".\n\nIl tuo codice di verifica è: " + code)
                .setPositiveButton("Inserisci Codice", (dialog, which) -> {
                    showResetPasswordDialog(email);
                })
                .show();
    }

    private void showResetPasswordDialog(String email) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        TextInputEditText newPasswordEntry = dialogView.findViewById(R.id.newPasswordEditText);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nuova Password")
                .setMessage("Inserisci la tua nuova password per l'account " + email)
                .setView(dialogView)
                .setPositiveButton("Aggiorna", (dialog, which) -> {
                    String newPass = newPasswordEntry.getText().toString().trim();
                    if (!TextUtils.isEmpty(newPass)) {
                        if (userDao.updatePassword(email, newPass)) {
                            Toast.makeText(getContext(), "Password aggiornata con successo!", Toast.LENGTH_SHORT).show();
                            NavHostFragment.findNavController(this).navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
                        } else {
                            Toast.makeText(getContext(), "Errore durante l'aggiornamento", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }
}
