package io.github.flemmli97.equalloot.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.flemmli97.equalloot.EqualLoot;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LootConfigManager extends SimpleJsonResourceReloadListener {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EqualLoot.MODID, "entity");
    public static final String DIRECTORY = String.format("%s/%s", ID.getNamespace(), ID.getPath());

    private static final Gson GSON = new Gson();
    private static final Comparator<Map.Entry<ResourceLocation, LootShareConfig>> ORDER = (e1, e2) -> {
        int priority = Integer.compare(e2.getValue().priority(), e1.getValue().priority());
        if (priority == 0)
            return e1.getKey().toString().compareTo(e2.getKey().toString());
        return priority;
    };

    private static LootConfigManager INSTANCE;

    private final Map<EntityType<?>, Optional<LootShareConfig>> cache = new HashMap<>();
    private Map<ResourceLocation, LootShareConfig> config = ImmutableMap.of();

    private final HolderLookup.Provider provider;

    private LootConfigManager(HolderLookup.Provider provider) {
        super(GSON, DIRECTORY);
        this.provider = provider;
    }

    public static LootConfigManager create(HolderLookup.Provider provider) {
        LootConfigManager.INSTANCE = new LootConfigManager(provider);
        return getInstance();
    }

    public static LootConfigManager getInstance() {
        return INSTANCE;
    }

    @Nullable
    public LootShareConfig get(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        Optional<LootShareConfig> value = this.cache.get(type);
        return value == null ? this.calculate(type) : value.orElse(null);
    }

    private LootShareConfig calculate(EntityType<?> type) {
        Holder<EntityType<?>> holder = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(type);
        Optional<LootShareConfig> calculated = this.config.entrySet().stream().sorted(ORDER)
                .filter(e ->
                        e.getValue().include().contains(holder) && !e.getValue().exclude().contains(holder)).findFirst()
                .map(Map.Entry::getValue);
        this.cache.put(type, calculated);
        return calculated.orElse(null);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager manager, ProfilerFiller profiler) {
        this.cache.clear();
        ImmutableMap.Builder<ResourceLocation, LootShareConfig> builder = ImmutableMap.builder();
        DynamicOps<JsonElement> ops = this.provider.createSerializationContext(JsonOps.INSTANCE);
        data.forEach((res, el) -> {
            try {
                LootShareConfig config = LootShareConfig.CODEC.parse(ops, el).getOrThrow();
                builder.put(res, config);
            } catch (Exception ex) {
                EqualLoot.LOGGER.error("Couldn't parse config json {} {}", res, ex, ex.fillInStackTrace());
            }
        });
        this.config = builder.build();
    }
}