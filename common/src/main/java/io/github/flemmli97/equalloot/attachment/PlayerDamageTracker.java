package io.github.flemmli97.equalloot.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.equalloot.EqualLoot;
import io.github.flemmli97.equalloot.data.LootShareConfig;
import io.github.flemmli97.equalloot.utils.DamageContainerGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDamageTracker {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EqualLoot.MODID, "damage_tracker");

    private final LivingEntity entity;
    private final Map<UUID, DamageHolder> damageTracker = new HashMap<>();

    public PlayerDamageTracker(LivingEntity entity) {
        this.entity = entity;
    }

    public static PlayerDamageTracker get(LivingEntity entity) {
        return ((DamageContainerGetter) entity).equalLoot$getDamageContainer();
    }

    public void onIncomingDamage(DamageSource damageSource, float amount) {
        if (damageSource.getEntity() instanceof Player player) {
            float clamped = Math.min(amount, this.entity.getHealth());
            this.damageTracker.compute(player.getUUID(), (id, current) ->
                    current != null ? current.add(clamped, this.entity.tickCount) : new DamageHolder(clamped, this.entity.tickCount));
        }
    }

    public Set<Player> getPlayersForDrops(MinecraftServer server, LootShareConfig config, @Nullable LivingEntity killer) {
        Set<Player> players = new HashSet<>();
        List<Map.Entry<UUID, DamageHolder>> entries = this.damageTracker.entrySet().stream()
                .sorted((e1, e2) ->
                        Float.compare(e2.getValue().amount(), e1.getValue().amount())).toList();
        for (Map.Entry<UUID, DamageHolder> entry : entries) {
            float minDamageRequirement = config.percentage() ? this.entity.getMaxHealth() * config.minDamage() : config.minDamage();
            if (entry.getValue().amount() >= minDamageRequirement && (this.entity.tickCount - entry.getValue().lastHit()) <= config.damagedWithin()) {
                Player player = server.getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    players.add(player);
                }
                if (config.maxPlayers() > 0 && players.size() >= config.maxPlayers())
                    break;
            }
        }
        if (killer instanceof Player player) {
            switch (config.killerLoot()) {
                case FILL -> {
                    if (players.size() < config.maxPlayers()) {
                        players.add(player);
                    }
                }
                case ALWAYS -> players.add(player);
            }
        }
        return players;
    }

    public void load(CompoundTag tag) {
        this.damageTracker.clear();
        tag.getAllKeys().forEach(id -> {
            this.damageTracker.put(UUID.fromString(id), DamageHolder.CODEC.parse(NbtOps.INSTANCE, tag.get(id)).getOrThrow());
        });
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        this.damageTracker.forEach((id, holder) -> {
            tag.put(id.toString(), DamageHolder.CODEC.encodeStart(NbtOps.INSTANCE, holder).getOrThrow());
        });
        return tag;
    }

    private record DamageHolder(float amount, int lastHit) {

        public static final Codec<DamageHolder> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Codec.FLOAT.fieldOf("amount").forGetter(DamageHolder::amount),
                                Codec.INT.fieldOf("last_hit").forGetter(DamageHolder::lastHit))
                        .apply(instance, DamageHolder::new)
        );

        private DamageHolder add(float amount, int time) {
            return new DamageHolder(this.amount() + amount, time);
        }
    }
}
