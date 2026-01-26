package io.github.flemmli97.equalloot.mixin;

import io.github.flemmli97.equalloot.attachment.PlayerDamageTracker;
import io.github.flemmli97.equalloot.utils.DamageContainerGetter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements DamageContainerGetter {

    @Unique
    private final PlayerDamageTracker equalLoot$DamageContainer = new PlayerDamageTracker((LivingEntity) (Object) this);

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void loadData(ValueInput input, CallbackInfo ci) {
        this.equalLoot$DamageContainer.load(input.listOrEmpty(PlayerDamageTracker.ID.toString(), PlayerDamageTracker.DamageHolder.CODEC));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void saveData(ValueOutput output, CallbackInfo ci) {
        this.equalLoot$DamageContainer.save(output.list(PlayerDamageTracker.ID.toString(), PlayerDamageTracker.DamageHolder.CODEC));
    }

    @Override
    public PlayerDamageTracker equalLoot$getDamageContainer() {
        return this.equalLoot$DamageContainer;
    }
}
