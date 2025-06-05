package svenhjol.charmony.brew_and_stew.client.features.casks;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.brew_and_stew.common.features.casks.Networking;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;

public class Registers extends Setup<Casks> {
    public Registers(Casks feature) {
        super(feature);
        var registry = ClientRegistry.forFeature(feature);

        // Handle packets being sent from the server.
        registry.packetReceiver(Networking.S2CAddedToCask.TYPE,
            feature().handlers::handleAddedToCask);
    }

    @Override
    public Runnable boot() {
        return () -> {
            var registry = ClientRegistry.forFeature(feature());
            var common = feature().common.get();

            registry.itemTab(
                common.registers.block.get(),
                CreativeModeTabs.FUNCTIONAL_BLOCKS,
                Items.JUKEBOX);
        };
    }
}
