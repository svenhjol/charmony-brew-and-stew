package svenhjol.charmony.brew_and_stew.common.features.casks.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import svenhjol.charmony.brew_and_stew.common.features.casks.CaskBlockEntity;
import svenhjol.charmony.brew_and_stew.common.features.casks.Casks;
import svenhjol.charmony.core.common.dispenser.CompositeDispenseItemBehavior;
import svenhjol.charmony.core.common.dispenser.ConditionalDispenseItemBehavior;

import java.util.Optional;

public class PotionBehavior implements ConditionalDispenseItemBehavior {
    private ItemStack stack;
    
    @Override
    public boolean accept(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack stack) {
        var serverLevel = blockSource.level();
        var dispenserState = blockSource.state();
        var pos = blockSource.pos().relative(dispenserState.getValue(DispenserBlock.FACING));

        if (serverLevel.getBlockEntity(pos) instanceof CaskBlockEntity cask) {
            var result = Casks.feature().handlers.dispenserAddToCask(cask, stack);
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
