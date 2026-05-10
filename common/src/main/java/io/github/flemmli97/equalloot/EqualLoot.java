package io.github.flemmli97.equalloot;

import io.github.flemmli97.equalloot.attachment.PlayerDamageTracker;
import io.github.flemmli97.equalloot.data.LootConfigManager;
import io.github.flemmli97.equalloot.data.LootShareConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;

public class EqualLoot {

    public static final String MODID = "equal_loot";
    public static final Logger LOGGER = LogManager.getLogger("EqualLoot");

    public static boolean handleEntityDrops(LivingEntity entity, DamageSource originalSource, EntityLootTableDrop handler) {
        DropResult result = getPlayersForDrops(entity);
        if (result.players().isEmpty())
            return false;
        result.players().forEach(player -> {
            if (player.equals(originalSource.getEntity()))
                handler.dropFromLootTable(originalSource, result.config());
            else {
                handler.dropFromLootTable(entity.damageSources().playerAttack(player), result.config());
            }
        });
        return true;
    }

    public static DropResult getPlayersForDrops(LivingEntity entity) {
        LootShareConfig config = LootConfigManager.getInstance().get(entity);
        if (config == null)
            return DropResult.EMPTY;
        return new DropResult(PlayerDamageTracker.get(entity).getPlayersForDrops(entity.getServer(), config, entity.getKillCredit()), config);
    }

    public record DropResult(Collection<Player> players, LootShareConfig config) {
        public static final DropResult EMPTY = new DropResult(Collections.emptyList(), null);

    }

    public interface EntityLootTableDrop {

        void dropFromLootTable(DamageSource damageSource, LootShareConfig config);
    }
}
