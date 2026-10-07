package com.example.claudeclient;

import com.example.claudeclient.autosell.SellConfig;
import com.example.claudeclient.autosell.SellMenuScreen;
import com.example.claudeclient.autosell.Seller;
import com.example.claudeclient.core.Config;
import com.example.claudeclient.core.Module;
import com.example.claudeclient.core.Modules;
import com.example.claudeclient.gui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ClaudeClient implements ClientModInitializer {
    static KeyBinding guiKey, sellMenuKey, sellKey, loopKey;

    static KeyBinding key(String id, int code, KeyBinding.Category cat) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id, InputUtil.Type.KEYSYM, code, cat));
    }

    @Override
    public void onInitializeClient() {
        SellConfig.load();
        Config.load();

        var cat = KeyBinding.Category.create(Identifier.of("claudeclient", "main"));
        guiKey = key("key.claudeclient.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, cat);
        sellMenuKey = key("key.claudeclient.sellmenu", GLFW.GLFW_KEY_BACKSLASH, cat);
        sellKey = key("key.claudeclient.sell", GLFW.GLFW_KEY_RIGHT_BRACKET, cat);
        loopKey = key("key.claudeclient.loop", GLFW.GLFW_KEY_LEFT_BRACKET, cat);

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
            Identifier.of("claudeclient", "hud"), (ctx, tick) -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player == null || mc.options.hudHidden) return;
                for (Module m : Modules.ALL) if (m.isEnabled()) m.renderHud(ctx, mc);
            });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) mc.setScreen(new ClickGuiScreen());
            while (sellMenuKey.wasPressed()) mc.setScreen(new SellMenuScreen());
            while (sellKey.wasPressed()) Seller.start(mc);
            while (loopKey.wasPressed()) {
                SellConfig.I.loop = !SellConfig.I.loop;
                SellConfig.save();
                if (mc.player != null)
                    mc.player.sendMessage(Text.literal("[Claude Client] Loop " + (SellConfig.I.loop ? "ON" : "OFF")), true);
            }
            for (Module m : Modules.ALL) if (m.isEnabled()) m.onTick(mc);
            Seller.tick(mc);
            Seller.autoCheck(mc);
        });
    }
}
