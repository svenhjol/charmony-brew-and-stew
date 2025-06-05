package svenhjol.charmony.brew_and_stew.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.brew_and_stew.BrewAndStewMod;
import svenhjol.charmony.brew_and_stew.client.features.cooking_pots.CookingPots;
import svenhjol.charmony.brew_and_stew.common.features.casks.Casks;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.client.ClientInitializer.init();

        // Prepare and run the mod.
        var mod = BrewAndStewMod.instance();
        mod.addSidedFeature(Casks.class);
        mod.addSidedFeature(CookingPots.class);
        mod.run(Side.Client);
    }
}
