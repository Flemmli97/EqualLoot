package io.github.flemmli97.equalloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record LootShareConfig(HolderSet<EntityType<?>> include, HolderSet<EntityType<?>> exclude,
                              int maxPlayers, float minDamage, boolean percentage, int damagedWithin,
                              KillerLoot killerLoot,
                              int priority, boolean glowingDrops) {

    public static final Codec<LootShareConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("include").forGetter(LootShareConfig::include),
                            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("exclude").forGetter(LootShareConfig::exclude),
                            Codec.INT.fieldOf("max_players").forGetter(LootShareConfig::maxPlayers),
                            Codec.FLOAT.fieldOf("min_damage").forGetter(LootShareConfig::minDamage),
                            Codec.BOOL.fieldOf("percentage").forGetter(LootShareConfig::percentage),
                            Codec.INT.fieldOf("damaged_within").forGetter(LootShareConfig::damagedWithin),
                            KillerLoot.CODEC.fieldOf("killer_handling").forGetter(LootShareConfig::killerLoot),
                            Codec.INT.fieldOf("priority").forGetter(LootShareConfig::priority),
                            Codec.BOOL.optionalFieldOf("glowing_drops").forGetter(d -> d.glowingDrops() ? Optional.of(true) : Optional.empty()))
                    .apply(instance, LootShareConfig::new)
    );

    public LootShareConfig(HolderSet<EntityType<?>> include, HolderSet<EntityType<?>> exclude, int maxPlayers, float minDamage, boolean percentage, int damagedWithin, KillerLoot killerLoot, int priority) {
        this(include, exclude, maxPlayers, minDamage, percentage, damagedWithin, killerLoot, priority, true);
    }

    private LootShareConfig(HolderSet<EntityType<?>> include, HolderSet<EntityType<?>> exclude,
                            int maxPlayers, float minDamage, boolean percentage, int damagedWithin,
                            KillerLoot killerLoot,
                            int priority, Optional<Boolean> glowingDrops) {
        this(include, exclude, maxPlayers, minDamage, percentage, damagedWithin, killerLoot, priority, glowingDrops.orElse(false));
    }

    public enum KillerLoot {

        ALWAYS("always"),
        FILL("fill"),
        REQUIREMENTS("requirements");

        public static final Codec<KillerLoot> CODEC = Codec.STRING.flatXmap((s) -> {
            for (KillerLoot val : KillerLoot.values()) {
                if (val.id.equals(s))
                    return DataResult.success(val);
            }
            return DataResult.error(() -> "No such config value " + s + " for KillerLoot");
        }, (e) -> DataResult.success(e.id));

        private final String id;

        KillerLoot(String id) {
            this.id = id;
        }
    }
}
