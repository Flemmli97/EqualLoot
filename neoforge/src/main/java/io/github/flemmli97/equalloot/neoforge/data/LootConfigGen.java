package io.github.flemmli97.equalloot.neoforge.data;

import io.github.flemmli97.equalloot.EqualLoot;
import io.github.flemmli97.equalloot.data.LootShareConfig;
import io.github.flemmli97.equalloot.data.provider.LootConfigProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EqualLoot.MODID)
public class LootConfigGen extends LootConfigProvider {

    public LootConfigGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @SubscribeEvent
    public static void data(GatherDataEvent.Server event) {
        DataGenerator data = event.getGenerator();
        data.addProvider(true, new LootConfigGen(data.getPackOutput(), event.getLookupProvider()));
    }

    @Override
    protected void add(HolderLookup.Provider provider) {
        this.add(Identifier.fromNamespaceAndPath(EqualLoot.MODID, "bosses"), new LootShareConfig(provider.lookupOrThrow(Registries.ENTITY_TYPE)
                .getOrThrow(Tags.EntityTypes.BOSSES), HolderSet.empty(), 5,
                0.05f, true,
                300, LootShareConfig.KillerLoot.FILL, 10));
    }
}
