package com.xaver106.nonbtdispense;

import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class NoNBTDispense extends JavaPlugin implements Listener {

    FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent event) {
        event.setCancelled(true);
        Dispenser dispenser = (Dispenser) event.getBlock().getState();

        new BukkitRunnable(){
            public void run(){
                dispenser.getSnapshotInventory().remove(event.getItem().getType());
                dispenser.update();
            }
        }.runTaskLater(this, 1);

    }
}
