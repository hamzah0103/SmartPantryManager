package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.models.PantryItem;

import java.util.List;
import java.util.Locale;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnEditClickListener {
        void onEditClick(PantryItem item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(PantryItem item);
    }

    private final List<PantryItem> pantryItems;
    private final OnEditClickListener editListener;
    private final OnDeleteClickListener deleteListener;
    private final Context context;

    public PantryAdapter(
            Context context,
            List<PantryItem> pantryItems,
            OnEditClickListener editListener,
            OnDeleteClickListener deleteListener) {

        this.context = context;
        this.pantryItems = pantryItems;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder, int position) {

        PantryItem item = pantryItems.get(position);

        holder.tvIngredientName.setText(item.getName());
        SharedPreferences preferences = context.getSharedPreferences(
                "smart_pantry_settings",
                Context.MODE_PRIVATE
        );

        String unitPreference = preferences.getString(
                "unit_preference",
                "metric"
        );

        String displayQuantity = UnitConverter.convertForDisplay(
                item.getQuantity(),
                item.getUnit(),
                unitPreference
        );

        holder.tvIngredientQuantity.setText(displayQuantity);

        String expiryDate = item.getExpiryDate();

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            holder.tvIngredientExpiry.setText("Expiry: Not specified");
        } else {
            holder.tvIngredientExpiry.setText("Expiry: " + expiryDate);
        }

        holder.btnEdit.setOnClickListener(v ->
                editListener.onEditClick(item));

        holder.btnDelete.setOnClickListener(v ->
                deleteListener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvIngredientQuantity;
        TextView tvIngredientExpiry;
        Button btnEdit;
        Button btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIngredientName =
                    itemView.findViewById(R.id.tvIngredientName);
            tvIngredientQuantity =
                    itemView.findViewById(R.id.tvIngredientQuantity);
            tvIngredientExpiry =
                    itemView.findViewById(R.id.tvIngredientExpiry);
            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}