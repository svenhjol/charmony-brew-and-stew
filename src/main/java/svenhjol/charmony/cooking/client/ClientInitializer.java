package svenhjol.charmony.cooking.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.cooking.CookingMod;
import svenhjol.charmony.cooking.client.features.cooking_pots.CookingPots;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.client.ClientInitializer.init();

        // Prepare and run the mod.
        var mod = CookingMod.instance();
        mod.addSidedFeature(CookingPots.class);
        mod.run(Side.Client);
    }
}
