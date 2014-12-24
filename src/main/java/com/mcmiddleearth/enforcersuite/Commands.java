/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.Servlet.ServletDBmanager;
import java.util.Date;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//import ru.tehkode.permissions.bukkit.PermissionsEx;
/**
 *
 * @author Donovan
 */
public class Commands implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd,String Label, String[] args){
        Player p = (Player) sender;
        
        //punish <ob|ban> <name|uuid> <1 | 2> [convo]
        // cmd   args[0]    args[1]   args[2] args[3]
        
        if(p.hasPermission("enforcerHelper.punish")&&args.length>1&&cmd.getName().equalsIgnoreCase("punish")){
            if(args.length < 3){
                return false;
            }
            try{ // check that arg 1 is and int
                Integer.parseInt(args[2]);
            }catch (NumberFormatException e){
                return false;
            }
            OfflinePlayer op;
            try{
                op = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));
            }catch (Exception e){
                op = Bukkit.getOfflinePlayer(args[1]);
            }
            if(op.isOnline()){ //this line still works :P
                Player ob = op.getPlayer();
                if(args[0].equalsIgnoreCase("ob")){
                    if(!DBmanager.OBs.containsKey(ob.getUniqueId())){
                        ob.teleport(EnforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                        Infraction inf = new Infraction(Integer.parseInt(args[2]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob.getUniqueId(), ob.getName());
                        inf.setStarted(true);
                        DBmanager.OBs.put(ob.getUniqueId(), inf);
                        for(int j=0; j <= 3; j++){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + ob.getName());
                        }
                        DBmanager.save(ob.getUniqueId()); //save OB to file

                        p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                        p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                        ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
                        ob.sendMessage(EnforcerSuite.getPrefix()+"Your destination is " + ChatColor.RED + inf.getDestination().getName());
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix()+ob.getName() + " is already OB!");
                    }
                }else if(args[0].equalsIgnoreCase("ban")){
                    if(!ob.isBanned()){
                        if(op.hasPlayedBefore()){
                            Infraction inf = new Infraction(Integer.parseInt(args[2]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob.getUniqueId(), ob.getName());
                            inf.setBan(true);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + ob.getName());
                            DBmanager.saveBan(inf);
                            p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                            p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                            ServletDBmanager.Incomplete.add(inf);
                        }else{
                            
                        }
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix()+ob.getName() + " is already banned!");
                    }
                }else{
                    return false;
                }
            }else{
                if(args[0].equalsIgnoreCase("ob")){
                    if(op.hasPlayedBefore()){
                        Infraction inf = new Infraction(Integer.parseInt(args[2]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, op.getUniqueId());
                        DBmanager.OBs.put(op.getUniqueId(), inf);
                        for(int j=0; j <= 3; j++){
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + op.getName());
                        }
                        DBmanager.save(op.getUniqueId());
                        DBmanager.OBs.remove(op.getUniqueId());
                        p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+op.getName());
                        p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + op.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                    }
                }else if(args[0].equalsIgnoreCase("ban")){
                    if(!op.isBanned()){
                        if(op.hasPlayedBefore()){
                            Infraction inf = new Infraction(Integer.parseInt(args[2]), /*PermissionsEx.getUser(op.getName()).getPrefix()*/ "none", p, op.getUniqueId(), op.getName());
                            inf.setBan(true);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + op.getName());
                            DBmanager.saveBan(inf);
                            p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+op.getName());
                            p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + op.getUniqueId().toString());
                            ServletDBmanager.Incomplete.add(inf);
                        }else{
                            p.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                        }
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix()+op.getName() + " is already banned!");
                    }
                }else{
                    return false;
                }
        }
            //When the OB isn't online there file will be loaded on start
            return true;
        }else if(cmd.getName().equalsIgnoreCase("done")){
            if(!DBmanager.OBs.containsKey(p.getUniqueId())){
                p.sendMessage(EnforcerSuite.getPrefix()+"You are not OB!");
                return true;
            }else{
                Infraction ob = DBmanager.OBs.get(p.getUniqueId());
                if(ob.inDestination(p.getLocation())){
                    if(ob.getSeverity()>1){
                        ob.setFinished(new Date());
                        p.sendMessage(EnforcerSuite.getPrefix() + "You are OB for one more week");
                        ob.setDone(true);
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix() + "You are no longer OB!");
                        ob.setFinished(new Date());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "promote " + p.getName());
                        ob.setRePromoted(true);
                        DBmanager.archive(p.getUniqueId());
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
