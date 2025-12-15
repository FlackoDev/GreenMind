package com.example.greenmind.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // NUOVO IMPORT

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment; // NUOVO IMPORT

import com.example.greenmind.R;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    // --- BLOCCO DI CODICE COMPLETAMENTE NUOVO ---

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Troviamo il nostro TextView "Accedi" nel layout
        //    usando il suo ID (lo avevamo chiamato 'loginTextView').
        TextView loginTextView = view.findViewById(R.id.loginTextView);

        // 2. Impostiamo un ascoltatore di clic su quel TextView.
        loginTextView.setOnClickListener(v -> {

            // 3. Eseguiamo l'azione per tornare al LoginFragment.
            //    Questa azione l'abbiamo già definita in nav_graph.xml.
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }
}