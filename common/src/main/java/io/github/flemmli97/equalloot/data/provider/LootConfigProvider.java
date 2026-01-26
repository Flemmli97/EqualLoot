package io.github.flemmli97.equalloot.data.provider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.flemmli97.equalloot.data.LootConfigManager;
import io.github.flemmli97.equalloot.data.LootShareConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class LootConfigProvider implements DataProvider {

    private final Map<ResourceLocation, LootShareConfig> data = new HashMap<>();

    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> lookup;

    public LootConfigProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        this.output = output;
        this.lookup = lookup;
    }

    protected abstract void add(HolderLookup.Provider provider);

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.lookup.thenApply(provider -> {
            this.add(provider);
            return provider;
        }).thenCompose(provider -> CompletableFuture.allOf(this.data.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            Path path = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(id.getNamespace())
                    .resolve(LootConfigManager.DIRECTORY).resolve(id.getPath() + ".json");
            JsonElement obj = LootShareConfig.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), entry.getValue())
                    .getOrThrow();
            return DataProvider.saveStable(cache, obj, path);
        }).toArray(CompletableFuture<?>[]::new)));
    }

    @Override
    public String getName() {
        return "EqualLoot Config Gen";
    }

    public void add(ResourceLocation id, LootShareConfig config) {
        if (this.data.put(id, config) != null) {
            throw new IllegalStateException("Config already added for " + id);
        }
    }
}