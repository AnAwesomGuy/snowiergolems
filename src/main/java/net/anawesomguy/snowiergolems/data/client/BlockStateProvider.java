package net.anawesomguy.snowiergolems.data.client;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SnowierGolems.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // should i really be using datagen
        simpleBlockWithItem(GolemObjects.GOLEM_HAT,
                            models().getBuilder(GolemObjects.GOLEM_HAT_ID.toString())
                                    .parent(new UncheckedModelFile("builtin/entity"))
                                    .texture("particle", mcLoc("block/carved_pumpkin"))
                                    .transforms()

                                    .transform(ItemDisplayContext.GUI)
                                    .rotation(30F, 45F, 0F)
                                    .scale(0.625F)
                                    .end()

                                    .transform(ItemDisplayContext.GROUND)
                                    .translation(0F, 3F, 0F)
                                    .scale(0.25F)
                                    .end()

                                    .transform(ItemDisplayContext.HEAD)
                                    .rotation(0F, 180F, 0F)
                                    .end()

                                    .transform(ItemDisplayContext.FIXED)
                                    .rotation(0F, 180F, 0F)
                                    .scale(0.5F)
                                    .end()

                                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                                    .rotation(75F, 315F, 0F)
                                    .translation(0F, 2.5F, 0F)
                                    .scale(0.375F)
                                    .end()

                                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                                    .rotation(0F, 315F, 0F)
                                    .scale(0.4F)
                                    .end()

                                    .end());
    }
}
