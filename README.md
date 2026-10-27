Smart Pantry Manager Project

1.Project Description

Smart Pantry Manager is an Android application using the Java language and xml layout in this practical assignment.

The app lets users organize the ingredients that they already have in their homes, 
keep track of the expiration dates and find recipes they can cook with the items that are currently in their pantry. 
It is primarily used to minimize food wastage and optimize the available food resources.

2. Features

Pantry Management
Add pantry items including the name, quantity, unit, and (optional) expiry date.
- Sees what ingredients are available in the pantry.
- Edit ingredient details.
- Delete ingredients.
- Checks the ingredients that the user entered before they are saved.

Recipe Collection
View a library of preloaded recipes.
- Reads the list of ingredients and directions for the recipe.
- Locally cache recipe information in SQLite.

Suggested Recipes
- Find recipes that can be prepared and made using the available pantry ingredients.
- Has strict ingredient matching (recipes that cannot be created because unavailable ingredients are not included).
- Make comparisons between quantities and justify some typical differences between units.
- Deal with easy variations of ingredient names such as singular and plural.

Settings
- Select Metric or Imperial units preference.
- Set up reminder times for expirations.

Data Persistence
- Store local recipe information and pantry ingredients in a local SQLite database.
- Persist information even when app closes and reopens.

3. Technologies Used

- Programming language: Java
- Android version: 10.1
- User interface: XML layouts
- Database: SQLite
- Database helper: SQLiteOpenHelper
  Adapters are used with RecyclerView to implement dynamic lists.To implement dynamic lists, use RecyclerView with a custom adapter.
  Android Activities and Intents –Navigation
  Version control was introduced with Git and GitHub.Version control: Git and GitHub.

4. Database Choice

SQLite was chosen because it is a local persistent store that doesn't need a separate server or Internet connection.

SQLite is used for the app to cache pantry ingredients and recipe information. 
The database is managed by SQLiteOpenHelper to create and upgrade the database and the data operations are handled by repository classes.

It is suitable for an application that would have to manage a user's personal pantry in their Android phone.

5. Project Structure

The project separates its functionality into different classes and packages.

AddEditIngredientActivity.java — adds and edits ingredients to the pantry.
- PantryActivity.java — shows ingredients of the pantry.
- PantryAdapter.java — adapts data from pantry to be used in the list interface.
- RecipeDetailActivity.java - This class displays the ingredients and instructions of the recipe.
- RecipeMatcher.java - Looks up which recipes can be made with the available ingredients.
- RecipeRepository.java – Used to retrieve recipe information.
- PantryRepository.java — provides operations for the pantry data.
- The DatabaseHelper.java - creates and upgrades the SQLite database.
- UnitConverter.java — converts quantities displayed in metric or imperial units.
- SettingsActivity.java — deals with preferences for the application.

6. Requirements

The following is required to open and operate the project:

- Android Studio.
- Android SDK installed in Android Studio which is compatible.
- An Android emulator or an Android device that is compatible.

7. Set up and run instructions

- Download the repository from GitHub, or clone it.
- Open Android Studio.
- Click on Open, and navigate to the `SmartPantryManager` project folder.
- Wait for the Gradle project to be synced.
- Add any missing Android SDK components. 
- Choose an Android emulator or plug in a physical device that is compatible.
- Run the application from the Android Studio with the `Run` button.

A local sqlite database is used for the application. The device is used to store pantry ingredients and recipes.

8. GitHub Repository

Repository: https://github.com/hamzah0103/SmartPantryManager

9. Assignment Information

This assignment Smart Pantry Manager Project is an example of building an Android application by using Java, XML layouts, Activities and Intents, adapters, SQLite persistence, 
input validation, and recipe-matching logic.