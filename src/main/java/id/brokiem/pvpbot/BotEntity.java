package id.brokiem.pvpbot;

import org.allaymc.api.entity.interfaces.EntityPlayer;

public class BotEntity {
    private final EntityPlayer handle;
    private final TargetingController targetingController;
    private final AimController aimController;
    private final MovementController movementController;
    private final CombatController combatController;

    public BotEntity(EntityPlayer handle) {
        this.handle = handle;
        this.targetingController = new TargetingController(this);
        this.aimController = new AimController(this);
        this.movementController = new MovementController(this);
        this.combatController = new CombatController(this);
    }

    public void tick() {
        if (!handle.isAlive() || handle.isDespawned()) {
            return;
        }

        targetingController.tick();
        aimController.tick();
        movementController.tick();
        combatController.tick();
    }

    public EntityPlayer getHandle() {
        return handle;
    }

    public TargetingController getTargetingController() {
        return targetingController;
    }

    public AimController getAimController() {
        return aimController;
    }

    public MovementController getMovementController() {
        return movementController;
    }

    public CombatController getCombatController() {
        return combatController;
    }
}
