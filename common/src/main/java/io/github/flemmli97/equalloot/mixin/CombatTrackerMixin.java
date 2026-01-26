package io.github.flemmli97.equalloot.mixin;

import io.github.flemmli97.equalloot.attachment.PlayerDamageTracker;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {

    @Shadow
    @Final
    private LivingEntity mob;

    @Inject(method = "recordDamage", at = @At(value = "HEAD"))
    private void onRecordDamage(DamageSource damageSource, float damageAmount, CallbackInfo info) {
        PlayerDamageTracker.get(this.mob).onIncomingDamage(damageSource, damageAmount);
    }
}
