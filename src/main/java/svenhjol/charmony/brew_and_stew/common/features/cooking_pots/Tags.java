package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import svenhjol.charmony.core.Charmony;

public final class Tags {
    public static final TagKey<Block> COOKING_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
        Charmony.id("cooking_heat_sources"));
}
