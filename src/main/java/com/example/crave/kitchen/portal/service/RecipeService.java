package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.dto.RecipeDto;
import com.example.crave.kitchen.portal.entity.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeService {

    List<Recipe> getAllRecipes();

    Optional<Recipe> getRecipeById(Long id);

    Recipe createRecipe(RecipeDto recipeDto);

    Optional<Recipe> updateRecipe(Long id, RecipeDto recipeDto);

    boolean deleteRecipe(Long id);

    List<Recipe> searchRecipesByTitle(String title);

    List<Recipe> getRecipesByCuisine(String cuisine);

    List<Recipe> getRecipesByDifficulty(String difficulty);

    List<Recipe> getRecipesByUser(Long userId);

    List<Recipe> getRecipesByTotalTime(Integer maxTime);

    List<Recipe> getRecipesByServings(Integer minServings);

    List<Recipe> searchRecipes(String searchTerm);
}