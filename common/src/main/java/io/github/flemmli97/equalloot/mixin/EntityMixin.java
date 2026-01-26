package io.github.flemmli97.equalloot.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.flemmli97.equalloot.utils.EntityDropsData;
import io.github.flemmli97.equalloot.utils.UniqueItemDrop;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDropsData {

    @Shadow
    public abstract ItemEntity spawnAtLocation(ItemStack stack, float offsetY);

    @Unique
    private Player equalLoot$playerDropContext;
    @Unique
    private CustomDropsData equalLoot$custom;

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"))
    private void modifyItemEntity(ItemStack stack, float offsetY, CallbackInfoReturnable<ItemEntity> info, @Local ItemEntity entity) {
        if (this.equalLoot$playerDropContext() != null) {
            ((UniqueItemDrop) entity).equalLoot$setUniqueItemDropTo(this.equalLoot$playerDropContext().getUUID());
        }
        if (this.equalLoot$custom != null && this.equalLoot$custom.predicate().test(entity)
                && !this.equalLoot$custom.players().isEmpty()) {
            CustomDropsData current = this.equalLoot$custom;
            this.equalLoot$custom = null;
            Player first = current.players().stream().findFirst().get();
            current.players().forEach(player -> {
                if (player != first) {
                    this.equalLoot$setPlayerDropContext(player);
                    this.spawnAtLocation(entity.getItem().copy(), offsetY);
                    this.equalLoot$setPlayerDropContext(null);
                }
            });
            ((UniqueItemDrop) entity).equalLoot$setUniqueItemDropTo(first.getUUID());
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
    public Player equalLoot$playerDropContext() {
        return equalLoot$playerDropContext;
    }

    @Override
    public void equalLoot$setPlayerDropContext(Player player) {
        this.equalLoot$playerDropContext = player;
    }

    @Override
    public void equalLoot$setAdditionalDropsFor(CustomDropsData customDropsData) {
        this.equalLoot$custom = customDropsData;
    }
}
