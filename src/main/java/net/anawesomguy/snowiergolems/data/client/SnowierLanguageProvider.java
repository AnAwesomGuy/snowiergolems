package net.anawesomguy.snowiergolems.data.client;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static net.anawesomguy.snowiergolems.GolemObjects.*;
import static net.anawesomguy.snowiergolems.enchant.GolemEnchantments.*;

public abstract class SnowierLanguageProvider extends LanguageProvider {
    public SnowierLanguageProvider(PackOutput output, String locale) {
        super(output, SnowierGolems.MODID, locale);
    }

    protected void add(ResourceKey<Enchantment> key, String name) {
        add(Util.makeDescriptionId("enchantment", key.location()), name);
    }

    public static LanguageProvider[] getAllLanguageProviders(PackOutput output) {
        return new LanguageProvider[]{
            new EnglishUS(output),
            new ChineseTraditional(output),
            new ChineseSimplified(output)
        };
    }

    public static class EnglishUS extends SnowierLanguageProvider {
        public EnglishUS(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(GOLEM_TOME, "Enchanted Golem Tome");
            add(GOLEM_HAT, "Snow Golem Hat");
            add(ENCHANTED_SNOWBALL, "Enchanted Snowball");

            add(ACCURACY, "Accuracy");
            add(AGGRESSIVE, "Aggressive");
            add(FROST, "Frost");
            add(HEAT_RESISTANT, "Heat Resistant");
            add(SNOWY_LOYALTY, "Snowy Loyalty");

            add(PROJECTILE_ACCURACY.value().getDescriptionId(), "Projectile Accuracy");
        }
    }

    //我的中文很差，如果你比較會，請幫我開個PR，謝謝！
    public static class ChineseTraditional extends SnowierLanguageProvider {
        public ChineseTraditional(PackOutput output) {
            super(output, "zh_tw");
        }

        @Override
        protected void addTranslations() {
            add(GOLEM_TOME, "雪人附魔書");
            add(GOLEM_HAT, "雪人帽");
            add(ENCHANTED_SNOWBALL, "附魔雪球");

            add(ACCURACY, "準確度");
            add(AGGRESSIVE, "兇");
            add(FROST, "霜");
            add(HEAT_RESISTANT, "耐熱");
            add(SNOWY_LOYALTY, "雪人忠誠");

            add(PROJECTILE_ACCURACY.value().getDescriptionId(), "準確度");
        }
    }

    public static class ChineseSimplified extends SnowierLanguageProvider {
        public ChineseSimplified(PackOutput output) {
            super(output, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            add(GOLEM_TOME, "雪人附魔书");
            add(GOLEM_HAT, "雪人帽");
            add(ENCHANTED_SNOWBALL, "附魔雪球");

            add(ACCURACY, "准确度");
            add(AGGRESSIVE, "兇");
            add(FROST, "霜");
            add(HEAT_RESISTANT, "耐热");
            add(SNOWY_LOYALTY, "雪人忠诚");

            add(PROJECTILE_ACCURACY.value().getDescriptionId(), "准确度");
        }
    }
}
