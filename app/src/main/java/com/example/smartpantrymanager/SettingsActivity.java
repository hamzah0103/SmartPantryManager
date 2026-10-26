package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgUnitPreference;
    private Switch swExpiryReminders;
    private SharedPreferences preferences;

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_UNIT = "unit_preference";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rgUnitPreference = findViewById(R.id.rgUnitPreference);
        swExpiryReminders = findViewById(R.id.swExpiryReminders);

        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);
        Button btnSettingsBack = findViewById(R.id.btnSettingsBack);

        preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());

        btnSettingsBack.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        String unit = preferences.getString(KEY_UNIT, "metric");
        boolean remindersEnabled = preferences.getBoolean(
                KEY_EXPIRY_REMINDERS,
                true
        );

        if ("imperial".equals(unit)) {
            rgUnitPreference.check(R.id.rbImperial);
        } else {
            rgUnitPreference.check(R.id.rbMetric);
        }

        swExpiryReminders.setChecked(remindersEnabled);
    }

    private void saveSettings() {
        int selectedUnitId = rgUnitPreference.getCheckedRadioButtonId();

        if (selectedUnitId == -1) {
            Toast.makeText(
                    this,
                    "Please select a measurement system.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String unit = selectedUnitId == R.id.rbImperial
                ? "imperial"
                : "metric";

        boolean remindersEnabled = swExpiryReminders.isChecked();

        preferences.edit()
                .putString(KEY_UNIT, unit)
                .putBoolean(KEY_EXPIRY_REMINDERS, remindersEnabled)
                .apply();

        Toast.makeText(
                this,
                "Settings saved successfully.",
                Toast.LENGTH_SHORT
        ).show();
    }
}