package xyz.jpenilla.antiraidfarm;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AntiRaidFarm extends JavaPlugin implements Listener {
    private final Map<UUID, Long> lastRaidCache = new HashMap<>();
    private int badOmenCooldownSeconds = 180;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        badOmenCooldownSeconds = getConfig().getInt("bad-omen-cooldown-seconds", 180);
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 8660);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getCause() != EntityPotionEffectEvent.Cause.PATROL_CAPTAIN || event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        final Player player = (Player) event.getEntity();
        if (player.hasPermission("antiraidfarm.bypass") || !lastRaidCache.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - lastRaidCache.get(player.getUniqueId())) / 1000 > badOmenCooldownSeconds) {
            lastRaidCache.put(player.getUniqueId(), System.currentTimeMillis());
            return;
        }
        event.setCancelled(true);
    }
}
