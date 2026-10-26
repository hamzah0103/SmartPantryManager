package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        Button btnSettings = findViewById(R.id.btnSettings);

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

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });
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

    private void checkExpiryReminders() {
        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    2001
            );

            return;
        }

        new ExpiryReminderHelper(this).checkExpiringIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayPantrySummary();
        checkExpiryReminders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryRepository != null) {
            pantryRepository.close();
        }
    }
}