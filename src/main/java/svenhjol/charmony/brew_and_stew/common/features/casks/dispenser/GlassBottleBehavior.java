package svenhjol.charmony.brew_and_stew.common.features.casks.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import svenhjol.charmony.brew_and_stew.common.features.casks.CaskBlockEntity;
import svenhjol.charmony.brew_and_stew.common.features.casks.Casks;
import svenhjol.charmony.core.common.dispenser.CompositeDispenseItemBehavior;
import svenhjol.charmony.core.common.dispenser.ConditionalDispenseItemBehavior;

import java.util.Optional;

public class GlassBottleBehavior implements ConditionalDispenseItemBehavior {
    private ItemStack stack;
    
    @Override
    public boolean accept(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack stack) {
        var serverLevel = blockSource.level();
        var dispenserState = blockSource.state();
        var pos = blockSource.pos().relative(dispenserState.getValue(DispenserBlock.FACING));

        if (serverLevel.getBlockEntity(pos) instanceof CaskBlockEntity cask) {
            var opt = Casks.feature().handlers.dispenserTakeFromCask(cask);
            if (opt.isPresent()) {
                this.stack = this.takeLiquid(behavior, blockSource, stack, opt.get());
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Optional<ItemStack> stack() {
        return Optional.ofNullable(stack);
    }

    private ItemStack takeLiquid(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack itemStack, ItemStack itemStack2) {
        blockSource.level().gameEvent(null, GameEvent.FLUID_PICKUP, blockSource.pos());
        return behavior.consumeWithRemainder(blockSource, itemStack, itemStack2);
    }
}
