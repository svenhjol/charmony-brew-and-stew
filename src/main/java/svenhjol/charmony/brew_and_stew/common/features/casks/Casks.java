package svenhjol.charmony.brew_and_stew.common.features.casks;


import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = """
   Casks are used to hold potions. Use a potion bottle on a cask to add the bottle's contents to the cask.
   Use an empty bottle to retrieve one potion bottle back from the cask.
   Potion effects are combined inside the cask and the contents are not lost when the cask is broken.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Casks extends SidedFeature {
    public final Handlers handlers;
    public final Registers registers;
    public final Advancements advancements;

    @Configurable(
        name = "Maximum bottles",
        description = "Maximum number of bottles a cask can hold."
    )
    private static int maxBottles = 64;

    @Configurable(
        name = "Allow splash and lingering",
        description = "If true, splash and lingering potions may be added to a cask."
    )
    private static boolean allowSplashAndLingering = false;

    public Casks(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
        advancements = new Advancements(this);
    }

    public static Casks feature() {
        return Mod.getSidedFeature(Casks.class);
    }

    public int maxBottles() {
        return Mth.clamp(maxBottles, 1, 256);
    }

    public boolean allowSplashAndLingering() {
        return allowSplashAndLingering;
    }
}
