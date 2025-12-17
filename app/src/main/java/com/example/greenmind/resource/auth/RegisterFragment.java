package com.example.greenmind.resource.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenmind.R;
import com.example.greenmind.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    // Usa ViewBinding per evitare errori con findViewById
    private FragmentRegisterBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Carica il layout usando ViewBinding
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Imposta l'ascoltatore per il pulsante di registrazione
        binding.registerButton.setOnClickListener(v -> {
            handleRegistration();
        });

        // Imposta l'ascoltatore per il testo "Accedi"
        binding.loginTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        });

        // Imposta l'ascoltatore per il tasto "Invio" sulla password
        binding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleRegistration();
                return true;
            }
            return false;
        });
    }

    private void handleRegistration() {
        String name = binding.nameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Registrazione per " + name + " completata!", Toast.LENGTH_LONG).show();

        // Naviga alla schermata di login dopo la registrazione
        NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Pulisce il riferimento al binding per evitare memory leak
        binding = null;
    }
}