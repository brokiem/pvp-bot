package id.brokiem.pvpbot;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.SenderType;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.EntityInitInfo;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.entity.type.EntityTypes;
import org.allaymc.api.player.GameMode;
import org.allaymc.api.player.Player;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

import java.util.ArrayList;
import java.util.List;

public class PvPBot extends Plugin {
    private final List<BotEntity> activeBots = new ArrayList<>();

    @Override
    public void onLoad() {
        this.pluginLogger.info("PvPBot is loaded!");
    }

    @Override
    public void onEnable() {
        this.pluginLogger.info("PvPBot is enabled!");

        // Register tick loop for all active bots
        Server.getInstance().getScheduler().scheduleRepeating(this, () -> {
            activeBots.removeIf(bot -> {
                if (!bot.getHandle().isAlive() || bot.getHandle().isDespawned()) {
                    return true;
                }
                bot.tick();
                return false;
            });
        }, 1);

        // Register spawnbot command
        Registries.COMMANDS.register(new Command("spawnbot", "Spawn a PvP Bot", null) {
            @Override
            public void prepareCommandTree(CommandTree tree) {
                tree.getRoot().exec((context, sender) -> {
                    Player player = sender.getController();
                    if (player == null) {
                        pluginLogger.info("Command executed by non-player check failed.");
                        return CommandResult.fail(context);
                    }

                    pluginLogger.info("Player " + player.getOriginName() + " executed /spawnbot");

                    EntityPlayer botPlayer = EntityTypes.PLAYER.createEntity(
                            EntityInitInfo.builder().loc(sender.getLocation()).build()
                    );
                    
                    botPlayer.setNameTag("ComboBot");
                    botPlayer.setNameTagAlwaysShow(true);
                    botPlayer.setGameMode(GameMode.SURVIVAL);

                    sender.getDimension().getEntityManager().addEntity(botPlayer);

                    BotEntity bot = new BotEntity(botPlayer);
                    activeBots.add(bot);

                    sender.sendMessage("Spawned ComboBot!");
                    pluginLogger.info("Bot spawned successfully.");
                    return CommandResult.success(context);
                }, SenderType.ACTUAL_PLAYER);
            }
        });
    }

    @Override
    public void onDisable() {
        this.pluginLogger.info("PvPBot is disabled!");
        for (BotEntity bot : activeBots) {
            if (bot.getHandle() != null) {
                bot.getHandle().remove();
            }
        }
        activeBots.clear();
    }
}