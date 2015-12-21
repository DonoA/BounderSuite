/*
 * This file is part of EnforcerSuite.
 * 
 * EnforcerSuite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EnforcerSuite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with EnforcerSuite.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.enforcersuite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmiddleearth.enforcersuite.Listeners.LoginListen;
import com.mcmiddleearth.enforcersuite.Commands.Commands;
import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.Listeners.SaveListen;
import com.mcmiddleearth.enforcersuite.Servlet.Servlet;
import com.mcmiddleearth.enforcersuite.Servlet.ServletDBmanager;
import com.mcmiddleearth.enforcersuite.Servlet.ServletHandle;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.io.File;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

//Oathbreaker, Thrall, Commoner, Ranger, Artist, Foreman, Artisan, Steward, Enforcer, Valar
/**
 *
 * @author Donovan, aaldim
 */
public class EnforcerSuite extends JavaPlugin{
    @Getter
    private static EnforcerSuite plugin;
    
    @Getter
    private final static String prefix = ChatColor.GOLD +"["+ ChatColor.YELLOW +"EnforcerSuite"+ ChatColor.GOLD +"] "+ ChatColor.YELLOW;
    
    @Getter
    private final String FileSep = System.getProperty("file.separator");
    
    @Getter @Setter
    private World MainWorld;
    
    @Getter
    private Servlet servlet;
    
    @Getter
    private static ObjectMapper JSonParser;
    
    @Getter
    private static boolean Debug = true;
            
    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        this.reloadConfig();
        if(!new File(this.getDataFolder() + FileSep + "Destination-DB").exists()){
            LogUtil.printErr("No Destinations found, terminating plugin");
            this.getPluginLoader().disablePlugin(this);
        }
        JSonParser = new ObjectMapper();
        plugin = this;
        Debug = true;//this.getConfig().getBoolean("debug");
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new LoginListen(), this);
        pm.registerEvents(new SaveListen(), this);
        getCommand("ob").setExecutor(new Commands());
        getCommand("ban").setExecutor(new Commands());
        getCommand("done").setExecutor(new Commands());
        getCommand("getinfo").setExecutor(new Commands());
        getCommand("pardon").setExecutor(new Commands());
        getCommand("suitetool").setExecutor(new Commands());
        getCommand("ob").setTabCompleter(new Commands());
        getCommand("ban").setTabCompleter(new Commands());
        getCommand("done").setTabCompleter(new Commands());
        getCommand("getinfo").setTabCompleter(new Commands());
        getCommand("pardon").setTabCompleter(new Commands());
        getCommand("suitetool").setTabCompleter(new Commands());
        MainWorld = Bukkit.getWorld(this.getConfig().getString("MainWorld"));
        if(MainWorld == null){
            MainWorld = Bukkit.getWorlds().get(0);
        }
        if(this.getConfig().getBoolean("useServlet")){
            int port = this.getConfig().getInt("port");
            servlet = new Servlet(port);
            servlet.start();
        }
        (new ServletHandle.TCPconnect()).start();
    }
    @Override
    public void onDisable(){
        try {
            servlet.getServer().stop();
        } catch (Exception ex) {
            LogUtil.printErr("Failed to stop EnforcerSuite");
            LogUtil.printDebugStack(ex);
        }finally{
            for(UUID uuid : DBmanager.Bans.keySet()){
                DBmanager.saveBan(uuid);
            }
            for(UUID uuid : DBmanager.OBs.keySet()){
                DBmanager.saveOB(uuid);
            }
            ServletDBmanager.saveIncomplete();
        }
    }
}