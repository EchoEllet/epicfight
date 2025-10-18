package yesman.epicfight.client.input;

import dev.isxander.controlify.InputMode;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.controller.ControllerEntity;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.compat.ControlifyCompat;

import javax.annotation.Nullable;
import java.util.Optional;

public class ControlifyEpicFightController implements IEpicFightController {
    private static ControlifyApi getApi() {
        return ControlifyApi.get();
    }

    @NotNull
    private static InputBinding getInputBinding(EpicFightInputAction action) {
        final InputBindingSupplier supplier = switch (action) {
            case ATTACK -> ControlifyCompat.COMBAT_ATTACK; // TODO: ADDD
            case VANILLA_ATTACK_DESTROY -> ControlifyBindings.ATTACK;
            case JUMP, MOBILITY -> ControlifyBindings.JUMP;
            case WEAPON_INNATE_SKILL -> ControlifyBindings.ATTACK;
            case GUARD -> null;
            case DODGE -> null;
            case LOCK_ON -> null;
            case TOGGLE_BATTLE_MODE -> null;
        };
        return supplier.on(ControlifyApi.get().getCurrentController().get());
    }

    @Override
    public boolean inControllerMode() {
        return getApi().currentInputMode() == InputMode.CONTROLLER;
    }

    @Override
    public boolean isActionJustPressed(EpicFightInputAction action) {
        return inControllerMode() && getInputBinding(action).justPressed();
    }

    @Override
    public boolean isActionTriggered(EpicFightInputAction action) {
        return inControllerMode() && getInputBinding(action).digitalNow();
    }

    @Nullable
    @Override
    public InputState getInputState() {
        Optional<ControllerEntity> optionalControllerEntity = getApi().getCurrentController();

        if (optionalControllerEntity.isEmpty()) {
            return null;
        }

        ControllerEntity controller = optionalControllerEntity.get();

        InputBinding forwardBind = ControlifyBindings.WALK_FORWARD.on(controller);
        InputBinding backwardBind = ControlifyBindings.WALK_BACKWARD.on(controller);
        InputBinding leftBind = ControlifyBindings.WALK_LEFT.on(controller);
        InputBinding rightBind = ControlifyBindings.WALK_RIGHT.on(controller);
        InputBinding jumpBind = ControlifyBindings.JUMP.on(controller);
        InputBinding sneakBind = ControlifyBindings.SNEAK.on(controller);

        float forwardImpulse = forwardBind.analogueNow() - backwardBind.analogueNow();
        float leftImpulse = rightBind.analogueNow() - leftBind.analogueNow();

        return new InputState(leftImpulse, forwardImpulse, forwardBind.digitalNow(), backwardBind.digitalNow(), leftBind.digitalNow(), rightBind.digitalNow(), jumpBind.digitalNow(), sneakBind.digitalNow());
    }
}
