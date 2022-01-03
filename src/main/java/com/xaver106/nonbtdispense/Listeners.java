package com.xaver106.nonbtdispense;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Listeners implements Listener {

    private final HashMap<String, StateFlag> flags;
    private final Plugin plugin;

    public Listeners(HashMap<String, StateFlag> flags, Plugin plugin){

        this.flags = flags;
        this.plugin = plugin;

    }

    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent event) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getBlock().getLocation()));

        // Testing for the Dispense NBT SpawnEggs Flag
        if (!set.testState(null, flags.get("dispense-nbt-spawneggs"))) {
            NBTItem nbtItem = new NBTItem(event.getItem());
            if (nbtItem.hasKey("EntityTag") && event.getItem().getType().toString().contains("SPAWN_EGG")) {
                event.setCancelled(true);
                Dispenser dispenser = (Dispenser) event.getBlock().getState();

                new BukkitRunnable() {
                    public void run() {
                        dispenser.getSnapshotInventory().remove(event.getItem().getType());
                        dispenser.update();
                    }
                }.runTaskLater(plugin, 1);
            }
        }

    }

}
