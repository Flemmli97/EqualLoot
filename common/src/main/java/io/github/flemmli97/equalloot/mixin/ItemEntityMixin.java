package io.github.flemmli97.equalloot.mixin;

import io.github.flemmli97.equalloot.utils.UniqueItemDrop;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements UniqueItemDrop {

    @Shadow
    @Nullable
    private UUID target;

    @Shadow
    public abstract void setTarget(UUID target);

    @Unique
    private boolean equalLoot$unique;

    @Override
    public void equalLoot$setUniqueItemDropTo(UUID target) {
        this.setTarget(target);
        this.equalLoot$unique = true;
    }

    @Override
    public boolean equalLoot$isUniqueItemDrop() {
        return equalLoot$unique;
    }

    @Override
    public UUID equalLoot$GetTarget() {
        return this.target;
    }
}
