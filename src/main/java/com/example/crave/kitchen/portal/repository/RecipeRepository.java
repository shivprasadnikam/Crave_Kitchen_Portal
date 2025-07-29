package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByTitleContainingIgnoreCase(String title);

    List<Recipe> findByCuisineIgnoreCase(String cuisine);

    List<Recipe> findByDifficultyIgnoreCase(String difficulty);

    List<Recipe> findByCreatedById(Long userId);

    @Query("SELECT r FROM Recipe r WHERE r.prepTime + r.cookTime <= :maxTime")
    List<Recipe> findByTotalTimeLessThanOrEqualTo(@Param("maxTime") Integer maxTime);

    @Query("SELECT r FROM Recipe r WHERE r.servings >= :minServings")
    List<Recipe> findByServingsGreaterThanOrEqualTo(@Param("minServings") Integer minServings);

    List<Recipe> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}