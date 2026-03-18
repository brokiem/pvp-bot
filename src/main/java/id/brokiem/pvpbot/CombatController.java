package id.brokiem.pvpbot;

import org.allaymc.api.entity.damage.DamageContainer;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.world.Dimension;
import org.allaymc.api.block.type.BlockState;
import org.joml.Vector3d;

import java.util.Random;

public class CombatController {
    private final BotEntity bot;
    private final Random random = new Random();

    private int ticksSinceLastAttack = 0;
    
    public CombatController(BotEntity bot) {
        this.bot = bot;
    }

    public void tick() {
        EntityPlayer target = bot.getTargetingController().getTarget();
        if (target == null) return;

        ticksSinceLastAttack++;

        int targetCps = PvPBotConfig.minCps + random.nextInt((PvPBotConfig.maxCps - PvPBotConfig.minCps) + 1);
        int ticksPerAttack = 20 / targetCps;

        if (ticksSinceLastAttack >= ticksPerAttack) {
            EntityPlayer handle = bot.getHandle();
            double distanceSq = handle.getLocation().distanceSquared(target.getLocation());
            double reachSq = PvPBotConfig.reach * PvPBotConfig.reach;

            if (distanceSq <= reachSq && hasLineOfSight(handle, target)) {
                handle.applyAction(org.allaymc.api.entity.action.SimpleEntityAction.SWING_ARM);
                
                // Deal damage
                float damage = 0.1f; // Could be calculated based on held item
                target.attack(DamageContainer.entityAttack(handle, damage));
                
                ticksSinceLastAttack = 0;
            }
        }
    }

    private boolean hasLineOfSight(EntityPlayer source, EntityPlayer target) {
        Location3dc sourceLoc = source.getLocation();
        Location3dc targetLoc = target.getLocation();
        Dimension dim = source.getDimension();
        if (dim != target.getDimension()) return false;

        Vector3d start = new Vector3d(sourceLoc.x(), sourceLoc.y() + source.getEyeHeight(), sourceLoc.z());
        Vector3d end = new Vector3d(targetLoc.x(), targetLoc.y() + target.getEyeHeight(), targetLoc.z());

        Vector3d dir = new Vector3d(end).sub(start);
        double dist = dir.length();
        dir.normalize();

        double step = 0.5; // check every half block
        Vector3d current = new Vector3d(start);

        for (double d = 0; d < dist; d += step) {
            int blockX = (int) Math.floor(current.x);
            int blockY = (int) Math.floor(current.y);
            int blockZ = (int) Math.floor(current.z);

            BlockState block = dim.getBlockState(blockX, blockY, blockZ);
            if (block != null && block.getBlockStateData().hasCollision()) {
                return false; // Obstruction found
            }
            
            current.add(dir.x * step, dir.y * step, dir.z * step);
        }

        return true;
    }
}
