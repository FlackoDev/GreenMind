package com.example.greenmind.resource.classifica;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.databinding.ItemLeaderboardBinding;
import com.example.greenmind.resource.model.LeaderboardEntry;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<LeaderboardEntry> items;
    private final int currentUserId;

    public LeaderboardAdapter(List<LeaderboardEntry> items, int currentUserId) {
        this.items = items;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = items.get(position);
        holder.binding.rankText.setText(String.valueOf(entry.getPosition()));
        holder.binding.nameText.setText(entry.getUserId() == currentUserId ? entry.getUserName() + " (Tu)" : entry.getUserName());
        holder.binding.pointsText.setText(String.valueOf(entry.getPoints()));
        holder.binding.initialsText.setText(entry.getInitials());

        if (entry.getUserId() == currentUserId) {
            holder.binding.itemContainer.setBackgroundColor(Color.parseColor("#F1FBF7"));
            holder.binding.nameText.setTextColor(Color.parseColor("#19A578"));
        } else {
            holder.binding.itemContainer.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.nameText.setTextColor(Color.parseColor("#212121"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemLeaderboardBinding binding;

        public ViewHolder(ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
