package svenhjol.charmony.brew_and_stew.common.features.cooking_pots.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import svenhjol.charmony.core.common.dispenser.CompositeDispenseItemBehavior;
import svenhjol.charmony.core.common.dispenser.ConditionalDispenseItemBehavior;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.CookingPotBlockEntity;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.CookingPots;

import java.util.Optional;

public class WaterBucketBehavior implements ConditionalDispenseItemBehavior {
    private ItemStack stack;
    
    @Override
    public boolean accept(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack stack) {
        var serverLevel = blockSource.level();
        var dispenserState = blockSource.state();
        var pos = blockSource.pos().relative(dispenserState.getValue(DispenserBlock.FACING));

        if (serverLevel.getBlockEntity(pos) instanceof CookingPotBlockEntity cask) {
            var result = CookingPots.feature().handlers.dispenserAddToPot(cask, stack);
            if (result) {
                stack.shrink(1);
                this.stack = stack;
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Optional<ItemStack> stack() {
        return Optional.ofNullable(stack);
    }
}
