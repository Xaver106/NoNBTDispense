package com.xaver106.nonbtdispense;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public final class NoNBTDispense extends JavaPlugin implements Listener {

    public static StateFlag TEST_FLAG;
    FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();

        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onLoad() {
        this.register_flag(new StateFlag("block-nbt-spawneggs", true));
    }

    private void register_flag(StateFlag flag){
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(flag);
            TEST_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("my-custom-flag");
            if (existing instanceof StateFlag) {
                TEST_FLAG = (StateFlag) existing;
            } else {
                this.getLogger().log(Level.SEVERE, "Flag: " + flag.toString() + " could not be registered!");
            }
        }
    }

    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent event) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getBlock().getLocation()));
        if (!set.testState(null, TEST_FLAG)) {
            NBTItem nbtItem = new NBTItem(event.getItem());
            if (nbtItem.hasKey("EntityTag") && event.getItem().getType().toString().contains("SPAWN_EGG")) {
                event.setCancelled(true);
                Dispenser dispenser = (Dispenser) event.getBlock().getState();

                new BukkitRunnable() {
                    public void run() {
                        dispenser.getSnapshotInventory().remove(event.getItem().getType());
                        dispenser.update();
                    }
                }.runTaskLater(this, 1);
            }
        }

    }
}
