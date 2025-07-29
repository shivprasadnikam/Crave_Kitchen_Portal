package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Long id);

    Category createCategory(Category category);

    Optional<Category> updateCategory(Long id, Category category);

    boolean deleteCategory(Long id);

    Optional<Category> findByName(String name);

    List<Category> searchCategoriesByName(String name);

    boolean existsByName(String name);
}