package svenhjol.charmony.cooking.client.feature.cooking_pots;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charmony.cooking.common.features.cooking_pots.Networking;

import java.util.List;

public final class Registers extends Setup<CookingPots> {
    public Registers(CookingPots feature) {
        super(feature);
        var registry = ClientRegistry.forFeature(feature);

        // Handle packets being sent from the server.
        registry.packetReceiver(Networking.S2CAddedToCookingPot.TYPE,
            () -> feature.handlers::handleAddedToCookingPot);
    }

    @Override
    public Runnable boot() {
        return () -> {
            var registry = ClientRegistry.forFeature(feature());
            var common = feature().common.get();

            registry.blockColor(feature().handlers::handleBlockColor, List.of(common.registers.block));
            registry.itemTab(common.registers.block.get(), CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.CAULDRON);
            registry.itemTab(common.registers.mixedStewItem.get(), CreativeModeTabs.FOOD_AND_DRINKS, Items.RABBIT_STEW);
        };
    }
}
