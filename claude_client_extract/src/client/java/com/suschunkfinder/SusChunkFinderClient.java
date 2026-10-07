package com.suschunkfinder;

import com.suschunkfinder.config.ModConfig;
import com.suschunkfinder.gui.ConfigScreen;
import com.suschunkfinder.module.PlayerDebugModule;
import com.suschunkfinder.module.StorageEspModule;
import com.suschunkfinder.module.SusChunkModule;
import com.suschunkfinder.render.ModRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SusChunkFinderClient implements ClientModInitializer {

    public static final String MOD_ID = "suschunkfinder";
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        var category = KeyBinding.Category.create(Identifier.of("suschunkfinder", "main"));
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.suschunkfinder.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            if (client.world != null && client.player != null) {
                SusChunkModule.tick(client);
                PlayerDebugModule.tick(client);
                StorageEspModule.tick(client);
            }
        });

        // 1.21.11: WorldRenderEvents is in .world package
        WorldRenderEvents.END_MAIN.register(ModRenderer::render);
    }
}
