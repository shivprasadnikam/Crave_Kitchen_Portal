package com.example.crave.kitchen.portal.impl;

import com.example.crave.kitchen.portal.dto.RecipeDto;
import com.example.crave.kitchen.portal.entity.Recipe;
import com.example.crave.kitchen.portal.entity.User;
import com.example.crave.kitchen.portal.repository.RecipeRepository;
import com.example.crave.kitchen.portal.repository.UserRepository;
import com.example.crave.kitchen.portal.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    @Override
    public Recipe createRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setIngredients(recipeDto.getIngredients());
        recipe.setInstructions(recipeDto.getInstructions());
        recipe.setPrepTime(recipeDto.getPrepTime());
        recipe.setCookTime(recipeDto.getCookTime());
        recipe.setServings(recipeDto.getServings());
        recipe.setDifficulty(recipeDto.getDifficulty());
        recipe.setCuisine(recipeDto.getCuisine());

        // Set the creator if provided
        if (recipeDto.getCreatedById() != null) {
            Optional<User> user = userRepository.findById(recipeDto.getCreatedById());
            user.ifPresent(recipe::setCreatedBy);
        }

        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> updateRecipe(Long id, RecipeDto recipeDto) {
        return recipeRepository.findById(id)
                .map(recipe -> {
                    recipe.setTitle(recipeDto.getTitle());
                    recipe.setDescription(recipeDto.getDescription());
                    recipe.setIngredients(recipeDto.getIngredients());
                    recipe.setInstructions(recipeDto.getInstructions());
                    recipe.setPrepTime(recipeDto.getPrepTime());
                    recipe.setCookTime(recipeDto.getCookTime());
                    recipe.setServings(recipeDto.getServings());
                    recipe.setDifficulty(recipeDto.getDifficulty());
                    recipe.setCuisine(recipeDto.getCuisine());
                    return recipeRepository.save(recipe);
                });
    }

    @Override
    public boolean deleteRecipe(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Recipe> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Recipe> getRecipesByCuisine(String cuisine) {
        return recipeRepository.findByCuisineIgnoreCase(cuisine);
    }

    @Override
    public List<Recipe> getRecipesByDifficulty(String difficulty) {
        return recipeRepository.findByDifficultyIgnoreCase(difficulty);
    }

    @Override
    public List<Recipe> getRecipesByUser(Long userId) {
        return recipeRepository.findByCreatedById(userId);
    }

    @Override
    public List<Recipe> getRecipesByTotalTime(Integer maxTime) {
        return recipeRepository.findByTotalTimeLessThanOrEqualTo(maxTime);
    }

    @Override
    public List<Recipe> getRecipesByServings(Integer minServings) {
        return recipeRepository.findByServingsGreaterThanOrEqualTo(minServings);
    }

    @Override
    public List<Recipe> searchRecipes(String searchTerm) {
        return recipeRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm,
                searchTerm);
    }
}