
package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Filename: Main.java
 * 
 * Project: p5
 * 
 * Course: cs400
 * 
 * Authors: Jason Carrington, Sarah Ostermeier, Cristian Espinoza, Brian O'Loughlin
 * 
 * Due Date: Milestone 2: 11/30/2018
 * 
 *
 * Additional credits:
 *
 * Bugs or other notes:
 *
 */
public class Main extends Application {
    private FoodDataADT<FoodItem> foodItemList;

    public Main() {
        foodItemList = new FoodData();
    }
    private String filePath = "";
    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            primaryStage.setTitle("Food Query");

            Scene scene = new Scene(root, 960, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            // creates the save and load file options
            Menu menu = new Menu("File");// change to file chooser
            MenuItem save = new MenuItem("Save");
            MenuItem load = new MenuItem("Load");
            menu.getItems().add(0, save);
            menu.getItems().add(1, load);
            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().add(menu);
            root.setTop(menuBar);

            // right side of the root pane. Where input information goes
            BorderPane foodList = new BorderPane();
            ObservableList<String> mealItems = FXCollections.observableArrayList();
            ObservableList<FoodItem> foodItems = FXCollections.observableArrayList();
            ObservableList<String> foodItemsNames = FXCollections.observableArrayList();

            ListView selected = new ListView(mealItems);
            ListView fullList = new ListView(foodItemsNames);
            VBox mealItemsVbox = new VBox();
            VBox fullListVbox = new VBox();
            Label mealItemsLabel = new Label("Meal list");
            Label fullListLabel = new Label("Food items");
            Button clearMealButton = new Button("Clear meal");

            mealItemsVbox.getChildren().add(0, mealItemsLabel);
            mealItemsVbox.getChildren().add(1, selected);
            mealItemsVbox.getChildren().add(2, clearMealButton);
            fullListVbox.getChildren().add(0, fullListLabel);
            fullListVbox.getChildren().add(1, fullList);
            foodList.setLeft(mealItemsVbox);
            foodList.setRight(fullListVbox);

            // Contains the search and add features
            GridPane listUI = new GridPane();
            listUI.setPadding(new Insets(0, 0, 0, 100));

            TextField foodName_Value = new TextField();
            TextField nutritionInfoCal = new TextField();
            TextField nutritionInfoFat = new TextField();
            TextField nutritionInfoCarb = new TextField();
            TextField nutritionInfoFib = new TextField();
            TextField nutritionInfoPro = new TextField();
            TextField addButtonKey_Comparator = new TextField();
            Label warningLabel = new Label("Please fill in all fields");
            warningLabel.setVisible(false);
            TextField foodName = new TextField();
            foodName.setVisible(false);

            // sets the text fields' prompts
            foodName_Value.setPromptText("Food Name");
            nutritionInfoCal.setPromptText("calories(number)");
            nutritionInfoFat.setPromptText("fat(number)");
            nutritionInfoCarb.setPromptText("carbohydrates(number)");
            nutritionInfoFib.setPromptText("fiber(number)");
            nutritionInfoPro.setPromptText("protein(number)");
            addButtonKey_Comparator.setPromptText("key");
            foodName.setPromptText("Or search by food name");

            // adds the buttons
            Button addFoodButton = new Button("Add Food");
            Button filter = new Button("Filter");
            HBox addFoodButtons = new HBox();
            addFoodButtons.getChildren().add(addFoodButton);
            addFoodButtons.getChildren().add(filter);

            // adds a radio button choice
            ToggleGroup group = new ToggleGroup();
            RadioButton leftRadioButton = new RadioButton("Food");
            leftRadioButton.setToggleGroup(group);
            leftRadioButton.setSelected(true);
            RadioButton rightRadioButton = new RadioButton("Filter");
            rightRadioButton.setToggleGroup(group);

            HBox selection = new HBox();
            selection.getChildren().add(leftRadioButton);
            selection.getChildren().add(rightRadioButton);

            // adds all of the UI items into the Grid pane in their correct locations
            listUI.add(addFoodButtons, 0, 0);
            listUI.add(selection, 0, 1);
            listUI.add(warningLabel, 0, 2);
            listUI.add(foodName, 0, 3);
            listUI.add(foodName_Value, 1, 0);
            listUI.add(nutritionInfoCal, 1, 2);
            listUI.add(nutritionInfoFat, 1, 3);
            listUI.add(nutritionInfoCarb, 1, 4);
            listUI.add(nutritionInfoFib, 1, 5);
            listUI.add(nutritionInfoPro, 1, 6);
            listUI.add(addButtonKey_Comparator, 1, 1);

            foodList.setBottom(listUI);
            listUI.setAlignment(Pos.BASELINE_RIGHT);

            // create Vbox to store list manipulation buttons
            VBox arrows = new VBox();
            arrows.setAlignment(Pos.BASELINE_CENTER);
            arrows.setSpacing(10);

            Button rightArrow = new Button(">");
            Button leftArrow = new Button("<");

            arrows.getChildren().add(0, rightArrow);
            arrows.getChildren().add(1, leftArrow);

            // creates a vbox to store the analze meal button and the ouput label
            VBox analyzeVbox = new VBox();
            analyzeVbox.setAlignment(Pos.BASELINE_CENTER);
            analyzeVbox.setSpacing(30);

            Label outputInfo = new Label("Nutrition info is outputted here.");
            outputInfo.setMinHeight(100);
            Button analyzeMeal = new Button("Analyze Meal!");

            analyzeVbox.getChildren().add(0, analyzeMeal);
            analyzeVbox.getChildren().add(1, outputInfo);
            root.setCenter(analyzeVbox);

            foodList.setCenter(arrows);
            root.setRight(foodList);

            primaryStage.setScene(scene);
            primaryStage.show();

            // event handler for leftArrow
            leftArrow.setOnAction(event -> {
                String toAdd = (String) fullList.getSelectionModel().getSelectedItem();

                if (toAdd != null) {
                    fullList.getSelectionModel().clearSelection();
                    mealItems.add(toAdd);
                }
            });

            // event handler for rightArrow
            rightArrow.setOnAction(event -> {
                String toRemove = (String) selected.getSelectionModel().getSelectedItem();

                if (toRemove != null) {
                    selected.getSelectionModel().clearSelection();
                    mealItems.remove(toRemove);
                }
            });

            // event handler for leftRadioButton
            leftRadioButton.setOnAction(event -> {
                // changes the text fields' prompts according to the addFood button
                foodName_Value.setPromptText("Food Name");
                nutritionInfoCal.setPromptText("calories");
                nutritionInfoFat.setPromptText("fat");
                nutritionInfoCarb.setPromptText("carbohydrates");
                nutritionInfoFib.setPromptText("fiber");
                nutritionInfoPro.setPromptText("protein");
                addButtonKey_Comparator.setPromptText("key");
                warningLabel.setVisible(false);
                foodName.setVisible(false);
            });

            // event handler for rightRadioButton
            rightRadioButton.setOnAction(event -> {
                // changes the text Fields' prompts according to the filter button
                foodName_Value.setPromptText("value");
                addButtonKey_Comparator.setPromptText("comparator(>=, <=, ==)");
                nutritionInfoCal.setPromptText("calories");
                nutritionInfoFat.setPromptText("fat");
                nutritionInfoCarb.setPromptText("carbohydrates");
                nutritionInfoFib.setPromptText("fiber");
                nutritionInfoPro.setPromptText("protein");
                warningLabel.setText("Type y in only \n1 of the nutrients");
                warningLabel.setVisible(true);
                foodName.setVisible(true);
            });

            // event handler for addFoodButton
            addFoodButton.setOnAction(event -> {
                // if any field is empty
                if (nutritionInfoCal.getText().length() == 0
                    || nutritionInfoFat.getText().length() == 0
                    || nutritionInfoCarb.getText().length() == 0
                    || nutritionInfoFib.getText().length() == 0
                    || nutritionInfoPro.getText().length() == 0
                    || addButtonKey_Comparator.getText().length() == 0
                    || foodName_Value.getText().length() == 0) {
                    warningLabel.setText("Please fill in all fields");
                    warningLabel.setVisible(true);

                } else {
                    FoodItem newItem =
                        new FoodItem(addButtonKey_Comparator.getText(), foodName_Value.getText());
                    // adds nutrients
                    try {
                        newItem.addNutrient("calories",
                            Double.parseDouble(nutritionInfoCal.getText()));
                        newItem.addNutrient("fat", Double.parseDouble(nutritionInfoFat.getText()));
                        newItem.addNutrient("carbohydrate",
                            Double.parseDouble(nutritionInfoCarb.getText()));
                        newItem.addNutrient("fiber",
                            Double.parseDouble(nutritionInfoFib.getText()));
                        newItem.addNutrient("protein",
                            Double.parseDouble(nutritionInfoPro.getText()));

                        // adds the item to all the locations it needs to be added
                        foodItems.add(newItem);
                        foodItemList.addFoodItem(newItem);
                        // re-adds all of the foodItems into foodItems in the correct order
                        foodItems.clear();
                        foodItems.addAll(foodItemList.getAllFoodItems());

                        // updates foodItemsNames to be sorted
                        foodItemsNames.clear();
                        for (FoodItem food : foodItems) {
                            foodItemsNames.add(food.getName());
                        }
                        // clears all text input
                        nutritionInfoCal.clear();
                        nutritionInfoFat.clear();
                        nutritionInfoCarb.clear();
                        nutritionInfoFib.clear();
                        nutritionInfoPro.clear();
                        foodName_Value.clear();
                        addButtonKey_Comparator.clear();
                    } catch (IllegalArgumentException e) {
                        warningLabel.setText("Please fill in all fields");
                        warningLabel.setVisible(true);
                        System.out.println("user input was not correct");
                    }
                }
            });

            // event handler for clearMealButton
            clearMealButton.setOnAction(event -> {
                mealItems.clear();
            });

            // event handler for filter
            filter.setOnAction(event -> {
                String nutrient = null;
                Boolean isNone = false;
                List<FoodItem> filteredList = null;
                Boolean searchByName = false;
                if (!foodName.getText().equals("")) {// if user chose to filter by foodName
                    filteredList = foodItemList.filterByName(foodName.getText());
                    searchByName = true;
                    isNone = true;
                }
                // if the user chose to filter by a query then check all fields are filled
                if (!searchByName && nutritionInfoCal.getText().equals("y")) {
                    nutrient = "calories";
                } else if (!searchByName && nutritionInfoFat.getText().equals("y")) {
                    nutrient = "fat";
                } else if (!searchByName && nutritionInfoCarb.getText().equals("y")) {
                    nutrient = "carbohydrate";
                } else if (!searchByName && nutritionInfoFib.getText().equals("y")) {
                    nutrient = "fiber";
                } else if (!searchByName && nutritionInfoPro.getText().equals("y")) {
                    nutrient = "protein";
                } else {
                    isNone = true;
                }
                // all the other fields are filled
                if (foodName_Value.getText().length() == 0
                    || addButtonKey_Comparator.getText().length() == 0) {
                    isNone = true;
                }
                if (!isNone) {// if all required fields are filled then filter by nutrients
                    String value = foodName_Value.getText();
                    String comparator = addButtonKey_Comparator.getText();

                    ArrayList<String> rules = new ArrayList<String>();
                    rules.add(nutrient);
                    rules.add(comparator);
                    rules.add(value);

                    filteredList = foodItemList.filterByNutrients(rules);

                }
                foodItemsNames.clear();
                for (FoodItem food : filteredList) {
                    foodItemsNames.add(food.getName());
                }
                foodName.clear();
            });

            // event handler for load
            load.setOnAction(event -> {
                // opens up the file chooser menu
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Food Item File");
                File file = fileChooser.showOpenDialog(primaryStage);

                if (file != null) {
                    filePath = file.getPath();
                    
                    foodItemList.loadFoodItems(file.getPath());
                    foodItems.addAll(foodItemList.getAllFoodItems());
                    // clears the list of names and then reprints the sorted version
                    foodItemsNames.clear();
                    for (FoodItem food : foodItems) {
                        foodItemsNames.add(food.getName());
                    }
                }
            });

            // event handler for save
            save.setOnAction(event -> {
                // opens up the file chooser menu
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Food Item File");
                File file = fileChooser.showOpenDialog(primaryStage);

                if (file != null) {
                    foodItemList.saveFoodItems(file.getPath());
                }
            });

            // event handler for analyzeMeal
            analyzeMeal.setOnAction(event -> {
                // stores the nutrient values
                Double calories = 0.0;
                Double fat = 0.0;
                Double carbohydrate = 0.0;
                Double fiber = 0.0;
                Double protein = 0.0;

                // iterates through the selected mealItems
                for (String name : mealItems) {
                    // finds the index of of each mealItem name and stores the foodItem
                    FoodItem analyzeList = foodItems.get(foodItemsNames.indexOf(name));

                    // adds up the nutrient info
                    calories = calories + analyzeList.getNutrientValue("calories");
                    fat = fat + analyzeList.getNutrientValue("fat");
                    carbohydrate = carbohydrate + analyzeList.getNutrientValue("carbohydrate");
                    fiber = fiber + analyzeList.getNutrientValue("fiber");
                    protein = protein + analyzeList.getNutrientValue("protein");

                }
                outputInfo
                    .setText("Meal:\nCalories: " + calories + "\nFat: " + fat + "\nCarbohydrate: "
                        + carbohydrate + "\nFiber: " + fiber + "\nProtein: " + protein);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
