package com.example.greenmind.resource.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenmind.databinding.FragmentChatBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private GenerativeModelFutures model;

    // API KEY
    private static final String API_KEY = "AIzaSyCKMtjL7NyfPUkCz5IKv99vafp7mmyAwM4";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupChat();
        setupGemini();

        binding.btnSend.setOnClickListener(v -> sendMessage());
        binding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void setupChat() {
        messages = new ArrayList<>();
        messages.add(new ChatMessage("Ciao! Sono il tuo Eco-Assistant. Chiedimi pure consigli su come vivere in modo più sostenibile!", ChatMessage.TYPE_GEMINI));

        adapter = new ChatAdapter(messages);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerChat.setAdapter(adapter);
    }

    private void setupGemini() {
        // Aggiornato al modello Gemini 2.5 Flash come richiesto
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    private void sendMessage() {
        String userText = binding.editMessage.getText().toString().trim();
        if (userText.isEmpty()) return;

        ChatMessage userMsg = new ChatMessage(userText, ChatMessage.TYPE_USER);
        messages.add(userMsg);
        adapter.notifyItemInserted(messages.size() - 1);
        binding.recyclerChat.scrollToPosition(messages.size() - 1);
        binding.editMessage.setText("");

        Content content = new Content.Builder()
                .addText(userText)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botText = result.getText();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addGeminiResponse(botText));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Errore di connessione o API", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }, executor);
    }

    private void addGeminiResponse(String text) {
        messages.add(new ChatMessage(text, ChatMessage.TYPE_GEMINI));
        adapter.notifyItemInserted(messages.size() - 1);
        binding.recyclerChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
