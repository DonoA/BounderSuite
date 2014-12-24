/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.Servlet.Servlet;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.jackson.map.ObjectMapper;
import ru.tehkode.permissions.PermissionManager;
//Oathbreaker, Thrall, Commoner, Ranger, Artist, Foreman, Artisan, Steward, Enforcer, Valar
/**
 *
 * @author Donovan, aaldim
 */
public class EnforcerSuite extends JavaPlugin{
    @Getter
    private static EnforcerSuite plugin;
    
    @Getter
    private static String prefix = ChatColor.GOLD +"["+ ChatColor.YELLOW +"EnforcerSuite"+ ChatColor.GOLD +"] "+ ChatColor.YELLOW;
    
    @Getter
    private String FileSep = System.getProperty("file.separator");
    
    @Getter @Setter
    private World MainWorld;
    
    @Getter
    private Servlet servlet;
    
    @Getter
    private static ObjectMapper JSonParser;
            
    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        this.reloadConfig();
        int port = this.getConfig().getInt("port");
        JSonParser = new ObjectMapper();
        plugin = this;
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new LoginListen(), this);
        getCommand("punish").setExecutor(new Commands());
        getCommand("done").setExecutor(new Commands());
        MainWorld = Bukkit.getWorld(this.getConfig().getString("MainWorld"));
        servlet = new Servlet(port);
        servlet.start();
    }
    @Override
    public void onDisable(){
        try {
            servlet.getServer().stop();
        } catch (Exception ex) {
            Logger.getLogger(EnforcerSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}