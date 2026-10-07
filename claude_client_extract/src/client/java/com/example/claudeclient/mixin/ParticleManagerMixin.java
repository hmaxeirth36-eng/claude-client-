package com.example.claudeclient.mixin;

import com.example.claudeclient.modules.LagCut;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true)
    private void cc$skip(ParticleEffect effect, double x, double y, double z,
                         double vx, double vy, double vz, CallbackInfoReturnable<Particle> cir) {
        if (!LagCut.cutParticles()) return;
        var t = effect.getType();
        if (t == ParticleTypes.EXPLOSION || t == ParticleTypes.EXPLOSION_EMITTER || t == ParticleTypes.LARGE_SMOKE) {
            cir.setReturnValue(null);
        }
    }
}
