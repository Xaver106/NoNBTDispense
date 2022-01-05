/*
*
*
 */

package com.xaver106.nonbtdispense;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public final class NoNBTDispense extends JavaPlugin implements Listener {

    public HashMap<String, StateFlag> flags;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listeners(flags, this), this);
    }

    @Override
    public void onLoad() {
        this.register_flag(new StateFlag("dispense-nbt-spawneggs", true)); //Registering the flag on Load (Important)
    }

    /**
     * Registering a Flag with Worldguard and saving it inside the HashMap
     *
     * @param flag The new Flag to register
     */
    private void register_flag(StateFlag flag) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        if (this.flags == null){
            this.flags = new HashMap<>();
        }
        try {
            registry.register(flag);
            this.flags.put(flag.getName(), flag);
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get(flag.getName());
            if (existing instanceof StateFlag) {
                this.flags.put(existing.getName(), (StateFlag) existing);
            } else {
                this.getLogger().log(Level.SEVERE, "Flag: " + flag.getName() + " could not be registered!");
            }
        }
    }


}
