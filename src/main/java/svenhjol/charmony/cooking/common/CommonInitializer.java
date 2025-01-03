package svenhjol.charmony.cooking.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charmony.core.enums.Side;
import svenhjol.charmony.cooking.CookingMod;
import svenhjol.charmony.cooking.common.features.cooking_pots.CookingPots;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.common.CommonInitializer.init();

        // Prepare and run the mod.
        var mod = CookingMod.instance();
        mod.addSidedFeature(CookingPots.class);
        mod.run(Side.Common);
    }
}
