package svenhjol.charmony.cooking.common.features.cooking_pots;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public final class Advancements extends Setup<CookingPots> {
    public Advancements(CookingPots feature) {
        super(feature);
    }

    public void preparedCookingPot(Player player) {
        AdvancementHelper.trigger("prepared_cooking_pot", player);
    }

    public void tookFoodFromCookingPot(Player player) {
        AdvancementHelper.trigger("took_food_from_cooking_pot", player);
    }
}
