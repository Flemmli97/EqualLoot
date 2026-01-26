package io.github.flemmli97.equalloot.mixin;

import io.github.flemmli97.equalloot.attachment.PlayerDamageTracker;
import io.github.flemmli97.equalloot.utils.DamageContainerGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
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
    private void loadData(CompoundTag compound, CallbackInfo info) {
        this.equalLoot$DamageContainer.load(compound.getCompound(PlayerDamageTracker.ID.toString()));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void saveData(CompoundTag compound, CallbackInfo info) {
        compound.put(PlayerDamageTracker.ID.toString(), this.equalLoot$DamageContainer.save());
    }

    @Override
    public PlayerDamageTracker equalLoot$getDamageContainer() {
        return this.equalLoot$DamageContainer;
    }
}
