package io.github.flemmli97.equalloot.mixin;

import io.github.flemmli97.equalloot.utils.EntityDropsData;
import io.github.flemmli97.equalloot.utils.UniqueItemDrop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements UniqueItemDrop {

    @Shadow
    @Nullable
    private UUID target;

    @Unique
    private boolean equalLoot$unique;

    private ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void setTarget(UUID target);

    @Override
    public void equalLoot$setUniqueItemDropTo(EntityDropsData.PlayerDropContext context) {
        this.setTarget(context.player().getUUID());
        this.setGlowingTag(context.config().glowingDrops());
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
