package application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Filename: FoodData.java
 * 
 * Project: p5
 * 
 * Course: cs400
 * 
 * Authors: Jason Carrington, Sarah Ostermeier, Cristian Espinoza, Brian O'Loughlin
 * 
 * Due Date: Milestone 3: 12/12/2018, Before 10:00 PM
 * 
 * Additional credits: none 
 *
 * Bugs or other notes: No known bugs
 *
 * This class represents the backend for managing all the operations associated with FoodItems
 * 
 */
public class FoodData implements FoodDataADT<FoodItem> {

    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    // THe BPTree uses value of the nutrient as key, and the associated foodItem as
    // value
    private HashMap<String, BPTree<Double, FoodItem>> indexes;

    /**
     * Public constructor
     */
    public FoodData() {
        foodItemList = new ArrayList<FoodItem>();
        indexes = new HashMap<String, BPTree<Double, FoodItem>>();
    }

    /* (non-Javadoc)
     * @see FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {

        // Initialize a BPTree with a branching factor of 3 for each nutrient
        BPTree<Double, FoodItem> calories = new BPTree<Double, FoodItem>(3);
        BPTree<Double, FoodItem> fat = new BPTree<Double, FoodItem>(3);
        BPTree<Double, FoodItem> carbohydrate = new BPTree<Double, FoodItem>(3);
        BPTree<Double, FoodItem> fiber = new BPTree<Double, FoodItem>(3);
        BPTree<Double, FoodItem> protein = new BPTree<Double, FoodItem>(3);

        try (Stream<String> wordStream = Files.lines(Paths.get(filePath))) { // Try with resources to open wordstream

            // Assign wordStream to foodItemList, filter empty lines, and map each line to a
            // FoodItem
            foodItemList = wordStream.filter(line -> line != null && line != "" && !line.startsWith(",")).map(line -> {
                String[] str = line.split(","); // Split each line into a String Array by commas

                String id = str[0]; // First element of the array is the FoodItem id
                String name = str[1]; // Second element is the name of the FoodItem

                // Assign appropriate elements of str array to corresponding nutrient variable
                Double cal_count = Double.valueOf(str[3]);
                Double fat_grams = Double.valueOf(str[5]);
                Double carb_grams = Double.valueOf(str[7]);
                Double fiber_grams = Double.valueOf(str[9]);
                Double protein_grams = Double.valueOf(str[11]);

                // Create a new FoodItem, with ID and name from above
                FoodItem newFood = new FoodItem(id, name);

                // Add each nutrient to the new FoodItem
                newFood.addNutrient("calories", cal_count);
                newFood.addNutrient("fat", fat_grams);
                newFood.addNutrient("carbohydrate", carb_grams);
                newFood.addNutrient("fiber", fiber_grams);
                newFood.addNutrient("protein", protein_grams);

                // Add the nutrient values and the new FoodItem to the corresponding BPTree
                calories.insert(cal_count, newFood);
                fat.insert(fat_grams, newFood);
                carbohydrate.insert(carb_grams, newFood);
                fiber.insert(fiber_grams, newFood);
                protein.insert(protein_grams, newFood);

                // Return the new FoodItem to foodItemList
                return newFood;

            }).collect(Collectors.toList()); // Add each line to foodItemList as a FoodItem

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sort foodItemList by name in ascending order
        Collections.sort(foodItemList, (FoodItem food1, FoodItem food2) -> food1.getName().compareTo(food2.getName()));

        // Put each BPTree in the indexes HashMap
        indexes.put("calories", calories);
        indexes.put("fat", fat);
        indexes.put("carbohydrate", carbohydrate);
        indexes.put("fiber", fiber);
        indexes.put("protein", protein);
    } // End loadFoodItems()

    
    /* (non-Javadoc)
     * @see FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {

        // Create finalList of FoodItems
        ArrayList<FoodItem> finalList = new ArrayList<FoodItem>();

        // Iterate through each item in foodItem List
        for (FoodItem food : foodItemList) {
            String temp = food.getName().toLowerCase();// Get the name of each FoodItem make it lower case
            if (temp.contains(substring)) { // If the name contains the provided substring, add it to finalList
                finalList.add(food);
            }
        }
        return finalList;
    } // End filterByName()
    

    /* (non-Javadoc)
     * @see FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        BPTree<Double, FoodItem> tree = indexes.get(rules.get(0)); // Get the BPTree associated with the input nutrient
        return tree.rangeSearch(Double.parseDouble(rules.get(2)), rules.get(1)); // rangeSearch the BPTree with the
                                                                                    // given comparator and nutrient
                                                                                    // value
    } // End filterByNutrients()


    /* (non-Javadoc)
     * @see FoodDataADT#addFoodItem(application.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        foodItemList.add(foodItem); // Add the input FoodItem to foodItemList
        Collections.sort(foodItemList, // Re-sort foodItemList by name in ascending order
                (FoodItem food1, FoodItem food2) -> food1.getName().compareTo(food2.getName()));
    } // End addFoodItem()


    /* (non-Javadoc)
     * @see FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemList;
    } // End getAllFoodItems()


    /* (non-Javadoc)
     * @see FoodDataADT#saveFoodItems(java.lang.String)
     */
    @Override
    public void saveFoodItems(String filename) {
        try {
            PrintWriter output = new PrintWriter(filename); // Create a new PrintWriter to the input filename
            for (FoodItem food : foodItemList) { // For each item in FoodItemList
                output.println(getCSVString(food)); // Print a new line with the string returned by getCSVString()
            }
            output.close(); // Close the PrintWriter
        } catch (FileNotFoundException e) { 
            System.out.println("FileNotFoundException thrown");
        }
    } // End saveFoodItems()

    
    /*
     * Helper method to convert a FoodItem object into a string, formated as a line
     * in a csv file
     * 
     * @param foodItem the food item instance to converted to a string
     */
    private String getCSVString(FoodItem foodItem) {
        
        String csvString = ""; // Initialize string
        
        // Add FoodItem information to the string
        csvString = foodItem.getID() + "," + foodItem.getName() + ",calories,"
            + Double.toString(foodItem.getNutrientValue("calories")) + ",fat,"
            + Double.toString(foodItem.getNutrientValue("fat")) + ",carbohydrate,"
            + Double.toString(foodItem.getNutrientValue("carbohydrate")) + ",fiber,"
            + Double.toString(foodItem.getNutrientValue("fiber")) + ",protein,"
            + Double.toString(foodItem.getNutrientValue("protein"));
        
        return csvString; //Return the string as a line of
    } //End getCSVString()
    
} //End FoodData class
