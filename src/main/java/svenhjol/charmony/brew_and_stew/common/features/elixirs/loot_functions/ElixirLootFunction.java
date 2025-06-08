
package svenhjol.charmony.brew_and_stew.common.features.elixirs.loot_functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import svenhjol.charmony.api.elixirs.ElixirsApi;
import svenhjol.charmony.brew_and_stew.common.features.elixirs.Elixirs;

import java.util.List;

public class ElixirLootFunction extends LootItemConditionalFunction {
    public static final MapCodec<ElixirLootFunction> CODEC = RecordCodecBuilder.mapCodec(
        instance -> ElixirLootFunction.commonFields(instance)
            .apply(instance, ElixirLootFunction::new));

    public ElixirLootFunction(List<LootItemCondition> list) {
        super(list);
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return Elixirs.feature().registers.elixirLootFunction.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        return ElixirsApi.instance().randomElixir(context.getResolver(), context.getRandom());
    }
}
