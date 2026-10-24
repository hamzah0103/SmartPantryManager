package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.data.PantryRepository;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PantryRepository pantryRepository;
    private TextView tvPantrySummary;
    private Button btnAddIngredient;
    private Button btnViewPantry;
    private Button btnSuggestedRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPantrySummary = findViewById(R.id.tvPantrySummary);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnViewPantry = findViewById(R.id.btnViewPantry);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);

        pantryRepository = new PantryRepository(this);

        btnAddIngredient.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            AddEditIngredientActivity.class
                    );

            startActivity(intent);
        });

        btnViewPantry.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v ->
                tvPantrySummary.setText(
                        "Recipe suggestions will be available soon."
                )
        );

        displayPantrySummary();
    }

    private void displayPantrySummary() {
        List<PantryItem> ingredients =
                pantryRepository.getAllIngredients();

        int count = ingredients.size();

        if (count == 0) {
            tvPantrySummary.setText(
                    "Your pantry is empty. Add an ingredient to get started."
            );
        } else {
            tvPantrySummary.setText(
                    "You have " + count +
                            (count == 1 ? " ingredient" : " ingredients") +
                            " saved in your pantry."
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayPantrySummary();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryRepository != null) {
            pantryRepository.close();
        }
    }
}