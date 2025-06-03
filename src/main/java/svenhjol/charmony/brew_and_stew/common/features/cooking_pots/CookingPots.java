package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Cooking pots allow any food item to be added. Once the combined nourishment total has reached maximum, use wooden bowls to take mixed stew from the pot.
    All negative and positive effects will be removed from the food added to the pot.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class CookingPots extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    @Configurable(
        name = "Stew hunger restored",
        description = """
            Number of hunger points restored from a single portion of mixed stew."""
    )
    private static int stewHungerRestored = 9;

    @Configurable(
        name = "Stew saturation restored",
        description = """
            Amount of saturation restored from a single portion of mixed stew."""
    )
    private static double stewSaturationRestored = 0.9d;

    @Configurable(
        name = "Stew stack size",
        description = """
            Maximum stack size of stew obtained from the cooking pot."""
    )
    private static int stewStackSize = 16;

    public CookingPots(Mod mod) {
        super(mod);

        handlers = new Handlers(this);
        registers = new Registers(this);
        advancements = new Advancements(this);
    }

    public static CookingPots feature() {
        return Mod.getSidedFeature(CookingPots.class);
    }

    public int stewHungerRestored() {
        return Mth.clamp(stewHungerRestored, 0, 64);
    }

    public float stewSaturationRestored() {
        return (float)Mth.clamp(stewSaturationRestored, 0.0f, 64.0f);
    }

    public int stewStackSize() {
        return Mth.clamp(stewStackSize, 1, Item.DEFAULT_MAX_STACK_SIZE);
    }
}
