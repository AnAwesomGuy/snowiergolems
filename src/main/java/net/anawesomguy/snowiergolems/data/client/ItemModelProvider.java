package net.anawesomguy.snowiergolems.data.client;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SnowierGolems.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(GolemObjects.GOLEM_TOME);
    }
}
