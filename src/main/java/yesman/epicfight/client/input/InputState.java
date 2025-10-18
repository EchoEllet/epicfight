package yesman.epicfight.client.input;

import net.minecraft.client.player.Input;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Abstract player input state as a record.
 * Mirrors vanilla Minecraft {@link Input} class but works for any controller.
 */
@OnlyIn(Dist.CLIENT)
public record InputState(
        float leftImpulse,
        float forwardImpulse,
        boolean up,
        boolean down,
        boolean left,
        boolean right,
        boolean jumping,
        boolean sneaking
) {
    /**
     * Returns a 2D movement vector (left/right, forward/back) based on impulses.
     */
    public Vec2 getMoveVector() {
        return new Vec2(this.leftImpulse, this.forwardImpulse);
    }

    /**
     * Returns true if there is any forward movement.
     */
    public boolean hasForwardImpulse() {
        return this.forwardImpulse > 1.0E-5F;
    }
}
