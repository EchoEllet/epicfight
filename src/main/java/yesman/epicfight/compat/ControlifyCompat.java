package yesman.epicfight.compat;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.isxander.controlify.api.event.ControlifyEvents;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.bindings.ControlifyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;

public class ControlifyCompat implements ControlifyEntrypoint {

    public static boolean isInGame(Minecraft mc) {
        return mc.screen == null && mc.level != null && mc.player != null;
    }
    public static final BindContext COMBAT_MODE = new BindContext(
            EpicFightMod.id("combat_mode"),
            mc -> isInGame(mc) && ClientEngine.getInstance().isEpicFightMode()
    );

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {

    }

    public static InputBindingSupplier COMBAT_ATTACK;

    @Override
    public void onControlifyInit(InitContext ctx) {

    }

    @Override
    public void onControlifyPreInit(PreInitContext ctx) {
        COMBAT_ATTACK = ControlifyBindApi.get().registerBinding(
                builder -> builder
                        .id(EpicFightMod.MODID, "combatAttack")
                        .category(ControlifyBindings.GAMEPLAY_CATEGORY)
                        .allowedContexts(COMBAT_MODE)
                        .keyEmulation(EpicFightKeyMappings.ATTACK) // TODO: Workaround, it's probably better to update ControlEngine fully?
                        .description(Component.literal("Epic Fight Battle Mode Attack"))
                        .name(Component.literal("Battle Mode Attack"))
                        .addKeyCorrelation(EpicFightKeyMappings.ATTACK)
        );
        ControlifyEvents.LOOK_INPUT_MODIFIER.register(event -> {
            LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();

            if (localPlayerPatch != null && localPlayerPatch.isTargetLockedOn()) {
                // Fixes a minor issue when locking on an enemy.
                event.lookInput().zero();
            }
        });
    }
}
