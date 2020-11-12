package xyz.jpenilla.antiraidfarm;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class AntiRaidFarm extends JavaPlugin implements Listener {
    private Cache<UUID, Long> lastRaidCache;
    private int raidCooldownSeconds = 180;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        raidCooldownSeconds = getConfig().getInt("raid-cooldown-seconds", 180);
        lastRaidCache = CacheBuilder.newBuilder()
                .expireAfterWrite(raidCooldownSeconds, TimeUnit.SECONDS)
                .build();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("antiraidfarm.bypass")) {
            return;
        }
        final boolean hasCooldown = lastRaidCache.getIfPresent(player.getUniqueId()) != null;
        if (hasCooldown) {
            event.setCancelled(true);
        } else {
            lastRaidCache.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
