package io.github.flemmli97.equalloot.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.equalloot.EqualLoot;
import io.github.flemmli97.equalloot.data.LootShareConfig;
import io.github.flemmli97.equalloot.utils.DamageContainerGetter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDamageTracker {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(EqualLoot.MODID, "damage_tracker");

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
                    current != null ? current.add(clamped, this.entity.tickCount) : new DamageHolder(id, clamped, this.entity.tickCount));
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

    public void load(ValueInput.TypedInputList<DamageHolder> tag) {
        this.damageTracker.clear();
        tag.forEach(holder -> this.damageTracker.put(holder.id(), holder));
    }

    public void save(ValueOutput.TypedOutputList<DamageHolder> list) {
        this.damageTracker.values().forEach(list::add);
    }

    public record DamageHolder(UUID id, float amount, int lastHit) {

        public static final Codec<DamageHolder> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(UUIDUtil.CODEC.fieldOf("player").forGetter(DamageHolder::id),
                                Codec.FLOAT.fieldOf("amount").forGetter(DamageHolder::amount),
                                Codec.INT.fieldOf("last_hit").forGetter(DamageHolder::lastHit))
                        .apply(instance, DamageHolder::new)
        );

        private DamageHolder add(float amount, int time) {
            return new DamageHolder(this.id(), this.amount() + amount, time);
        }
    }
}
