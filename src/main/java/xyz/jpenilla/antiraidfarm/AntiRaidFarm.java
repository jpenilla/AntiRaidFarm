package xyz.jpenilla.antiraidfarm;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiRaidFarm extends JavaPlugin implements Listener {
    private Cache<UUID, Long> lastRaidCache;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        final int raidCooldownSeconds = this.getConfig().getInt("raid-cooldown-seconds", 180);
        this.lastRaidCache = CacheBuilder.newBuilder()
                .expireAfterWrite(raidCooldownSeconds, TimeUnit.SECONDS)
                .build();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onRaidTrigger(final RaidTriggerEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("antiraidfarm.bypass")) {
            return;
        }
        final boolean hasCooldown = this.lastRaidCache.getIfPresent(player.getUniqueId()) != null;
        if (hasCooldown) {
            event.setCancelled(true);
        } else {
            this.lastRaidCache.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
