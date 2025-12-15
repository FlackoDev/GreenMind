package com.example.greenmind.auth;

import android.os.Bundle;
import android.text.TextUtils; // NUOVO IMPORT
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // NUOVO IMPORT
import android.widget.TextView;
import android.widget.Toast; // NUOVO IMPORT

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenmind.R;
import com.google.android.material.textfield.TextInputEditText; // NUOVO IMPORT

public class RegisterFragment extends Fragment {

    // --- NUOVO: Variabili per gli elementi della UI ---
    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    // ----------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- INIZIO BLOCCO MODIFICATO ---

        // 1. Colleghiamo le variabili agli elementi del layout
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        registerButton = view.findViewById(R.id.registerButton);
        TextView loginTextView = view.findViewById(R.id.loginTextView);

        // 2. Impostiamo l'ascoltatore per il pulsante di registrazione
        registerButton.setOnClickListener(v -> {
            // Questo metodo verrà chiamato quando il pulsante viene cliccato
            handleRegistration();
        });

        // 3. Logica per tornare al login (già presente)
        loginTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        });

        // --- FINE BLOCCO MODIFICATO ---
    }

    // --- METODO COMPLETAMENTE NUOVO ---
    private void handleRegistration() {
        // 1. Leggiamo il testo dai campi di input
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 2. Controlliamo se i campi sono vuoti
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Mostriamo un messaggio di errore
            Toast.makeText(getContext(), "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show();
            return; // Interrompiamo l'esecuzione del metodo
        }

        // 3. Se i controlli sono superati, mostriamo un messaggio di successo
        //    (Per ora, in futuro qui salveremo i dati)
        Toast.makeText(getContext(), "Registrazione per " + name + " completata!", Toast.LENGTH_LONG).show();

        // EXTRA: Dopo la registrazione, potremmo voler portare l'utente alla schermata di login.
        // NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment);
    }
}