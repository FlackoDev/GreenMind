package com.example.greenmind.resource.profilo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenmind.R;
import com.example.greenmind.databinding.ItemBadgeBinding;
import com.example.greenmind.resource.model.Badge;
import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {

    private List<Badge> badges;

    public BadgeAdapter(List<Badge> badges) {
        this.badges = badges;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBadgeBinding binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BadgeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Badge badge = badges.get(position);
        holder.binding.textBadgeName.setText(badge.getName());
        
        if (badge.isSpecial()) {
            // "Vedi tutti" button
            holder.binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            holder.binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
            holder.binding.imageBadgeIcon.setImageResource(R.drawable.ic_book);
            holder.binding.getRoot().setAlpha(1.0f);
        } else {
            // Default badge style
            int trophyYellow = ContextCompat.getColor(holder.itemView.getContext(), R.color.trophy_yellow);
            
            if (badge.isUnlocked()) {
                holder.binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF9C4")));
                holder.binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(trophyYellow));
                holder.binding.getRoot().setAlpha(1.0f);
            } else {
                // Locked style: Grayscale and semi-transparent
                holder.binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
                holder.binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
                holder.binding.getRoot().setAlpha(0.5f);
            }
            holder.binding.imageBadgeIcon.setImageResource(R.drawable.ic_classifica);
        }
    }

    @Override
    public int getItemCount() {
        return badges != null ? badges.size() : 0;
    }

    public static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ItemBadgeBinding binding;

        public BadgeViewHolder(ItemBadgeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
