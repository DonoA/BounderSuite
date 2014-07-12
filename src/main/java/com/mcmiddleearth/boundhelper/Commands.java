/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

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
        if(p.hasPermission("BounderHelper.ob")&&args.length>1&&cmd.getName().equalsIgnoreCase("ob")){
            for(int i = 0; i<10; i++){
                Bukkit.getServer().dispatchCommand(sender, "demote " + args[0]);
            }
            DBmanager.OBs.put(args[0], new OathBreaker(Integer.parseInt(args[1]), args[0]));
            if(Bukkit.getServer().getOfflinePlayer(args[0]).isOnline()){
//                DBmanager.OBs.put(args[0], new OathBreaker(Integer.parseInt(args[1])));
                Player ob = Bukkit.getPlayer(args[0]);
                ob.teleport(new Location(Bukkit.getWorld("world"), -4088, 41, -4445));
            }
            OathBreaker ob = new OathBreaker(Integer.parseInt(args[1]), args[0]);
        }else if(cmd.getName().equalsIgnoreCase("done")){
            if(!DBmanager.OBs.containsKey(p.getName())){
                p.sendMessage("You are not OB!");
                return true;
            }else{
                OathBreaker ob = DBmanager.OBs.get(p.getName());
                if(ob.isDone(p.getLocation())){
                    if(ob.severity>1){
                        DBmanager.waitinglist.put(p.getName(), new Date());
                    }else{
                        DBmanager.archive(p.getName());
                        
                    }
                }else{
                    p.sendMessage("You have not reached your location");
                    p.sendMessage("Destination: " + ob.Destination);
                }
                return true;
            }
        }
        return false;
    }
}
