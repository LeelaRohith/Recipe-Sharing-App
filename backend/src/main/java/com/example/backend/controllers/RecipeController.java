package com.example.backend.controllers;

import com.example.backend.database.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class RecipeController {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    Logger logger
            = LoggerFactory.getLogger(RecipeController.class);





    @PostMapping("/addrecipe/{userid}")
    public ResponseEntity<Response> addRecipe(@RequestBody Recipe recipe, @PathVariable Integer userid)
    {
        Optional<User> user = userRepository.findById(userid);
        recipe.setUser(user.get());

        Recipe savedRecipe = recipeRepository.save(recipe);

        return ResponseEntity.ok(Response.builder().text(savedRecipe.getId().toString()).build());
    }
    @PutMapping("/editrecipe/{userid}")
    public ResponseEntity<Response> editRecipe(@RequestBody editRecipeDTO recipe,@PathVariable Integer userid)
    {
        Optional<User> user = userRepository.findById(userid);

        Optional<List<Ingredients>> l = ingredientRepository.findByRecipeId(recipe.getId());
        Recipe editedRecipe = Recipe
                .builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .date(recipe.getDate())
                .description(recipe.getDescription())
                .ingredients(l.get())
                .user(user.get())
                .image(recipe.getImage())
                .cookingInstructions(recipe.getCookingInstructions())
                .build();
        Recipe savedRecipe = recipeRepository.save(editedRecipe);
        return ResponseEntity.ok(Response.builder().text(savedRecipe.getId().toString()).build());
    }
    @Transactional
    @PutMapping("/editingredients/{recipeid}")
    public ResponseEntity<Response> editIngredients(@RequestBody IngredientRequest ingredients,@PathVariable Integer recipeid)
    {
        logger.info("edit ingredients function started");
        ingredientRepository.deleteByRecipeId(recipeid);
        Optional<Recipe> recipe = recipeRepository.findById(recipeid);
        logger.info("Starting of loop");
//
        for(int i=0;i<ingredients.ingredients.size();i++)
        {
            Ingredients in =  Ingredients.builder().ingredient(ingredients.ingredients.get(i)).recipe(recipe.get()).build();
            ingredientRepository.save(in);
        }

        return ResponseEntity.ok(Response.builder().text("Ingredients Added Successfully").build());

    }
    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<Response> deleteRecipe(@RequestBody deleteRecipeDTO recipe)
    {


        ingredientRepository.deleteByRecipeId(recipe.getId());
        recipeRepository.deleteById(recipe.getId());
        return ResponseEntity.ok(Response.builder().text("Recipe Deleted").build());

    }
    @PostMapping("/addingredients/{recipeid}")
    public ResponseEntity<Response> addIngredients(@RequestBody IngredientRequest ingredients,@PathVariable Integer recipeid)
    {
        Optional<Recipe> recipe = recipeRepository.findById(recipeid);
//
        for(int i=0;i<ingredients.ingredients.size();i++)
        {
           Ingredients in =  Ingredients.builder().ingredient(ingredients.ingredients.get(i)).recipe(recipe.get()).build();
           ingredientRepository.save(in);
        }

        return ResponseEntity.ok(Response.builder().text("Ingredients Added Successfully").build());
    }


    @GetMapping("/currentuserrecipes/{id}")
    public ResponseEntity<List<RecipeDTO>> getCurrentUserRecipes(@PathVariable Integer id)
    {
        Optional<List<Recipe>> l = recipeRepository.findByUserId(id);
         List<RecipeDTO> currentUserRecipes = new ArrayList<>();
       for(int i=0;i<l.get().size();i++)
       {
           Optional<List<Ingredients>> ingredients=ingredientRepository.findByRecipeId(l.get().get(i).getId());
           l.get().get(i).setIngredients(ingredients.get());
           List<IngredientsDTO> ingredientsDTO = new ArrayList<>();
           for(int j=0;j<ingredients.get().size();j++)
           {
               ingredientsDTO.add(IngredientsDTO
                       .builder()
                       .ingredient(ingredients.get().get(j).getIngredient())
                       .build());
           }
           RecipeDTO r = RecipeDTO
                   .builder()
                   .id(l.get().get(i).getId())
                   .name(l.get().get(i).getName())
                   .date(l.get().get(i).getDate())
                   .description(l.get().get(i).getDescription())
                   .cookingInstructions(l.get().get(i).getCookingInstructions())
                   .ingredients(ingredientsDTO)
                   .image(l.get().get(i).getImage())
                   .userfirstname("user")
                   .userlastname("user")
                   .build();
           currentUserRecipes.add(r);
//           logger.info(String.valueOf(l.get().get(i).getId()));
//           logger.info(String.valueOf(l.get().get(i).getName()));
//           logger.info(String.valueOf(l.get().get(i).getDescription()));
//           logger.info(String.valueOf(l.get().get(i).getCookingInstructions()));
//           logger.info(String.valueOf(l.get().get(i).getIngredients().size()));
       }
       logger.info("Fetched current user recipes");
        return ResponseEntity.ok(currentUserRecipes);
    }
    @GetMapping("/allrecipes")
    public ResponseEntity<List<RecipeDTO>> getallRecipes()
    {
        List<Recipe> l =recipeRepository.findAll();
        List<RecipeDTO> allRecipes = new ArrayList<>();
        for(int i=0;i<l.size();i++)
        {
            Optional<List<Ingredients>> ingredients=ingredientRepository.findByRecipeId(l.get(i).getId());
            l.get(i).setIngredients(ingredients.get());
            List<IngredientsDTO> ingredientsDTO = new ArrayList<>();
            for(int j=0;j<ingredients.get().size();j++)
            {
                ingredientsDTO.add(IngredientsDTO
                        .builder()
                        .ingredient(ingredients.get().get(j).getIngredient())
                        .build());
            }
            String firstname = l.get(i).getUser().getFirstname();
            String lastname=l.get(i).getUser().getLastname();
            RecipeDTO r = RecipeDTO
                    .builder()
                    .id(l.get(i).getId())
                    .name(l.get(i).getName())
                    .date(l.get(i).getDate())
                    .description(l.get(i).getDescription())
                    .cookingInstructions(l.get(i).getCookingInstructions())
                    .ingredients(ingredientsDTO)
                    .image(l.get(i).getImage())
                    .userfirstname(firstname)
                    .userlastname(lastname)
                    .build();
            allRecipes.add(r);

        }
        logger.info("Fetched all recipes");
        return ResponseEntity.ok(allRecipes);

    }




}
