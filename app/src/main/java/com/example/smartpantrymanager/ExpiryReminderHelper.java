package com.example.smartpantrymanager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.smartpantrymanager.data.PantryRepository;
import com.example.smartpantrymanager.models.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class ExpiryReminderHelper {

    private static final String CHANNEL_ID = "expiry_reminders_v2";
    private static final int NOTIFICATION_ID = 1001;

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";
    private static final String KEY_LAST_EXPIRY_SIGNATURE =
            "last_expiry_notification_signature";

    private final Context context;

    public ExpiryReminderHelper(Context context) {
        this.context = context.getApplicationContext();
    }


    // Finds ingredients expiring today or within the next three days.
    // A notification is then posted when the qualifying list changes.

    public void checkExpiringIngredients() {
        SharedPreferences preferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Does nothing if the user disabled expiry reminders.
        boolean remindersEnabled =
                preferences.getBoolean(KEY_EXPIRY_REMINDERS, true);

        if (!remindersEnabled) {
            NotificationManagerCompat.from(context)
                    .cancel(NOTIFICATION_ID);

            preferences.edit()
                    .remove(KEY_LAST_EXPIRY_SIGNATURE)
                    .apply();

            return;
        }

        // Android 13 and newer will require notifications permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        List<PantryItem> pantryItems = null;

        PantryRepository repository = null;

        try {
            repository = new PantryRepository(context);
            pantryItems = repository.getAllIngredients();

        } catch (Exception e) {
            // Does not update the saved notification signature
            // if the pantry could not be loaded.
            return;

        } finally {
            if (repository != null) {
                repository.close();
            }
        }

        if (pantryItems == null) {
            return;
        }

        List<String> expiringIngredients = new ArrayList<>();
        Set<String> signatureParts = new TreeSet<>();

        Calendar today = Calendar.getInstance();
        clearTime(today);

        Calendar lastReminderDay = (Calendar) today.clone();
        lastReminderDay.add(Calendar.DAY_OF_YEAR, 3);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormat.setLenient(false);

        for (PantryItem item : pantryItems) {
            if (item == null || item.getExpiryDate() == null) {
                continue;
            }

            String expiryDate = item.getExpiryDate().trim();

            if (expiryDate.isEmpty()) {
                continue;
            }

            try {
                Date parsedDate = dateFormat.parse(expiryDate);

                if (parsedDate == null) {
                    continue;
                }

                // Ensure the date is exactly YYYY-MM-dd.
                if (!dateFormat.format(parsedDate).equals(expiryDate)) {
                    continue;
                }

                Calendar expiryCalendar = Calendar.getInstance();
                expiryCalendar.setTime(parsedDate);
                clearTime(expiryCalendar);

                boolean expiresSoon =
                        !expiryCalendar.before(today)
                                && !expiryCalendar.after(lastReminderDay);

                if (expiresSoon) {
                    String ingredientName = item.getName() == null
                            ? "Unnamed ingredient"
                            : item.getName().trim();

                    String displayText =
                            ingredientName + " — expires " + expiryDate;

                    expiringIngredients.add(displayText);

                    // Include the database ID so separately stored ingredients
                    // are distinguished, even if their names are identical.
                    String signature =
                            item.getId() + ":"
                                    + ingredientName + ":"
                                    + expiryDate;

                    signatureParts.add(signature);
                }

            } catch (ParseException e) {
                // Ignore invalid expiry dates.
            }
        }

        // No qualifying ingredients: cancel the reminder and reset its
        // signature so a future qualifying ingredient can trigger an alert.
        if (expiringIngredients.isEmpty()) {
            NotificationManagerCompat.from(context)
                    .cancel(NOTIFICATION_ID);

            preferences.edit()
                    .remove(KEY_LAST_EXPIRY_SIGNATURE)
                    .apply();

            return;
        }

        Collections.sort(expiringIngredients);

        String currentSignature = String.join("|", signatureParts);

        String previousSignature = preferences.getString(
                KEY_LAST_EXPIRY_SIGNATURE,
                ""
        );

        // The list has not changed, so do not alert repeatedly.
        if (currentSignature.equals(previousSignature)) {
            return;
        }

        // Alert only if at least one ingredient is new compared with the
        // previous qualifying list. Changes to existing ingredients update
        // the notification without making it alert again.
        Set<String> previousParts = new TreeSet<>();

        if (!previousSignature.isEmpty()) {
            Collections.addAll(
                    previousParts,
                    previousSignature.split("\\|")
            );
        }

        boolean hasNewIngredient = false;

        for (String signature : signatureParts) {
            if (!previousParts.contains(signature)) {
                hasNewIngredient = true;
                break;
            }
        }

        boolean notificationPosted = showNotification(
                expiringIngredients,
                hasNewIngredient
        );

        // Save only after the notification was successfully posted.
        if (notificationPosted) {
            preferences.edit()
                    .putString(
                            KEY_LAST_EXPIRY_SIGNATURE,
                            currentSignature
                    )
                    .apply();
        }
    }


    private boolean showNotification(
            List<String> expiringIngredients,
            boolean shouldAlert
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        createNotificationChannel();

        String contentText;

        if (expiringIngredients.size() == 1) {
            contentText = "1 ingredient is expiring soon.";
        } else {
            contentText = expiringIngredients.size()
                    + " ingredients are expiring soon.";
        }

        String expandedText = String.join("\n", expiringIngredients);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Pantry expiry reminder")
                        .setContentText(contentText)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(expandedText)
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(!shouldAlert);

        try {
            NotificationManagerCompat.from(context).notify(
                    NOTIFICATION_ID,
                    builder.build()
            );

            return true;

        } catch (SecurityException e) {
            // Permission may have changed between checking and posting.
            return false;
        }
    }


     // Creates the Android notification channel if it does not exist.

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager == null) {
                return;
            }

            NotificationChannel existingChannel =
                    manager.getNotificationChannel(CHANNEL_ID);

            if (existingChannel == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Expiry reminders",
                        NotificationManager.IMPORTANCE_HIGH
                );

                channel.setDescription(
                        "Alerts for ingredients expiring soon"
                );

                channel.setShowBadge(true);

                manager.createNotificationChannel(channel);
            }
        }
    }


    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
