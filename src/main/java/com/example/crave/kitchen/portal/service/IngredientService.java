package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.entity.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientService {

    List<Ingredient> getAllIngredients();

    Optional<Ingredient> getIngredientById(Long id);

    Ingredient createIngredient(Ingredient ingredient);

    Optional<Ingredient> updateIngredient(Long id, Ingredient ingredient);

    boolean deleteIngredient(Long id);

    Optional<Ingredient> findByName(String name);

    List<Ingredient> searchIngredientsByName(String name);

    List<Ingredient> findByCategory(String category);

    List<Ingredient> findByCaloriesLessThanOrEqualTo(Double maxCalories);

    List<Ingredient> findByCaloriesGreaterThanOrEqualTo(Double minCalories);

    boolean existsByName(String name);
}