package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.PantryRepository;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private PantryRepository pantryRepository;
    private PantryAdapter pantryAdapter;
    private TextView tvPantryCount;
    private RecyclerView rvPantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        tvPantryCount = findViewById(R.id.tvPantryCount);
        rvPantryItems = findViewById(R.id.rvPantryItems);

        Button btnAdd = findViewById(R.id.btnPantryAddIngredient);
        Button btnBack = findViewById(R.id.btnPantryBack);

        pantryRepository = new PantryRepository(this);

        rvPantryItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pantryRepository != null) {
            loadPantryItems();
        }
    }

    private void loadPantryItems() {
        List<PantryItem> items =
                pantryRepository.getAllIngredients();

        int count = items.size();

        tvPantryCount.setText(
                count + (count == 1
                        ? " ingredient in your pantry"
                        : " ingredients in your pantry")
        );

        pantryAdapter = new PantryAdapter(
                items,
                this::editIngredient,
                this::confirmDeleteIngredient
        );

        rvPantryItems.setAdapter(pantryAdapter);
    }

    private void editIngredient(PantryItem item) {
        Intent intent = new Intent(
                PantryActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra(
                AddEditIngredientActivity.EXTRA_ID,
                item.getId()
        );
        intent.putExtra(
                AddEditIngredientActivity.EXTRA_NAME,
                item.getName()
        );
        intent.putExtra(
                AddEditIngredientActivity.EXTRA_QUANTITY,
                item.getQuantity()
        );
        intent.putExtra(
                AddEditIngredientActivity.EXTRA_UNIT,
                item.getUnit()
        );
        intent.putExtra(
                AddEditIngredientActivity.EXTRA_EXPIRY_DATE,
                item.getExpiryDate()
        );

        startActivity(intent);
    }

    private void confirmDeleteIngredient(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getName() + "?"
                )
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted =
                            pantryRepository.deleteIngredient(item.getId());

                    if (deleted) {
                        Toast.makeText(
                                this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadPantryItems();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not delete ingredient",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryRepository != null) {
            pantryRepository.close();
            pantryRepository = null;
        }
    }
}