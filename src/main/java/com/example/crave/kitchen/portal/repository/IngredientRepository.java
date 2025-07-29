package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByNameIgnoreCase(String name);

    List<Ingredient> findByNameContainingIgnoreCase(String name);

    List<Ingredient> findByCategoryIgnoreCase(String category);

    @Query("SELECT i FROM Ingredient i WHERE i.caloriesPerUnit <= :maxCalories")
    List<Ingredient> findByCaloriesLessThanOrEqualTo(@Param("maxCalories") Double maxCalories);

    @Query("SELECT i FROM Ingredient i WHERE i.caloriesPerUnit >= :minCalories")
    List<Ingredient> findByCaloriesGreaterThanOrEqualTo(@Param("minCalories") Double minCalories);

    boolean existsByNameIgnoreCase(String name);
}