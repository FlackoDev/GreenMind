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

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Gonfia" il layout XML e lo tiene in una variabile 'view'.
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    // --- BLOCCO DI CODICE COMPLETAMENTE NUOVO ---

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Troviamo il nostro TextView "Registrati" nel layout
        //    usando il suo ID univoco.
        TextView registerTextView = view.findViewById(R.id.registerTextView);

        // 2. Impostiamo un "ascoltatore di clic" su quel TextView.
        //    Questo blocco di codice verrà eseguito solo quando l'utente
        //    tocca il testo "Registrati".
        registerTextView.setOnClickListener(v -> {

            // 3. Eseguiamo l'azione di navigazione che abbiamo definito
            //    nel file nav_graph.xml.
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
