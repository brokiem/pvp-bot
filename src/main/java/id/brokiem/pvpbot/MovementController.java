package id.brokiem.pvpbot;

import org.allaymc.api.entity.component.EntityPhysicsComponent;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.world.Dimension;
import org.joml.Vector3dc;

import java.util.Random;

public class MovementController {
    private final BotEntity bot;
    private final Random random = new Random();

    private int strafeDirection = 1; // 1 for left, -1 for right
    private int strafeTimer = 0;

    // Config values
    private final double forwardSpeed = PvPBotConfig.forwardSpeed;
    private final double strafeSpeed = PvPBotConfig.strafeSpeed;
    private final double optimalDistance = PvPBotConfig.optimalDistance;

    public MovementController(BotEntity bot) {
        this.bot = bot;
    }

    public void tick() {
        EntityPlayer target = bot.getTargetingController().getTarget();
        if (target == null)
            return;

        EntityPlayer handle = bot.getHandle();
        Location3dc botLoc = handle.getLocation();
        Location3dc targetLoc = target.getLocation();

        // Calculate distances
        double dx = targetLoc.x() - botLoc.x();
        double dz = targetLoc.z() - botLoc.z();
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        // Yaw in radians
        double yawRadians = Math.toRadians(botLoc.yaw());

        // Forward vector
        double fwdX = -Math.sin(yawRadians);
        double fwdZ = Math.cos(yawRadians);

        // Strafe vector (90 degrees offset)
        double strX = Math.cos(yawRadians);
        double strZ = Math.sin(yawRadians);

        double moveX = 0;
        double moveZ = 0;

        // Forward / Backward movement
        if (distanceXZ > optimalDistance) {
            moveX += fwdX * forwardSpeed;
            moveZ += fwdZ * forwardSpeed;
        } else if (distanceXZ < optimalDistance - 0.5) {
            moveX -= fwdX * forwardSpeed * 0.8; // Move back slightly slower
            moveZ -= fwdZ * forwardSpeed * 0.8;
        }

        // Strafing logic
        strafeTimer--;
        if (strafeTimer <= 0) {
            strafeDirection = random.nextBoolean() ? 1 : -1;
            strafeTimer = 10 + random.nextInt(30); // switch direction every 10-40 ticks
        }

        // Only strafe if we are somewhat close to the target
        if (distanceXZ < optimalDistance + 2.0) {
            moveX += strX * strafeSpeed * strafeDirection;
            moveZ += strZ * strafeSpeed * strafeDirection;
        }

        Vector3dc currentMotion = handle.getMotion();

        // We only modify X and Z motion, maintain Y (gravity/jumping)
        double motionY = currentMotion.y();

        // Basic Jump logic for obstacles
        if (handle.isOnGround() && distanceXZ > optimalDistance) {
            Dimension dim = handle.getDimension();

            // Calculate movement direction
            double moveLen = Math.sqrt(moveX * moveX + moveZ * moveZ);
            if (moveLen > 0.01) {
                double normX = moveX / moveLen;
                double normZ = moveZ / moveLen;

                // Track 0.6 blocks ahead based on movement vector
                double aheadX = botLoc.x() + normX * 0.6;
                double aheadZ = botLoc.z() + normZ * 0.6;

                int blockX = (int) Math.floor(aheadX);
                int blockY = (int) Math.floor(botLoc.y());
                int blockZ = (int) Math.floor(aheadZ);

                BlockState blockAhead = dim.getBlockState(blockX, blockY, blockZ);
                // If the block right ahead has collision, attempt a jump
                if (blockAhead != null && blockAhead.getBlockStateData().hasCollision()) {
                    BlockState blockAbove = dim.getBlockState(blockX, blockY + 1, blockZ);
                    BlockState blockTwoAbove = dim.getBlockState(blockX, blockY + 2, blockZ);
                    if (blockAbove == null || !blockAbove.getBlockStateData().hasCollision()) {
                        if (blockTwoAbove == null || !blockTwoAbove.getBlockStateData().hasCollision()) {
                            motionY = 0.42; // standard Minecraft jump velocity
                        }
                    }
                }
            }
        }

        handle.setMotion(moveX, motionY, moveZ);
    }
}
