package com.example.greenmind.resource.learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenmind.databinding.ItemLearningContentBinding;
import com.example.greenmind.resource.model.LearningContent;

import java.util.ArrayList;
import java.util.List;

public class LearningContentAdapter extends RecyclerView.Adapter<LearningContentAdapter.VH> {

    public interface OnReadClickListener {
        void onRead(LearningContent content);
    }

    private final List<LearningContent> items = new ArrayList<>();
    private final OnReadClickListener listener;

    public LearningContentAdapter(OnReadClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<LearningContent> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLearningContentBinding b = ItemLearningContentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemLearningContentBinding b;

        VH(ItemLearningContentBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(LearningContent item, OnReadClickListener listener) {
            b.txtTitle.setText(item.getTitle());
            b.txtCategory.setText(item.getCategory());
            b.txtTime.setText(item.getReadingTimeMin() + " min");

            // Preview e immagine: dipendono dai campi che hai nel DB.
            // Se per ora non ce li hai, metto fallback.
            String preview = getPreviewSafe(item);
            b.txtPreview.setText(preview);

            int imgRes = getImageResSafe(b.getRoot().getContext(), item);
            b.imgCover.setImageResource(imgRes);

            b.btnRead.setOnClickListener(v -> {
                if (listener != null) listener.onRead(item);
            });

            b.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onRead(item);
            });
        }

        private String getPreviewSafe(LearningContent item) {
            // se NON hai preview nel model, puoi lasciare una stringa vuota oppure derivarla
            // TODO: sostituire con item.getPreview() quando aggiungi il campo
            return "Scopri di più sull’argomento e leggi una guida pratica...";
        }

        private int getImageResSafe(Context ctx, LearningContent item) {
            // TODO: se aggiungi imageResName nel model:
            // int id = ctx.getResources().getIdentifier(item.getImageResName(), "drawable", ctx.getPackageName());
            // if (id != 0) return id;

            return android.R.color.darker_gray;
        }
    }
}
