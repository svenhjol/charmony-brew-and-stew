package svenhjol.charmony.brewing.common.features.cooking_pots.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import svenhjol.charmony.core.common.dispenser.CompositeDispenseItemBehavior;
import svenhjol.charmony.core.common.dispenser.ConditionalDispenseItemBehavior;
import svenhjol.charmony.brewing.common.features.cooking_pots.CookingPotBlockEntity;
import svenhjol.charmony.brewing.common.features.cooking_pots.CookingPots;

import java.util.Optional;

public class BowlBehavior implements ConditionalDispenseItemBehavior {
    private ItemStack stack;

    @Override
    public boolean accept(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack stack) {
        var serverLevel = blockSource.level();
        var dispenserState = blockSource.state();
        var pos = blockSource.pos().relative(dispenserState.getValue(DispenserBlock.FACING));

        if (serverLevel.getBlockEntity(pos) instanceof CookingPotBlockEntity cask) {
            var opt = CookingPots.feature().handlers.dispenserTakeFromPot(cask);
            if (opt.isPresent()) {
                this.stack = behavior.consumeWithRemainder(blockSource, stack, opt.get());
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
