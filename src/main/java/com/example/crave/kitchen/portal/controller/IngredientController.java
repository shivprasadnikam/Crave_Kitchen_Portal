package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.entity.Ingredient;
import com.example.crave.kitchen.portal.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Optional<Ingredient> ingredient = ingredientService.getIngredientById(id);
        return ingredient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        Ingredient createdIngredient = ingredientService.createIngredient(ingredient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredient) {
        Optional<Ingredient> updatedIngredient = ingredientService.updateIngredient(id, ingredient);
        return updatedIngredient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        boolean deleted = ingredientService.deleteIngredient(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ingredient>> searchIngredients(@RequestParam String name) {
        List<Ingredient> ingredients = ingredientService.searchIngredientsByName(name);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Ingredient>> getIngredientsByCategory(@PathVariable String category) {
        List<Ingredient> ingredients = ingredientService.findByCategory(category);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/calories/max/{maxCalories}")
    public ResponseEntity<List<Ingredient>> getIngredientsByMaxCalories(@PathVariable Double maxCalories) {
        List<Ingredient> ingredients = ingredientService.findByCaloriesLessThanOrEqualTo(maxCalories);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/calories/min/{minCalories}")
    public ResponseEntity<List<Ingredient>> getIngredientsByMinCalories(@PathVariable Double minCalories) {
        List<Ingredient> ingredients = ingredientService.findByCaloriesGreaterThanOrEqualTo(minCalories);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Ingredient> getIngredientByName(@PathVariable String name) {
        Optional<Ingredient> ingredient = ingredientService.findByName(name);
        return ingredient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}