package svenhjol.charmony.brew_and_stew.common.features.elixirs;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

import java.util.ArrayList;
import java.util.List;

@FeatureDefinition(side = Side.Common, description = """
    Adds potions with effects that last longer and have much higher potency than can normally be brewed.
    Elixirs can only be found in loot.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Elixirs extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Providers providers;

    @Configurable(
        name = "Dungeon chest chance",
        description = """
            Chance (out of 1.0) of an elixir being found in a dungeon chest."""
    )
    private static double dungeonChestChance = 0.12d;

    @Configurable(
        name = "Stronghold corridor chest chance",
        description = """
            Chance (out of 1.0) of an elixir being found in a stronghold corridor chest."""
    )
    private static double strongholdCorridorChance = 0.25d;

    @Configurable(
        name = "Cleric gift chance",
        description = """
            Chance (out of 1.0) of an elixir being given by a cleric when the player has Hero of the Village."""
    )
    private static double clericGiftChance = 0.12d;

    @Configurable(
        name = "Trial Chambers vault chance",
        description = """
            Chance (out of 1.0) of an elixir being provided by a Trial Chambers vault."""
    )
    private static double trialChambersChance = 0.25d;

    @Configurable(
        name = "Common elixir min amplifier",
        description = """
            Minimum amplifier (potency) of a common elixir.""",
        requireRestart = false
    )
    private static int commonElixirMinAmplifier = 2;

    @Configurable(
        name = "Common elixir max amplifier",
        description = """
            Maximum amplifier (potency) of a common elixir.""",
        requireRestart = false
    )
    private static int commonElixirMaxAmplifier = 5;

    @Configurable(
        name = "Common elixir effects",
        description = """
            Effects that can be added to a common elixir.
            Common elixirs randomize their duration and amplifier (potency).""",
        requireRestart = false
    )
    private static List<String> commonElixirEffects = new ArrayList<>(List.of(
        "minecraft:absorption",
        "minecraft:fire_resistance",
        "minecraft:haste",
        "minecraft:health_boost",
        "minecraft:invisibility",
        "minecraft:jump_boost",
        "minecraft:night_vision",
        "minecraft:regeneration",
        "minecraft:resistance",
        "minecraft:saturation",
        "minecraft:speed",
        "minecraft:strength",
        "minecraft:water_breathing"
    ));

    public Elixirs(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        providers = new Providers(this);
        registers = new Registers(this);
    }

    public static Elixirs feature() {
        return Mod.getSidedFeature(Elixirs.class);
    }

    public List<String> commonElixirEffects() {
        return commonElixirEffects;
    }

    public int commonElixirMinAmplifier() {
        return Math.max(1, commonElixirMinAmplifier);
    }

    public int commonElixirMaxAmplifier() {
        return Math.max(commonElixirMinAmplifier(), commonElixirMaxAmplifier);
    }

    public double dungeonChestChance() {
        return Mth.clamp(dungeonChestChance, 0.0d, 1.0d);
    }

    public double strongholdCorridorChance() {
        return Mth.clamp(strongholdCorridorChance, 0.0d, 1.0d);
    }

    public double clericGiftChance() {
        return Mth.clamp(clericGiftChance, 0.0d, 1.0d);
    }

    public double trialChambersChance() {
        return Mth.clamp(trialChambersChance, 0.0d, 1.0d);
    }
}
