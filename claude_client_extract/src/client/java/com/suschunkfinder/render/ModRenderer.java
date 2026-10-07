package com.suschunkfinder.render;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

/**
 * Legacy rendering code relied on removed 1.20-era Blaze3D APIs.
 * The project targets Fabric 1.21.11, so keep the render hook but avoid
 * drawing until the overlay is ported to the current rendering pipeline.
 */
public class ModRenderer {

    public static void render(WorldRenderContext context) {
        // Intentionally left as a no-op while the overlay is ported to 1.21.11.
    }
}
