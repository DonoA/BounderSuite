/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

import org.bukkit.plugin.java.JavaPlugin;
//Oathbreaker, Thrall, Commoner, Ranger, Artist, Foreman, Artisan, Steward, Bounder, Valar
/**
 *
 * @author Donovan
 */
public class BounderHelper extends JavaPlugin{
    public static BounderHelper plugin;
    @Override
    public void onEnable(){
        plugin = this;
        getCommand("ob").setExecutor(new Commands());
        getCommand("done").setExecutor(new Commands());
    }
    @Override
    public void onDisable(){
        
    }
    
}
