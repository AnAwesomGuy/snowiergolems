package net.anawesomguy.snowiergolems.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.snowiergolems.GolemObjects.*;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.world.item.Items.*;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(PackOutput output,
                          CompletableFuture<Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, Provider holderLookup) {
        super.buildRecipes(output, holderLookup);

        ShapelessRecipeBuilder.shapeless(MISC, CARVED_PUMPKIN)
                              .requires(GOLEM_HAT_ITEM)
                              .unlockedBy("has_golem_hat", has(GOLEM_HAT))
                              .save(output, id("golem_hat_to_pumpkin"));
        ShapelessRecipeBuilder.shapeless(MISC, GOLEM_HAT_ITEM.defaultStackWithoutFace())
                              .requires(CARVED_PUMPKIN)
                              .requires(SNOWBALL)
                              .unlockedBy("has_snowball_and_pumpkin",
                                          inventoryTrigger(ItemPredicate.Builder.item().of(CARVED_PUMPKIN, SNOWBALL)))
                              .save(output, id("pumpkin_to_golem_hat"));
        ShapelessRecipeBuilder.shapeless(MISC, GOLEM_TOME)
                              .requires(BOOK)
                              .requires(SNOWBALL)
                              .requires(CARVED_PUMPKIN)
                              .unlockedBy("has_required",
                                          inventoryTrigger(ItemPredicate.Builder.item().of(CARVED_PUMPKIN, SNOWBALL, BOOK)))
                              .save(output, id("golem_tome_from_book"));
    }
}
