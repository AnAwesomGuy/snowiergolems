package net.anawesomguy.snowiergolems.data;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_HAT;
import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_HAT_ITEM;
import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_TOME;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.world.item.Items.BOOK;
import static net.minecraft.world.item.Items.CARVED_PUMPKIN;
import static net.minecraft.world.item.Items.SNOWBALL;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shapeless(MISC, CARVED_PUMPKIN)
            .requires(GOLEM_HAT_ITEM)
            .unlockedBy("has_golem_hat", has(GOLEM_HAT))
            .save(output, key("golem_hat_to_pumpkin"));
        shapeless(MISC, GOLEM_HAT_ITEM.defaultStackWithoutFace())
            .requires(CARVED_PUMPKIN)
            .requires(SNOWBALL)
            .unlockedBy("has_pumpkin", has(CARVED_PUMPKIN))
            .save(output, key("pumpkin_to_golem_hat"));
        shapeless(MISC, GOLEM_TOME)
            .requires(BOOK)
            .requires(SNOWBALL)
            .requires(CARVED_PUMPKIN)
            .unlockedBy("has_required",
                        inventoryTrigger(ItemPredicate.Builder.item().of(this.items, CARVED_PUMPKIN, BOOK)))
            .save(output, key("golem_tome_from_book"));
    }

    public ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, id(path));
    }

    public static class Runner extends net.minecraft.data.recipes.RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new RecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "Vanilla Recipes";
        }
    }
}
