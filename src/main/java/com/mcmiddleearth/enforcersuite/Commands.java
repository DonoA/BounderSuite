/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBamanger.DBmanager;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan
 */
public class Commands implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd,String Label, String[] args){
        Player p = (Player) sender;
        if(p.hasPermission("enforcerHelper.ob")&&args.length>1&&cmd.getName().equalsIgnoreCase("ob")){
            //demote
            if(Bukkit.getServer().getOfflinePlayer(args[0]).isOnline()){
                Player ob = Bukkit.getPlayer(args[0]);
                ob.teleport(enforcerSuite.plugin.getMainWorld().getSpawnLocation());
            }
        }else if(cmd.getName().equalsIgnoreCase("done")){
            if(!DBmanager.OBs.containsKey(p.getName())){
                p.sendMessage("You are not OB!");
                return true;
            }else{
                OathBreaker ob = DBmanager.OBs.get(p.getName());
                if(ob.isDone(p.getLocation())){
                    if(ob.getSev()>1){
                        ob.setFin(new Date());
                    }else{
                        DBmanager.archive(p.getName());
                    }
                }else{
                    p.sendMessage("You have not reached your location");
                    p.sendMessage("Destination: " + ob.getDestination().getName());
                }
                return true;
            }
        }
        return false;
    }
}
