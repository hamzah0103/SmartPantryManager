package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.data.PantryRepository;
import com.example.smartpantrymanager.models.PantryItem;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "ingredient_id";
    public static final String EXTRA_NAME = "ingredient_name";
    public static final String EXTRA_QUANTITY = "ingredient_quantity";
    public static final String EXTRA_UNIT = "ingredient_unit";
    public static final String EXTRA_EXPIRY_DATE = "ingredient_expiry_date";

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;
    private TextView tvFormTitle;

    private PantryRepository pantryRepository;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        pantryRepository = new PantryRepository(this);

        if (getIntent().hasExtra(EXTRA_ID)) {
            loadIngredientForEditing();
        }

        btnSave.setOnClickListener(v -> saveIngredient());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadIngredientForEditing() {
        ingredientId = getIntent().getIntExtra(EXTRA_ID, -1);

        tvFormTitle.setText("Edit Ingredient");
        etIngredientName.setText(
                getIntent().getStringExtra(EXTRA_NAME)
        );
        etQuantity.setText(
                String.valueOf(getIntent().getDoubleExtra(EXTRA_QUANTITY, 0))
        );
        etUnit.setText(
                getIntent().getStringExtra(EXTRA_UNIT)
        );
        etExpiryDate.setText(
                getIntent().getStringExtra(EXTRA_EXPIRY_DATE)
        );
    }

    private void saveIngredient() {
        String name = etIngredientName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        if (name.isEmpty()) {
            etIngredientName.setError("Enter an ingredient name");
            etIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etQuantity.setError("Enter a quantity");
            etQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            etUnit.setError("Enter a unit");
            etUnit.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);

            if (!Double.isFinite(quantity) || quantity <= 0) {
                etQuantity.setError("Quantity must be greater than zero");
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        if (!expiryDate.isEmpty() && !isValidExpiryDate(expiryDate)) {
            etExpiryDate.setError("Enter a valid date in YYYY-MM-DD format");
            etExpiryDate.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(
                ingredientId,
                name,
                quantity,
                unit,
                expiryDate.isEmpty() ? null : expiryDate
        );

        boolean saved;

        if (ingredientId == -1) {
            saved = pantryRepository.addIngredient(item);
        } else {
            saved = pantryRepository.updateIngredient(item);
        }

        if (saved) {
            Toast.makeText(
                    this,
                    ingredientId == -1
                            ? "Ingredient added"
                            : "Ingredient updated",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        } else {
            Toast.makeText(
                    this,
                    "Could not save the ingredient",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private boolean isValidExpiryDate(String dateText) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateFormat.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        java.util.Date parsedDate = dateFormat.parse(dateText, position);

        return parsedDate != null
                && position.getIndex() == dateText.length();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryRepository != null) {
            pantryRepository.close();
        }
    }
}