package svenhjol.charmony.brew_and_stew.client.features.casks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import svenhjol.charmony.brew_and_stew.common.features.casks.Networking;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<Casks> {
    public Handlers(Casks feature) {
        super(feature);
    }

    @SuppressWarnings("unused")
    public void handleAddedToCask(Player player, Networking.S2CAddedToCask payload) {
        var minecraft = Minecraft.getInstance();

        if (minecraft.level != null) {
            createParticles(minecraft.level, payload.pos());
        }
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
}
