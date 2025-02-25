package net.anawesomguy.snowiergolems.data;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_HEAD_ITEM;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.world.item.Items.CARVED_PUMPKIN;
import static net.minecraft.world.item.Items.SNOWBALL;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(PackOutput output,
                          CompletableFuture<Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, Provider holderLookup) {
        super.buildRecipes(output, holderLookup);

        ShapelessRecipeBuilder.shapeless(MISC, CARVED_PUMPKIN)
                              .requires(GOLEM_HEAD_ITEM)
                              .unlockedBy("has_golem_head", has(CARVED_PUMPKIN))
                              .save(output, id("golem_head_to_pumpkin"));
        ShapelessRecipeBuilder.shapeless(MISC, GOLEM_HEAD_ITEM).requires(CARVED_PUMPKIN)
                              .requires(SNOWBALL)
                              .save(output, id("pumpkin_to_golem_head"));
    }
}
