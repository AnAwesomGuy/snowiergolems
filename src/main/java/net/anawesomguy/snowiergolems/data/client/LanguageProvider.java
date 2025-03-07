package net.anawesomguy.snowiergolems.data.client;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import static net.anawesomguy.snowiergolems.GolemObjects.*;

public class LanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {
    public LanguageProvider(PackOutput output) {
        super(output, SnowierGolems.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ENCHANTED_SNOWBALL, "Enchanted Snowball");
        add(GOLEM_TOME, "Enchanted Golem Tome");
        add(GOLEM_HAT, "Snow Golem Hat");
        add(GolemEnchantments.ACCURACY, "Accuracy");
        add(GolemEnchantments.FROST, "Frost");
        add(GolemEnchantments.PROJECTILE_ACCURACY.value().getDescriptionId(), "Projectile Accuracy");
    }

    protected void add(ResourceKey<Enchantment> key, String name) {
        add(Util.makeDescriptionId("enchantment", key.location()), name);
    }
}
