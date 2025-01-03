package svenhjol.charmony.cooking.common.features.cooking_pots;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import svenhjol.charmony.cooking.CookingMod;

public final class Tags {
    public static final TagKey<Block> COOKING_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
        CookingMod.id("cooking_heat_sources"));
}
