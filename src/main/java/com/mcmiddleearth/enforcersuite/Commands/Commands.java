/*
 * This file is part of BoundHelper.
 * 
 * BoundHelper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BoundHelper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BoundHelper.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.enforcersuite.Commands;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Records.Infraction;
import com.mcmiddleearth.enforcersuite.Servlet.ServletDBmanager;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
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
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args){
        Player p = (Player) sender;
        
        //punish <ob|ban> <name|uuid> <1 | 2> [convo]
        // cmd   args[0]    args[1]   args[2] args[3]
        
        //ob <name|uuid> <1|2> [convo]
        //ban <name|uuid> <1|2> [convo]
        
        if(p.hasPermission("enforcerHelper.punish")&&args.length>1&&cmd.getName().equalsIgnoreCase("ob")){
            if(args.length < 2){
                return false;
            }
            try{ // check that arg 1 is and int
                Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                return false;
            }
            OfflinePlayer op;
            String opName = "nill";
            try{
                op = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
            }catch (Exception e){
                op = Bukkit.getOfflinePlayer(args[0]);
                opName = args[0];
            }
            if(op.isOnline()){
                Player ob = op.getPlayer();
                if(!DBmanager.OBs.containsKey(ob.getUniqueId())){
                    ob.teleport(EnforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                    Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob.getUniqueId(), ob.getName());
                    inf.setStarted(true);
                    DBmanager.OBs.put(ob.getUniqueId(), inf);
                    for(int j=0; j <= 3; j++){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + ob.getName());
                    }
                    DBmanager.saveOB(ob.getUniqueId()); //save OB to file

                    p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                    p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                    ServletDBmanager.Incomplete.add(inf);
                    ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
                    ob.sendMessage(EnforcerSuite.getPrefix()+"Your destination is " + ChatColor.RED + inf.getDestination().getName());
                }else{
                    p.sendMessage(EnforcerSuite.getPrefix()+ob.getName() + " is already OB!");
                }
            }else{
                if(!DBmanager.loadOB(op.getUniqueId())){
                    if(op.hasPlayedBefore()){
                        Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, op.getUniqueId());
                        DBmanager.OBs.put(op.getUniqueId(), inf);
                        for(int j=0; j <= 3; j++){
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + op.getName());
                        }
                        DBmanager.saveOB(op.getUniqueId());
                        DBmanager.OBs.remove(op.getUniqueId());
                        p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+op.getName());
                        p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + op.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                    }
                }else{
                    DBmanager.OBs.remove(op.getUniqueId());
                    p.sendMessage(EnforcerSuite.getPrefix() + "That player is already OB!");
                }
            }
            //When the OB isn't online there file will be loaded on start
            return true;
        }else if(p.hasPermission("enforcerHelper.punish")&&args.length>1&&cmd.getName().equalsIgnoreCase("ban")){
            if(args.length < 2){
                return false;
            }
            try{ // check that arg 1 is and int
                Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                return false;
            }
            OfflinePlayer op;
            String opName = "nill";
            try{
                op = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
            }catch (Exception e){
                op = Bukkit.getOfflinePlayer(args[0]);
                opName = args[0];
            }
            if(op.isOnline()){
                Player ob = op.getPlayer();
                if(!ob.isBanned()){
                    if(op.hasPlayedBefore()){
                        Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob.getUniqueId(), ob.getName());
                        inf.setBan(true);
                        DBmanager.Bans.put(op.getUniqueId(), inf);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + op.getName());
                        DBmanager.saveBan(op.getUniqueId());
                        p.sendMessage(EnforcerSuite.getPrefix()+"You have Banned "+ob.getName());
                        p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this Ban, uuid " + ob.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                    }
                }else{
                    p.sendMessage(EnforcerSuite.getPrefix()+ob.getName() + " is already Banned!");
                }
            }else{
                if(!op.isBanned()){
                    if(op.hasPlayedBefore()){
                        Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(op.getName()).getPrefix()*/ "none", p, op.getUniqueId(), op.getName());
                        inf.setBan(true);
                        LogUtil.printDebug(inf.toString());
                        DBmanager.Bans.put(op.getUniqueId(), inf);
                        op.setBanned(true);
                        if(op.isOnline()){
                            op.getPlayer().kickPlayer("You have been banned");
                        }
                        DBmanager.saveBan(op.getUniqueId());
                        p.sendMessage(EnforcerSuite.getPrefix()+"You have Banned "+op.getName());
                        p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this Ban, uuid " + op.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                    }else{
                        p.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                    }
                }else{
                    p.sendMessage(EnforcerSuite.getPrefix()+op.getName() + " is already Banned!");
                }
            }
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
                        ob.setDone(true);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "promote " + p.getName());
                        ob.setRePromoted(true);
                        DBmanager.archiveOB(p.getUniqueId());
                    }
                }else{
                    p.sendMessage("You have not reached your location");
                    p.sendMessage("Destination: " + ob.getDestination().getName());
                }
                return true;
            }
        }else if(cmd.getName().equalsIgnoreCase("getinfo") && p.hasPermission("enforcerHelper.punish") && args.length>0){
            OfflinePlayer op;
            try{
                op = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
            }catch (Exception e){
                op = Bukkit.getOfflinePlayer(args[0]);
            }
            if(!DBmanager.OBs.containsKey(op.getUniqueId())){
                if(!DBmanager.loadOB(op.getUniqueId())){
                    p.sendMessage(EnforcerSuite.getPrefix()+ " " + op.getName() + " does not have a record");
                    return true;
                }else{
                    p.sendMessage(EnforcerSuite.getPrefix() + " " + DBmanager.OBs.get(op.getUniqueId()).toString());
                    DBmanager.OBs.remove(op.getUniqueId());
                    return true;
                }
            }else{
                p.sendMessage(EnforcerSuite.getPrefix() + " " + DBmanager.OBs.get(op.getUniqueId()).toString());
                return true;
            }
        }else if(cmd.getName().equalsIgnoreCase("pardon") && p.hasPermission("enforcerHelper.punish") && args.length>0){
            OfflinePlayer op;
            try{
                op = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
            }catch (Exception e){
                op = Bukkit.getOfflinePlayer(args[0]);
            }
            if(DBmanager.Bans.containsKey(op.getUniqueId())){
                Infraction ob = DBmanager.OBs.get(p.getUniqueId());
                ob.setFinished(new Date());
                ob.setDone(true);
                ob.getBannedOn().remove("build");
                DBmanager.archiveBan(op.getUniqueId());
                op.setBanned(false);
            }else if(DBmanager.OBs.containsKey(op.getUniqueId())){
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
                        DBmanager.archiveOB(p.getUniqueId());
                    }
                }
            }else{
                if(DBmanager.loadBan(op.getUniqueId())){
                    Infraction ob = DBmanager.OBs.get(p.getUniqueId());
                    ob.setFinished(new Date());
                    ob.setDone(true);
                    ob.getBannedOn().remove("build");
                    DBmanager.archiveBan(op.getUniqueId());
                    op.setBanned(false);
                }
                if(DBmanager.loadOB(op.getUniqueId())){
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
                            DBmanager.archiveOB(p.getUniqueId());
                        }
                    }
                }
            }
            return true;
        }else if(cmd.getName().equalsIgnoreCase("suitetool") && p.isOp() && args.length>0){
            if(args[0].equalsIgnoreCase("dest") && DBmanager.OBs.containsKey(p.getUniqueId())){
                p.teleport(DBmanager.OBs.get(p.getUniqueId()).getDestination().getCenter());
            }
            return true;
        }
        return false;
    }
}
