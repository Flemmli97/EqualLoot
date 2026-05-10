package io.github.flemmli97.equalloot.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.flemmli97.equalloot.utils.EntityDropsData;
import io.github.flemmli97.equalloot.utils.UniqueItemDrop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDropsData {

    @Shadow
    public abstract ItemEntity spawnAtLocation(ServerLevel level, ItemStack stack, Vec3 offset);

    @Unique
    private PlayerDropContext equalLoot$playerDropContext;
    @Unique
    private CustomDropsData equalLoot$custom;

    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"))
    private void modifyItemEntity(ServerLevel level, ItemStack stack, Vec3 offset, CallbackInfoReturnable<ItemEntity> info, @Local ItemEntity entity) {
        if (this.equalLoot$playerDropContext() != null) {
            ((UniqueItemDrop) entity).equalLoot$setUniqueItemDropTo(this.equalLoot$playerDropContext());
        }
        if (this.equalLoot$custom != null && this.equalLoot$custom.predicate().test(entity)
                && !this.equalLoot$custom.result().players().isEmpty()) {
            CustomDropsData current = this.equalLoot$custom;
            this.equalLoot$custom = null;
            Player first = current.result().players().stream().findFirst().get();
            current.result().players().forEach(player -> {
                if (player != first) {
                    this.equalLoot$setPlayerDropContext(new PlayerDropContext(player, current.result().config()));
                    this.spawnAtLocation(level, entity.getItem().copy(), offset);
                    this.equalLoot$setPlayerDropContext(null);
                }
            });
            ((UniqueItemDrop) entity).equalLoot$setUniqueItemDropTo(new PlayerDropContext(first, current.result().config()));
            this.equalLoot$custom = current;
        }
    }

    @Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
    private void checkBroadcast(ServerPlayer player, CallbackInfoReturnable<Boolean> info) {
        if (this instanceof UniqueItemDrop drop && drop.equalLoot$isUniqueItemDrop() && drop.equalLoot$GetTarget() != null) {
            info.setReturnValue(player.getUUID().equals(drop.equalLoot$GetTarget()));
        }
    }

    @Override
    public PlayerDropContext equalLoot$playerDropContext() {
        return equalLoot$playerDropContext;
    }

    @Override
    public void equalLoot$setPlayerDropContext(PlayerDropContext context) {
        this.equalLoot$playerDropContext = context;
    }

    @Override
    public void equalLoot$setAdditionalDropsFor(CustomDropsData customDropsData) {
        this.equalLoot$custom = customDropsData;
    }
}
