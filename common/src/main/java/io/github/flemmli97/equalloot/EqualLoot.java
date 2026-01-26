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
        Collection<Player> players = getPlayersForDrops(entity);
        if (players.isEmpty())
            return false;
        players.forEach(player -> {
            if (player.equals(originalSource.getEntity()))
                handler.dropFromLootTable(originalSource);
            else {
                handler.dropFromLootTable(entity.damageSources().playerAttack(player));
            }
        });
        return true;
    }

    public static Collection<Player> getPlayersForDrops(LivingEntity entity) {
        LootShareConfig config = LootConfigManager.getInstance().get(entity);
        if (config == null)
            return Collections.emptyList();
        return PlayerDamageTracker.get(entity).getPlayersForDrops(entity.getServer(), config, entity.getKillCredit());
    }

    public interface EntityLootTableDrop {

        void dropFromLootTable(DamageSource damageSource);
    }
}
