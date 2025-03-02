package net.anawesomguy.snowiergolems.data.client;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SnowierGolems.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(GolemObjects.GOLEM_HEAD, models().getBuilder(GolemObjects.GOLEM_HEAD_ID.toString())
                                                             .texture("particle", "minecraft:block/carved_pumpkin"));

    }
}
