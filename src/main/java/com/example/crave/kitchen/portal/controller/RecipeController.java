package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.dto.RecipeDto;
import com.example.crave.kitchen.portal.entity.Recipe;
import com.example.crave.kitchen.portal.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        return recipe.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDto recipeDto) {
        Recipe createdRecipe = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody RecipeDto recipeDto) {
        Optional<Recipe> updatedRecipe = recipeService.updateRecipe(id, recipeDto);
        return updatedRecipe.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        boolean deleted = recipeService.deleteRecipe(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String q) {
        List<Recipe> recipes = recipeService.searchRecipes(q);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Recipe>> searchRecipesByTitle(@RequestParam String title) {
        List<Recipe> recipes = recipeService.searchRecipesByTitle(title);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<Recipe>> getRecipesByCuisine(@PathVariable String cuisine) {
        List<Recipe> recipes = recipeService.getRecipesByCuisine(cuisine);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Recipe>> getRecipesByDifficulty(@PathVariable String difficulty) {
        List<Recipe> recipes = recipeService.getRecipesByDifficulty(difficulty);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recipe>> getRecipesByUser(@PathVariable Long userId) {
        List<Recipe> recipes = recipeService.getRecipesByUser(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/time/{maxTime}")
    public ResponseEntity<List<Recipe>> getRecipesByTotalTime(@PathVariable Integer maxTime) {
        List<Recipe> recipes = recipeService.getRecipesByTotalTime(maxTime);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/servings/{minServings}")
    public ResponseEntity<List<Recipe>> getRecipesByServings(@PathVariable Integer minServings) {
        List<Recipe> recipes = recipeService.getRecipesByServings(minServings);
        return ResponseEntity.ok(recipes);
    }
}