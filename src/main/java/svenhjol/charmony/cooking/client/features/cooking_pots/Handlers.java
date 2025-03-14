package svenhjol.charmony.cooking.client.features.cooking_pots;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.cooking.common.features.cooking_pots.CookingPotBlock;
import svenhjol.charmony.cooking.common.features.cooking_pots.Networking.S2CAddedToCookingPot;

public final class Handlers extends Setup<CookingPots> {
    public Handlers(CookingPots feature) {
        super(feature);
    }

    public int handleBlockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex == 0) {
            return switch (state.getValue(CookingPotBlock.COOKING_STATUS)) {
                case EMPTY -> -1;
                case HAS_SOME_WATER -> 0x1199dd;
                case FILLED_WITH_WATER -> 0x0088cc;
                case HAS_SOME_FOOD -> 0x807046;
                case COOKED -> 0x442800;
            };
        }
        return -1;
    }

    public void createParticles(Level level, BlockPos pos) {
        var random = level.getRandom();
        for(int i = 0; i < 10; ++i) {
            var offsetX = random.nextGaussian() * 0.02d;
            var offsetY = random.nextGaussian() * 0.02d;
            var offsetZ = random.nextGaussian() * 0.02d;

            level.addParticle(ParticleTypes.SMOKE,
                pos.getX() + 0.13 + (0.73d * random.nextFloat()),
                pos.getY() + 0.8d + random.nextFloat() * 0.3d,
                pos.getZ() + 0.13d + (0.73d * random.nextFloat()),
                offsetX, offsetY, offsetZ);
        }
    }

    public void handleAddedToCookingPot(Player player, S2CAddedToCookingPot payload) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;

        if (level != null) {
            var pos = payload.pos();
            var state = level.getBlockState(pos);
            createParticles(level, pos);
            level.updateNeighborsAt(pos, state.getBlock());
            level.setBlocksDirty(pos, state, state);
            level.sendBlockUpdated(pos, state, state, 2);
        }
    }
}
