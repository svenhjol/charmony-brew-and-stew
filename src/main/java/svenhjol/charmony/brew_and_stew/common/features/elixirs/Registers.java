package svenhjol.charmony.brew_and_stew.common.features.elixirs;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import svenhjol.charmony.api.elixirs.ElixirDefinition;
import svenhjol.charmony.api.elixirs.ElixirDefinitionProvider;
import svenhjol.charmony.api.elixirs.ElixirsApi;
import svenhjol.charmony.brew_and_stew.common.features.elixirs.loot_functions.ElixirLootFunction;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registers extends Setup<Elixirs> {
    public final Map<String, ElixirDefinition> elixirDefinitions = new HashMap<>();
    public final Supplier<LootItemFunctionType<? extends LootItemConditionalFunction>> elixirLootFunction;

    public Registers(Elixirs feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        elixirLootFunction = registry.lootFunctionType(Constants.ELIXIR_LOOT,
            () -> new LootItemFunctionType<>(ElixirLootFunction.CODEC));

        Api.consume(ElixirDefinitionProvider.class, provider -> {
            for (var def : provider.getElixirDefinitions()) {
                elixirDefinitions.put(def.id(), def);
            }
        });
    }

    @Override
    public Runnable boot() {
        return () -> {
            // Implementation to get an elixir from a given definition ID.
            ElixirsApi.instance().setElixirImpl((id, provider, random) -> {
                var def = elixirDefinitions.getOrDefault(id, null);
                if (def == null) return ItemStack.EMPTY;
                return feature().handlers.createElixirItem(def, provider, random);
            });

            ElixirsApi.instance().setRandomElixirImpl((provider, random) -> {
                var defs = new ArrayList<>(elixirDefinitions.values());
                if (defs.isEmpty()) return ItemStack.EMPTY;

                Util.shuffle(defs, random);
                var def = defs.getFirst();
                return feature().handlers.createElixirItem(def, provider, random);
            });

            LootTableEvents.MODIFY.register(feature().handlers::handleLootTableModify);
        };
    }
}
