package svenhjol.charmony.brew_and_stew.common.features.casks;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<Casks> {
    public Advancements(Casks feature) {
        super(feature);
    }

    public void addedLiquidToCask(Player player) {
        AdvancementHelper.trigger("added_liquid_to_cask", player);
    }

    public void tookLiquidFromCask(Player player) {
        AdvancementHelper.trigger("took_liquid_from_cask", player);
    }
}
