package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.Networking.S2CAddedToCookingPot;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.dispenser.BowlBehavior;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.dispenser.PotionBehavior;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.dispenser.WaterBucketBehavior;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.helpers.ItemOverrideHelper;

import java.util.List;
import java.util.function.Supplier;

public final class Registers extends Setup<CookingPots> {
    private static final String BLOCK_ID = "cooking_pot";
    private static final String MIXED_STEW_ID = "mixed_stew";

    public final Supplier<CookingPotBlock> block;
    public final Supplier<CookingPotBlock.BlockItem> blockItem;
    public final Supplier<BlockEntityType<CookingPotBlockEntity>> blockEntity;
    public final Supplier<MixedStewItem> mixedStewItem;
    public final Supplier<SoundEvent> addSound;
    public final Supplier<SoundEvent> ambientSound;
    public final Supplier<SoundEvent> finishSound;
    public final Supplier<SoundEvent> takeSound;
    public final FoodProperties mixedStewFoodProperties;
    public final Consumable mixedStewConsumable;

    public Registers(CookingPots feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        block = registry.block(BLOCK_ID, CookingPotBlock::new);
        blockItem = registry.item(BLOCK_ID, key -> new CookingPotBlock.BlockItem(block, key));
        blockEntity = registry.blockEntity(BLOCK_ID, () -> CookingPotBlockEntity::new, List.of(block));

        mixedStewFoodProperties = feature().handlers.buildMixedStewProperties();
        mixedStewConsumable = feature().handlers.buildMixedStewConsumable();
        mixedStewItem = registry.item(MIXED_STEW_ID, MixedStewItem::new);

        addSound = registry.sound("cooking_pot_add");
        ambientSound = registry.sound("cooking_pot_ambient");
        finishSound = registry.sound("cooking_pot_finish");
        takeSound = registry.sound("cooking_pot_take");

        registry.packetSender(Side.Common, S2CAddedToCookingPot.TYPE, S2CAddedToCookingPot.CODEC);

        registry.conditionalDispenserBehavior(() -> Items.BOWL, BowlBehavior::new);
        registry.conditionalDispenserBehavior(() -> Items.POTION, PotionBehavior::new);
        registry.conditionalDispenserBehavior(() -> Items.WATER_BUCKET, WaterBucketBehavior::new);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ItemOverrideHelper.dataComponentValue(mixedStewItem.get(), DataComponents.FOOD, mixedStewFoodProperties);
            ItemOverrideHelper.dataComponentValue(mixedStewItem.get(), DataComponents.CONSUMABLE, mixedStewConsumable);
        };
    }
}
