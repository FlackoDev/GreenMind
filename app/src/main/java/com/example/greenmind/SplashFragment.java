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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;


import com.example.greenmind.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;

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

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                NavHostFragment.findNavController(SplashFragment.this)
                        .navigate(R.id.action_splashFragment_to_loginFragment);
            }
        }, SPLASH_DELAY);
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity act = (AppCompatActivity) requireActivity();
        if (act.getSupportActionBar() != null) act.getSupportActionBar().hide();

        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(requireActivity().getWindow(),
                        requireActivity().getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    @Override
    public void onStop() {
        super.onStop();

        AppCompatActivity act = (AppCompatActivity) requireActivity();
        if (act.getSupportActionBar() != null) act.getSupportActionBar().show();

        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(requireActivity().getWindow(),
                        requireActivity().getWindow().getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}