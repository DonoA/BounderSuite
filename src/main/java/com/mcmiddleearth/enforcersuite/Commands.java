/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
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
                ob.teleport(enforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                
                DBmanager.save(ob.getName()); //save OB to file
                //DBmanager.OBs.put(ob.getName(),OB);
                
                p.sendMessage(enforcerSuite.getPrefix()+"You have OB'ed "+ob.getName());
                ob.sendMessage(enforcerSuite.getPrefix()+"You are an OB now");
            }
            //When the OB isn't online...
            /* 
            else if (!Bukkit.getServer().getOfflinePlayer(args[0]).isOnline()) {
                Bukkit.broadcastMessage(enforcerSuite.getPrefix()+"OB ISN'T ONLINE");   
            }
            */
            return true;
        }else if(cmd.getName().equalsIgnoreCase("done")){
            if(!DBmanager.OBs.containsKey(p.getName())){
                p.sendMessage(enforcerSuite.getPrefix()+"You are not OB!");
                return true;
            }else{
                OathBreaker ob = DBmanager.OBs.get(p.getName());
                if(ob.isDone()){
                    if(ob.getSeverity()>1){
                        ob.setFinished(new Date());
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
