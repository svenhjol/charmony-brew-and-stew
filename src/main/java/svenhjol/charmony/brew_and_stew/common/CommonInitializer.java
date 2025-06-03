package svenhjol.charmony.brew_and_stew.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.brew_and_stew.BrewAndStewMod;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.CookingPots;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.common.CommonInitializer.init();

        // Prepare and run the mod.
        var mod = BrewAndStewMod.instance();
        mod.addSidedFeature(CookingPots.class);
        mod.run(Side.Common);
    }
}
