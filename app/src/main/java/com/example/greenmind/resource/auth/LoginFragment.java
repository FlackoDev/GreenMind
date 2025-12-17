package com.example.greenmind.auth;

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
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView; // NUOVA VARIABILE

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Colleghiamo le variabili agli elementi del layout
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        TextView registerTextView = view.findViewById(R.id.registerTextView);
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView); // NUOVO COLLEGAMENTO

        // Impostiamo l'ascoltatore per il pulsante di login
        loginButton.setOnClickListener(v -> {
            handleLogin();
        });

        // Logica per andare a registrazione
        registerTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });

        // NUOVO: Logica per andare a password dimenticata
        forgotPasswordTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Per favore, inserisci email e password", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Login effettuato con successo!", Toast.LENGTH_LONG).show();

        // **SECONDA PARTE DELLA TUA RICHIESTA**
        // Dopo un login corretto, navighiamo alla pagina dei quiz.
        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_quizTestFragment);
    }
}