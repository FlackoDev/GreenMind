package com.example.greenmind.resource.auth;

import android.os.Bundle;
import android.text.TextUtils; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button; 
import android.widget.TextView;
import android.widget.Toast; 

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenmind.R;
import com.google.android.material.textfield.TextInputEditText; 

public class RegisterFragment extends Fragment {

    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        registerButton = view.findViewById(R.id.registerButton);
        TextView loginTextView = view.findViewById(R.id.loginTextView);

        // --- GESTIONE CLICK SU BOTTONE "REGISTRATI" ---
        registerButton.setOnClickListener(v -> {
            handleRegistration();
        });

        // --- NUOVO: GESTIONE TASTO "INVIO" SULLA TASTIERA ---
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            // Controlla se l'azione è "IME_ACTION_DONE" (il tasto Invio/Fatto)
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Esegue la stessa logica del click sul bottone
                handleRegistration();
                return true; // Indica che l'evento è stato gestito
            }
            return false; // Lascia che il sistema gestisca altri eventi
        });

        // Logica per tornare al login
        loginTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }

    /**
     * Gestisce la logica di registrazione. Controlla i campi e, se validi,
     * procede con la registrazione.
     */
    private void handleRegistration() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // VALIDAZIONE: Controlla se i campi sono vuoti
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Mostra un messaggio di errore all'utente
            Toast.makeText(getContext(), "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show();
            return; // Interrompe l'esecuzione del metodo
        }

        // Se la validazione è superata, mostra un messaggio di successo.
        Toast.makeText(getContext(), "Registrazione per " + name + " completata!", Toast.LENGTH_LONG).show();

        // EXTRA: Dopo la registrazione, naviga l'utente alla schermata di login.
        NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment);
    }
}