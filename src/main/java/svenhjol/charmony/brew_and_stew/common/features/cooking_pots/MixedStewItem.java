package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class MixedStewItem extends Item {
    public MixedStewItem(ResourceKey<Item> key) {
        super(new Item.Properties()
            .stacksTo(CookingPots.feature().stewStackSize())
            .food(CookingPots.feature().registers.mixedStewFoodProperties, CookingPots.feature().registers.mixedStewConsumable)
            .usingConvertsTo(Items.BOWL)
            .setId(key));
    }
}
