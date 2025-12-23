package com.example.greenmind.resource.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.greenmind.databinding.ActivityChatBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private GenerativeModelFutures model;

    // INSERISCI QUI LA TUA API KEY
    private static final String API_KEY = "IL_TUO_PLACEHOLDER_QUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChat();
        setupGemini();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupChat() {
        messages = new ArrayList<>();
        messages.add(new ChatMessage("Ciao! Sono il tuo Eco-Assistant. Chiedimi pure consigli su come vivere in modo più sostenibile!", ChatMessage.TYPE_GEMINI));
        
        adapter = new ChatAdapter(messages);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChat.setAdapter(adapter);
    }

    private void setupGemini() {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    private void sendMessage() {
        String userText = binding.editMessage.getText().toString().trim();
        if (userText.isEmpty()) return;

        // Aggiungi messaggio utente alla lista
        ChatMessage userMsg = new ChatMessage(userText, ChatMessage.TYPE_USER);
        messages.add(userMsg);
        adapter.notifyItemInserted(messages.size() - 1);
        binding.recyclerChat.scrollToPosition(messages.size() - 1);
        binding.editMessage.setText("");

        // Chiamata a Gemini
        if (API_KEY.equals("IL_TUO_PLACEHOLDER_QUI")) {
            addGeminiResponse("Ehi! Sembra che tu non abbia ancora inserito l'API KEY nel codice di ChatActivity. Inseriscila per iniziare a parlare davvero con me!");
            return;
        }

        Content content = new Content.Builder()
                .addText(userText)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botText = result.getText();
                runOnUiThread(() -> addGeminiResponse(botText));
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                });
            }
        }, executor);
    }

    private void addGeminiResponse(String text) {
        messages.add(new ChatMessage(text, ChatMessage.TYPE_GEMINI));
        adapter.notifyItemInserted(messages.size() - 1);
        binding.recyclerChat.scrollToPosition(messages.size() - 1);
    }
}
