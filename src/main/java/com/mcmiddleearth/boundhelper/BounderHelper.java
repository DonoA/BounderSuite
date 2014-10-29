/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

import com.mcmiddleearth.boundhelper.DBamanger.LocationIndex;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
//Oathbreaker, Thrall, Commoner, Ranger, Artist, Foreman, Artisan, Steward, Bounder, Valar
/**
 *
 * @author Donovan
 */
public class BounderHelper extends JavaPlugin{
    public static BounderHelper plugin;
    
    @Getter @Setter
    private World MainWorld;
            
    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        this.reloadConfig();
        plugin = this;
        getCommand("ob").setExecutor(new Commands());
        getCommand("done").setExecutor(new Commands());
        LocationIndex.loadLocs();
        MainWorld = Bukkit.getWorld(this.getConfig().getString("MainWorld"));
    }
    @Override
    public void onDisable(){
        
    }
    
}
