package svenhjol.charmony.brew_and_stew.common.features.elixirs;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.Util;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import svenhjol.charmony.api.elixirs.ElixirDefinition;
import svenhjol.charmony.brew_and_stew.common.features.elixirs.loot_functions.ElixirLootFunction;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Handlers extends Setup<Elixirs> {
    public Handlers(Elixirs feature) {
        super(feature);
    }

    public ItemStack createElixirItem(ElixirDefinition def, HolderGetter.Provider provider, RandomSource random) {
        var name = def.name(random);
        var rarity = def.rarity();
        var lore = def.lore();
        var numEffects = def.numberOfEffects(random);

        List<MobEffectInstance> effects = new ArrayList<>();

        var fixed = new ArrayList<>(def.fixedEffects());
        if (!fixed.isEmpty()) {
            Util.shuffle(fixed, random);
            var max = Math.min(numEffects, fixed.size());

            for (var i = 0; i < max; i++) {
                effects.add(fixed.get(i));
            }
        }

        var valid = new ArrayList<>(def.validEffects(provider));
        if (!valid.isEmpty()) {
            Util.shuffle(valid, random);
            var max = Math.min(numEffects, valid.size());

            for (var i = 0; i < max; i++) {
                var effect = valid.get(i);

                var maxDuration = def.maxDuration();
                var minDuration = def.minDuration();
                var maxAmplifier = def.maxAmplifier();
                var minAmplifier = def.minAmplifier();

                var durationBound = Math.max(1, maxDuration - minDuration);
                var amplifierBound = Math.max(1, maxAmplifier - minAmplifier);
                var duration = Math.max(1, random.nextInt(durationBound) + minDuration) * 20;
                var amplifier = random.nextInt(amplifierBound) + minAmplifier;

                effects.add(new MobEffectInstance(effect, duration, amplifier));
            }
        }

        if (effects.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }

        var potionContents = new PotionContents(Optional.empty(), Optional.empty(), effects, Optional.empty());
        var out = new ItemStack(Items.POTION);
        out.set(DataComponents.POTION_CONTENTS, potionContents);
        out.set(DataComponents.CUSTOM_NAME, name);
        out.set(DataComponents.RARITY, rarity);
        lore.ifPresent(l -> out.set(DataComponents.LORE, l));

        return out;
    }

    public void handleLootTableModify(ResourceKey<LootTable> key, LootTable.Builder builder, LootTableSource source, HolderLookup.Provider provider) {
        if (!source.isBuiltin()) return;

        if (key == BuiltInLootTables.SIMPLE_DUNGEON) {
            addToLootWithChance(builder, feature().dungeonChestChance());
        }
        if (key == BuiltInLootTables.TRIAL_CHAMBERS_REWARD) {
            addToLootWithChance(builder, feature().trialChambersChance());
        }
        if (key == BuiltInLootTables.STRONGHOLD_CORRIDOR) {
            addToLootWithChance(builder, feature().strongholdCorridorChance());
        }
        if (key == BuiltInLootTables.CLERIC_GIFT) {
            addToLootWithChance(builder, feature().clericGiftChance());
        }

        // Add any type of elixir to the empty elixirs loot table.
        if (key == Tags.ELIXIRS) {
            addToLootWithChance(builder, 1.0d);
        }
    }

    public MutableComponent prefixedPotionName(RandomSource random) {
        var prefixes = Arrays.stream(Component.translatable("gui.charmony.potion_prefixes").getString().split(",")).toList();
        var prefix = prefixes.get(random.nextInt(prefixes.size()));

        var potionNames = Arrays.stream(Component.translatable("gui.charmony.potion_names").getString().split(",")).toList();
        var potionName = potionNames.get(random.nextInt(potionNames.size()));

        return Component.translatable("gui.charmony.prefixed_potion_name", prefix, potionName);
    }

    private void addToLootWithChance(LootTable.Builder builder, double chance) {
        var pool = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .when(LootItemRandomChanceCondition.randomChance((float)chance))
            .add(LootItem.lootTableItem(Items.GLASS_BOTTLE)
                .setWeight(1)
                .apply(() -> new ElixirLootFunction(List.of())));

        builder.pool(pool.build());
    }
}
