package com.example.greenmind.resource.profilo;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenmind.R;
import com.example.greenmind.databinding.DialogBadgeDetailBinding;
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
        if (badge == null) return;

        holder.binding.textBadgeName.setText(badge.getName());
        
        int trophyYellow = ContextCompat.getColor(holder.itemView.getContext(), R.color.trophy_yellow);
        holder.binding.textBadgePoints.setVisibility(View.VISIBLE);
        holder.binding.textBadgePoints.setText(badge.getRequiredPoints() + " pts");

        if (badge.isUnlocked()) {
            holder.binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_green_bg)));
            holder.binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(trophyYellow));
            holder.binding.getRoot().setAlpha(1.0f);
        } else {
            holder.binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.id.navigation_home == 0 ? R.color.divider_color : R.color.input_background))); // Use existing neutral background
            holder.binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_tertiary)));
            holder.binding.getRoot().setAlpha(0.5f);
        }
        holder.binding.imageBadgeIcon.setImageResource(R.drawable.ic_classifica);
        
        holder.binding.getRoot().setOnClickListener(v -> showBadgeDialog(v.getContext(), badge));
    }

    private void showBadgeDialog(android.content.Context context, Badge badge) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogBadgeDetailBinding binding = DialogBadgeDetailBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        binding.textBadgeName.setText(badge.getName());
        binding.textBadgeDescription.setText(badge.getDescription());
        binding.textRequiredPoints.setText("Richiede " + badge.getRequiredPoints() + " pts");

        int trophyYellow = ContextCompat.getColor(context, R.color.trophy_yellow);
        if (badge.isUnlocked()) {
            binding.textBadgeStatus.setText("SBLOCCATO");
            binding.textBadgeStatus.setTextColor(ContextCompat.getColor(context, R.color.green_primary));
            binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_green_bg)));
            binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(trophyYellow));
        } else {
            binding.textBadgeStatus.setText("BLOCCATO");
            binding.textBadgeStatus.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary));
            binding.badgeIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.input_background)));
            binding.imageBadgeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_tertiary)));
        }

        binding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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
