package com.example.crave.kitchen.portal.impl;

import com.example.crave.kitchen.portal.entity.Ingredient;
import com.example.crave.kitchen.portal.repository.IngredientRepository;
import com.example.crave.kitchen.portal.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Override
    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    @Override
    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Optional<Ingredient> updateIngredient(Long id, Ingredient ingredient) {
        return ingredientRepository.findById(id)
                .map(existingIngredient -> {
                    existingIngredient.setName(ingredient.getName());
                    existingIngredient.setDescription(ingredient.getDescription());
                    existingIngredient.setUnit(ingredient.getUnit());
                    existingIngredient.setCaloriesPerUnit(ingredient.getCaloriesPerUnit());
                    existingIngredient.setCategory(ingredient.getCategory());
                    return ingredientRepository.save(existingIngredient);
                });
    }

    @Override
    public boolean deleteIngredient(Long id) {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Ingredient> findByName(String name) {
        return ingredientRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Ingredient> searchIngredientsByName(String name) {
        return ingredientRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Ingredient> findByCategory(String category) {
        return ingredientRepository.findByCategoryIgnoreCase(category);
    }

    @Override
    public List<Ingredient> findByCaloriesLessThanOrEqualTo(Double maxCalories) {
        return ingredientRepository.findByCaloriesLessThanOrEqualTo(maxCalories);
    }

    @Override
    public List<Ingredient> findByCaloriesGreaterThanOrEqualTo(Double minCalories) {
        return ingredientRepository.findByCaloriesGreaterThanOrEqualTo(minCalories);
    }

    @Override
    public boolean existsByName(String name) {
        return ingredientRepository.existsByNameIgnoreCase(name);
    }
}