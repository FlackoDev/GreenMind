package com.example.greenmind;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

// Non dimenticare di importare la classe di binding generata automaticamente
import com.example.greenmind.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {

    // Variabile per gestire gli elementi del layout (View Binding)
    private FragmentSplashBinding binding;

    // Durata della splash screen in millisecondi (3000ms = 3 secondi)
    private static final int SPLASH_DELAY = 3000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Gonfia" (inflates) il layout XML e lo collega a questa classe
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Crea un Handler per eseguire un'azione con un ritardo
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Questo codice viene eseguito dopo 3 secondi

                // Trova il controller di navigazione e avvia l'azione
                // per andare al LoginFragment.
                NavHostFragment.findNavController(SplashFragment.this)
                        .navigate(R.id.action_splashFragment_to_loginFragment);
            }
        }, SPLASH_DELAY);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Nasconde l'ActionBar se esiste
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Ri-mostra l'ActionBar quando si lascia questo fragment, se esisteva
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Pulisce il riferimento al binding quando la vista viene distrutta
        // per evitare problemi di memoria.
        binding = null;
    }
}