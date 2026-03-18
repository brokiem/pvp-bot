package id.brokiem.pvpbot;

import org.allaymc.api.entity.component.EntityHeadYawComponent;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.math.location.Location3d;
import org.allaymc.api.math.location.Location3dc;

import java.util.Random;

public class AimController {
    private final BotEntity bot;
    private final Random random = new Random();

    public AimController(BotEntity bot) {
        this.bot = bot;
    }

    public void tick() {
        EntityPlayer target = bot.getTargetingController().getTarget();
        if (target == null) return;

        EntityPlayer handle = bot.getHandle();
        Location3dc botLoc = handle.getLocation();
        Location3dc targetLoc = target.getLocation();

        double dx = targetLoc.x() - botLoc.x();
        double dz = targetLoc.z() - botLoc.z();
        double dy = (targetLoc.y() + target.getEyeHeight()) - (botLoc.y() + handle.getEyeHeight());

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        
        double pitch = Math.toDegrees(-Math.atan2(dy, distanceXZ));
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;

        // Add humanization/jitter
        yaw += (random.nextDouble() - 0.5) * 5.0;
        pitch += (random.nextDouble() - 0.5) * 5.0;

        Location3d newLoc = new Location3d(botLoc);
        newLoc.yaw = yaw;
        newLoc.pitch = pitch;

        handle.trySetLocation(newLoc);

        if (handle instanceof EntityHeadYawComponent headComp) {
            headComp.setHeadYaw(yaw);
        }
    }
}
