package yesman.epicfight.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.InputEvent;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.events.engine.ControlEngine;

@OnlyIn(Dist.CLIENT)
public final class EpicFightControls {
    private EpicFightControls() {
    }

    private static final Minecraft MC = Minecraft.getInstance();
    private static final Options OPTIONS = MC.options;
    private static final IEpicFightController controllerInputProvider = new ControlifyEpicFightController();

    @NotNull
    private static KeyMapping getKeyMapping(EpicFightInputAction action) {
        return switch (action) {
            case ATTACK -> EpicFightKeyMappings.ATTACK;
            case VANILLA_ATTACK_DESTROY -> OPTIONS.keyAttack;
            case JUMP -> OPTIONS.keyJump;
            case MOBILITY -> EpicFightKeyMappings.MOVER_SKILL;
            case GUARD -> null;
            case DODGE -> null;
            case LOCK_ON -> null;
            case TOGGLE_BATTLE_MODE -> null;
            case WEAPON_INNATE_SKILL -> EpicFightKeyMappings.WEAPON_INNATE_SKILL;
        };
    }

    public static boolean isActionTriggered(EpicFightInputAction action) {
        return getKeyMapping(action).isDown() || controllerInputProvider.isActionTriggered(action);
    }

    public static boolean isActionTriggeredEf(EpicFightInputAction action) {
        return ControlEngine.isKeyDown(getKeyMapping(action)) || controllerInputProvider.isActionTriggered(action);
    }

    private static boolean isUsingController() {
        return controllerInputProvider.inControllerMode();
    }

    public static boolean isActionJustPressed(EpicFightInputAction action, boolean eventCheck) {
        KeyMapping keyMapping = getKeyMapping(action);

        boolean consumes = !isUsingController() ? keyMapping.consumeClick() : controllerInputProvider.isActionJustPressed(action);

        if (consumes && eventCheck) {
            int mouseButton = InputConstants.Type.MOUSE == keyMapping.getKey().getType() ? keyMapping.getKey().getValue() : -1;
            InputEvent.InteractionKeyMappingTriggered inputEvent = ClientHooks.onClickInput(mouseButton, keyMapping, InteractionHand.MAIN_HAND);

            if (inputEvent.isCanceled()) {
                return false;
            }
        }

        return consumes;
    }

    public static InputState getInputState() {
        if (isUsingController()) {
            InputState inputState = controllerInputProvider.getInputState();

            if (inputState != null) {
                return inputState;
            }
        }

        LocalPlayer player = MC.player;
        Input playerInput = player.input;
        return new InputState(
                playerInput.leftImpulse, playerInput.forwardImpulse,
                playerInput.up, playerInput.down,
                playerInput.left, playerInput.right,
                playerInput.jumping, playerInput.shiftKeyDown
        );
    }
}
