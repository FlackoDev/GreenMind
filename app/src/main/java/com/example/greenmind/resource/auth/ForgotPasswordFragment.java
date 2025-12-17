package com.example.greenmind.resource.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        continueButton.setOnClickListener(v -> handlePasswordReset());
    }

    private void handlePasswordReset() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Inserisci la tua email");
            return;
        }

        // Verifica se l'utente esiste (sempre per sicurezza, ma qui dobbiamo procedere)
        if (userDao.getByEmail(email) == null) {
            // Per sicurezza non diciamo che non esiste, ma simuliamo comunque l'invio
            Toast.makeText(getContext(), "Se l'email è presente nei nostri sistemi, riceverai un codice.", Toast.LENGTH_LONG).show();
            return;
        }

        // Simulazione invio codice
        String verificationCode = generateVerificationCode();
        showVerificationDialog(email, verificationCode);
    }

    private String generateVerificationCode() {
        // Genera un codice casuale a 6 cifre
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void showVerificationDialog(String email, String code) {
        // In una vera app, questo codice verrebbe inviato via email.
        // Qui lo mostriamo in un popup per permettere lo sviluppo.
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Codice di Verifica")
                .setMessage("Abbiamo simulato l'invio di un'email a " + email + ".\n\nIl tuo codice di verifica è: " + code)
                .setPositiveButton("Inserisci Codice", (dialog, which) -> {
                    showResetPasswordDialog(email);
                })
                .show();
    }

    private void showResetPasswordDialog(String email) {
        // Creiamo una view programmabile per il cambio password nel dialog
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
