package id.brokiem.pvpbot;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.player.Player;
import org.allaymc.api.player.GameMode;

public class TargetingController {
    private final BotEntity bot;
    private EntityPlayer currentTarget;

    public TargetingController(BotEntity bot) {
        this.bot = bot;
    }

    public void tick() {
        EntityPlayer handle = bot.getHandle();
        EntityPlayer closest = null;
        double closestDistSq = PvPBotConfig.targetingRadius * PvPBotConfig.targetingRadius;

        for (Player p : handle.getDimension().getPlayers()) {
            EntityPlayer playerEntity = p.getControlledEntity();
            
            // Ignore if it's the bot itself, or if the player is dead/null
            if (playerEntity == null || playerEntity == handle || !playerEntity.isAlive() || playerEntity.isDespawned()) {
                continue;
            }

            // Ignore spectators and creative players
            if (playerEntity.getGameMode() == GameMode.SPECTATOR || playerEntity.getGameMode() == GameMode.CREATIVE) {
                continue;
            }

            double distSq = handle.getLocation().distanceSquared(playerEntity.getLocation());
            if (distSq < closestDistSq) {
                closest = playerEntity;
                closestDistSq = distSq;
            }
        }

        this.currentTarget = closest;
    }

    public EntityPlayer getTarget() {
        if (currentTarget != null && (!currentTarget.isAlive() || currentTarget.isDespawned())) {
            currentTarget = null;
        }
        return currentTarget;
    }
}
