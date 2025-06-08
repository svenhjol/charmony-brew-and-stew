package svenhjol.charmony.brew_and_stew.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.brew_and_stew.BrewAndStewMod;
import svenhjol.charmony.brew_and_stew.common.features.casks.Casks;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.CookingPots;
import svenhjol.charmony.brew_and_stew.common.features.elixirs.Elixirs;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.common.CommonInitializer.init();

        // Prepare and run the mod.
        var mod = BrewAndStewMod.instance();
        mod.addSidedFeature(Casks.class);
        mod.addSidedFeature(CookingPots.class);
        mod.addSidedFeature(Elixirs.class);
        mod.run(Side.Common);
    }
}
