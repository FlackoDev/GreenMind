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
import com.example.greenmind.data.db.dao.UserDao;
import com.example.greenmind.databinding.FragmentRegisterBinding;
import com.example.greenmind.utils.SecurityUtils;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        userDao = new UserDao(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerButton.setOnClickListener(v -> handleRegistration());

        binding.loginTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        });

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

        if (TextUtils.isEmpty(name)) {
            binding.nameEditText.setError("Inserisci il tuo nome");
            return;
        }

        if (!SecurityUtils.isValidEmail(email)) {
            binding.emailEditText.setError("Inserisci un'email valida");
            return;
        }

        // VALIDAZIONE PASSWORD ATTIVATA
        if (!SecurityUtils.isValidPassword(password)) {
            binding.passwordEditText.setError("La password deve avere almeno 8 caratteri, una maiuscola, un numero e un simbolo");
            Toast.makeText(getContext(), "Password troppo debole", Toast.LENGTH_LONG).show();
            return;
        }

        if (userDao.getByEmail(email) != null) {
            binding.emailEditText.setError("Questa email è già registrata");
            return;
        }

        long id = userDao.registerUser(name, email, password);

        if (id > 0) {
            Toast.makeText(getContext(), "Registrazione completata con successo! Ora effettua il login.", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment);
        } else {
            Toast.makeText(getContext(), "Errore tecnico durante la registrazione", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
