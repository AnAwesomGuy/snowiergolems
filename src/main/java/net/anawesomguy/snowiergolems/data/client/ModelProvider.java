package net.anawesomguy.snowiergolems.data.client;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.client.GolemHatSpecialRenderer;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNullByDefault;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class ModelProvider extends net.minecraft.client.data.models.ModelProvider {
    public ModelProvider(PackOutput output) {
        super(output, SnowierGolems.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(GolemObjects.GOLEM_TOME, ModelTemplates.FLAT_ITEM);

        blockModels.createParticleOnlyBlock(GolemObjects.GOLEM_HAT, Blocks.CARVED_PUMPKIN);
        blockModels.generateSimpleSpecialItemModel(GolemObjects.GOLEM_HAT, GolemHatSpecialRenderer.Unbaked.INSTANCE);
    }
}
