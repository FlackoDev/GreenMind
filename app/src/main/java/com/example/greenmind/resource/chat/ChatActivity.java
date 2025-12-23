package com.example.greenmind.resource.chat;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenmind.databinding.FragmentChatBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends BottomSheetDialogFragment {

    private static final String TAG = "ChatActivity";
    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    // API KEY aggiornata
    private static final String API_KEY = "AIzaSyBEcsAwZTIYZXxlyZiT-GFJhNYsS7Vvd-w";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(layoutParams);

                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });
        return dialog;
    }

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
        messages.add(new ChatMessage("Ciao! Sono Eco, il tuo assistente basato su Gemini 2.5 Flash. Come posso aiutarti?", ChatMessage.TYPE_GEMINI));
        adapter = new ChatAdapter(messages);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerChat.setAdapter(adapter);
    }

    private void setupGemini() {
        try {
            GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
            model = GenerativeModelFutures.from(gm);
        } catch (Exception e) {
            Log.e(TAG, "Errore Setup AI: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String userText = binding.editMessage.getText().toString().trim();
        if (userText.isEmpty()) return;

        // 1. Aggiungi messaggio utente
        messages.add(new ChatMessage(userText, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messages.size() - 1);
        binding.recyclerChat.scrollToPosition(messages.size() - 1);
        binding.editMessage.setText("");

        // 2. Aggiungi stato "sta scrivendo"
        ChatMessage typingMsg = new ChatMessage("", ChatMessage.TYPE_TYPING);
        messages.add(typingMsg);
        int typingPos = messages.size() - 1;
        adapter.notifyItemInserted(typingPos);
        binding.recyclerChat.scrollToPosition(messages.size() - 1);

        if (model == null) {
            removeTypingAndShowError(typingPos, "AI non pronta.");
            return;
        }

        Content content = new Content.Builder().addText(userText).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botText = result.getText();
                if (botText == null || botText.isEmpty()) botText = "[Nessuna risposta]";
                
                final String finalBotText = botText;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        removeTyping(typingPos);
                        addGeminiResponse(finalBotText);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        removeTyping(typingPos);
                        showError(t.getMessage());
                    });
                }
            }
        }, executor);
    }

    private void removeTyping(int position) {
        if (position < messages.size() && messages.get(position).getType() == ChatMessage.TYPE_TYPING) {
            messages.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    private void removeTypingAndShowError(int position, String error) {
        removeTyping(position);
        showError(error);
    }

    private void showError(String error) {
        String msg = "Problema di connessione.";
        if (error != null && error.contains("resolve host")) {
            msg = "Eco non raggiunge internet. Verifica la connessione.";
        }
        addGeminiResponse(msg);
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
