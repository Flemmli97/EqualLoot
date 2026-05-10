package io.github.flemmli97.equalloot.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.flemmli97.equalloot.EqualLoot;
import io.github.flemmli97.equalloot.utils.EntityDropsData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingDropHandlerMixin {

    /**
     * Modifies loot table drops to be for each player
     */
    @WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V"))
    private void onLootTableDeathDrop(LivingEntity instance, ServerLevel level, DamageSource damageSource, boolean playerKill, Operation<Void> original) {
        if (!EqualLoot.handleEntityDrops((LivingEntity) (Object) this, damageSource, (source, config) -> {
            ((EntityDropsData) this).equalLoot$setPlayerDropContext(new EntityDropsData.PlayerDropContext((Player) source.getEntity(), config));
            original.call(instance, level, source, playerKill);
            ((EntityDropsData) this).equalLoot$setPlayerDropContext(null);
        })) {
            original.call(instance, level, damageSource, playerKill);
        }
    }

    /**
     * Makes the builder also use the relevant players luck instead of just the last player that hit it
     */
    @Inject(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;create(Lnet/minecraft/util/context/ContextKeySet;)Lnet/minecraft/world/level/storage/loot/LootParams;"))
    private void modifyLootParams(ServerLevel level, DamageSource damageSource, boolean playerKill, ResourceKey<LootTable> lootTable, Consumer<ItemStack> dropConsumer, CallbackInfo info, @Local LootParams.Builder builder) {
        if (playerKill && ((EntityDropsData) this).equalLoot$playerDropContext() != null) {
            builder.withLuck(((EntityDropsData) this).equalLoot$playerDropContext().player().getLuck());
        }
    }

    /**
     * Special case handling only for wither
     */
    @WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropCustomDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V"))
    private void onCustomDeathDrop(LivingEntity instance, ServerLevel level, DamageSource damageSource, boolean recentlyHit, Operation<Void> original) {
        if ((Object) this instanceof WitherBoss wither)
            ((EntityDropsData) this).equalLoot$setAdditionalDropsFor(new EntityDropsData.CustomDropsData(EqualLoot.getPlayersForDrops(wither), e -> e.getItem().is(Items.NETHER_STAR)));
        original.call(instance, level, damageSource, recentlyHit);
        ((EntityDropsData) this).equalLoot$setAdditionalDropsFor(null);
    }
}
