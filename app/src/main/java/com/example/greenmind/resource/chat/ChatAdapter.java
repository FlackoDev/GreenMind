package com.example.greenmind.resource.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenmind.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == ChatMessage.TYPE_GEMINI) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new GeminiViewHolder(view);
        } else {
            // Layout per lo stato "sta scrivendo"
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new TypingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).textMessage.setText(message.getText());
        } else if (holder instanceof GeminiViewHolder) {
            ((GeminiViewHolder) holder).textMessage.setText(message.getText());
        } else if (holder instanceof TypingViewHolder) {
            ((TypingViewHolder) holder).textMessage.setText("Eco sta scrivendo...");
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        UserViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }

    static class GeminiViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        GeminiViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TypingViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }
}
