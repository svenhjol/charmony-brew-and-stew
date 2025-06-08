package svenhjol.charmony.brew_and_stew.common.features.elixirs;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import svenhjol.charmony.core.Charmony;

public final class Tags {
    // This loot table intentionally has no pools. A dynamic pool is added at runtime.
    public static final ResourceKey<LootTable> ELIXIRS = ResourceKey.create(Registries.LOOT_TABLE,
        Charmony.id("gameplay/elixirs"));
}
