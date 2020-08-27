package xyz.jpenilla.antiraidfarm;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AntiRaidFarm extends JavaPlugin implements Listener {
    private final Map<UUID, Long> lastRaidCache = new HashMap<>();
    private int raidCooldownSeconds = 180;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        raidCooldownSeconds = getConfig().getInt("raid-cooldown-seconds", 180);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("antiraidfarm.bypass") || !lastRaidCache.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - lastRaidCache.get(player.getUniqueId())) / 1000 > raidCooldownSeconds) {
            return;
        }
        lastRaidCache.put(player.getUniqueId(), System.currentTimeMillis());
        event.setCancelled(true);
    }
}
