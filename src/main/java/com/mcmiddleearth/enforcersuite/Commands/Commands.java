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

package com.mcmiddleearth.enforcersuite.Commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Records.Infraction;
import com.mcmiddleearth.enforcersuite.Servlet.ServletDBmanager;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
//import ru.tehkode.permissions.bukkit.PermissionsEx;
/**
 *
 * @author Donovan
 */
public class Commands implements CommandExecutor, TabCompleter{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args){
        if(sender instanceof Player){
            Player p = (Player) sender;

            //punish <ob|ban> <name|uuid> <1 | 2> [convo]
            // cmd   args[0]    args[1]   args[2] args[3]

            //ob <name|uuid> <1|2> [convo]
            //ban <name|uuid> <1|appealable> [convo]

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
                        Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p.getUniqueId(), ob.getUniqueId(), ob.getName());
                        inf.setStarted(true);
                        DBmanager.OBs.put(ob.getUniqueId(), inf);
                        for(int j=0; j <= 3; j++){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + ob.getName());
                        }
                        DBmanager.saveOB(ob.getUniqueId()); //save OB to file

                        sender.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                        sender.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                        ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
                        ob.sendMessage(EnforcerSuite.getPrefix()+"Your destination is " + ChatColor.RED + inf.getDestination().getName());
                    }else{
                        sender.sendMessage(EnforcerSuite.getPrefix()+ob.getName() + " is already OB!");
                    }
                }else{
                    if(!DBmanager.loadOB(op.getUniqueId())){
                        if(op.hasPlayedBefore()){
                            Infraction inf = new Infraction(Integer.parseInt(args[1]), "none", p.getUniqueId(), op.getUniqueId());
                            DBmanager.OBs.put(op.getUniqueId(), inf);
                            for(int j=0; j <= 3; j++){
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + op.getName());
                            }
                            DBmanager.saveOB(op.getUniqueId());
                            DBmanager.OBs.remove(op.getUniqueId());
                            sender.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+op.getName());
                            sender.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + op.getUniqueId().toString());
                            ServletDBmanager.Incomplete.add(inf);
                        }else{
                            sender.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                        }
                    }else{
                        DBmanager.OBs.remove(op.getUniqueId());
                        sender.sendMessage(EnforcerSuite.getPrefix() + "That player is already OB!");
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

                if(!op.isBanned()){
                    int type = 0;
                    if(args[1].equalsIgnoreCase("appealable")){
                        type=1;
                    }else if(args[1].equalsIgnoreCase("permanent")){
                        type=2;
                    }
                    if(op.hasPlayedBefore()){
                        Infraction inf = new Infraction(type, "none", p.getUniqueId(), op.getUniqueId(), op.getName());
                        inf.setBan(true);
                        DBmanager.Bans.put(op.getUniqueId(), inf);
                        DBmanager.saveBan(op.getUniqueId());
                        try {
                            LogUtil.printDebug(EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.Bans));
                        } catch (JsonProcessingException ex) {
                            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        op.setBanned(true);
                        if(op.isOnline()){
                            op.getPlayer().kickPlayer("You have been banned");
                        }
                        sender.sendMessage(EnforcerSuite.getPrefix()+"You have Banned "+op.getName());
                        sender.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this Ban, uuid " + op.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                        return true;
                    }else{
                        sender.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                    }
                }else{
                    sender.sendMessage(EnforcerSuite.getPrefix()+op.getName() + " is already Banned!");
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
                    Infraction ob = DBmanager.Bans.get(op.getUniqueId());
                    ob.setFinished(new Date());
                    ob.setDone(true);
                    ob.getBannedOn().remove("build");
                    DBmanager.archiveBan(op.getUniqueId());
                    op.setBanned(false);
                    p.sendMessage(EnforcerSuite.getPrefix() + "You pardoned " + op.getName());
                    return true;
                }else if(DBmanager.OBs.containsKey(op.getUniqueId())){
                    Infraction ob = DBmanager.OBs.get(op.getUniqueId());
                    op.getPlayer().sendMessage(EnforcerSuite.getPrefix() + "You are no longer OB!");
                    ob.setFinished(new Date());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "promote " + op.getName());
                    ob.setRePromoted(true);
                    DBmanager.archiveOB(op.getUniqueId());
                    p.sendMessage(EnforcerSuite.getPrefix() + "You pardoned " + op.getName());
                    return true;
                }else{
                    if(DBmanager.loadBan(op.getUniqueId())){
                        Infraction ob = DBmanager.Bans.get(op.getUniqueId());
                        ob.setFinished(new Date());
                        ob.setDone(true);
                        ob.getBannedOn().remove("build");
                        DBmanager.archiveBan(op.getUniqueId());
                        op.setBanned(false);
                        p.sendMessage(EnforcerSuite.getPrefix() + "You pardoned " + op.getName());
                        return true;
                    }
                    if(DBmanager.loadOB(op.getUniqueId())){
                        Infraction ob = DBmanager.OBs.get(op.getUniqueId());
                        ob.setFinished(new Date());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "promote " + op.getName());
                        ob.setRePromoted(true);
                        DBmanager.archiveOB(op.getUniqueId());
                        p.sendMessage(EnforcerSuite.getPrefix() + "You pardoned " + op.getName());
                        return true;
                    }
                }
                p.sendMessage(EnforcerSuite.getPrefix() + "Failed to pardon " + op.getName());
                return true;
            }else if(cmd.getName().equalsIgnoreCase("suitetool") && p.isOp() && args.length>0 && EnforcerSuite.isDebug()){
                if(args[0].equalsIgnoreCase("dest") && DBmanager.OBs.containsKey(p.getUniqueId())){
                    p.teleport(DBmanager.OBs.get(p.getUniqueId()).getDestination().getCenter());
                }else if(args[0].equalsIgnoreCase("unban") && args.length >= 2){
                    OfflinePlayer op;
                    try{
                        op = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
                    }catch (Exception e){
                        op = Bukkit.getOfflinePlayer(args[0]);
                    }
                    if(op.isBanned()){
                        op.setBanned(false);
                    }
                }
                return true;
            }
            return false;
        }else{
            if(args.length>1&&cmd.getName().equalsIgnoreCase("ob")){
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
                        Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", ob.getUniqueId(), ob.getName());
                        inf.setStarted(true);
                        DBmanager.OBs.put(ob.getUniqueId(), inf);
                        for(int j=0; j <= 3; j++){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + ob.getName());
                        }
                        DBmanager.saveOB(ob.getUniqueId()); //save OB to file

                        sender.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                        sender.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                        ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
                        ob.sendMessage(EnforcerSuite.getPrefix()+"Your destination is " + ChatColor.RED + inf.getDestination().getName());
                    }else{
                        sender.sendMessage(EnforcerSuite.getPrefix()+ob.getName() + " is already OB!");
                    }
                }else{
                    if(!DBmanager.loadOB(op.getUniqueId())){
                        if(op.hasPlayedBefore()){
                            Infraction inf = new Infraction(Integer.parseInt(args[1]), "none", op.getUniqueId());
                            DBmanager.OBs.put(op.getUniqueId(), inf);
                            for(int j=0; j <= 3; j++){
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + op.getName());
                            }
                            DBmanager.saveOB(op.getUniqueId());
                            DBmanager.OBs.remove(op.getUniqueId());
                            sender.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+op.getName());
                            sender.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + op.getUniqueId().toString());
                            ServletDBmanager.Incomplete.add(inf);
                        }else{
                            sender.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                        }
                    }else{
                        DBmanager.OBs.remove(op.getUniqueId());
                        sender.sendMessage(EnforcerSuite.getPrefix() + "That player is already OB!");
                    }
                }
                //When the OB isn't online there file will be loaded on start
                return true;
            }else if(args.length>1&&cmd.getName().equalsIgnoreCase("ban")){
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

                if(!op.isBanned()){
                    int type = 0;
                    if(args[1].equalsIgnoreCase("appealable")){
                        type=1;
                    }else if(args[1].equalsIgnoreCase("permanent")){
                        type=2;
                    }
                    if(op.hasPlayedBefore()){
                        Infraction inf = new Infraction(type, "none", op.getUniqueId(), op.getName());
                        inf.setBan(true);
                        DBmanager.Bans.put(op.getUniqueId(), inf);
                        DBmanager.saveBan(op.getUniqueId());
                        try {
                            LogUtil.printDebug(EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.Bans));
                        } catch (JsonProcessingException ex) {
                            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        op.setBanned(true);
                        if(op.isOnline()){
                            op.getPlayer().kickPlayer("You have been banned");
                        }
                        sender.sendMessage(EnforcerSuite.getPrefix()+"You have Banned "+op.getName());
                        sender.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this Ban, uuid " + op.getUniqueId().toString());
                        ServletDBmanager.Incomplete.add(inf);
                        return true;
                    }else{
                        sender.sendMessage(EnforcerSuite.getPrefix() + "That Player has never played before");
                    }
                }else{
                    sender.sendMessage(EnforcerSuite.getPrefix()+op.getName() + " is already Banned!");
                }

            }
        }
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String Label, String[] args){
        List<String> plrs = new ArrayList<String>();
        for(Player p : Bukkit.getOnlinePlayers()){
            plrs.add(p.getName());
        }
        if(cmd.getName().equalsIgnoreCase("done")){
            return new ArrayList<String>();
        }else if(cmd.getName().equalsIgnoreCase("pardon")&&args.length==0){
            return plrs;
        }else if(cmd.getName().equalsIgnoreCase("ob")){
            if(args.length==0){
                return plrs;
            }else if(args.length==1){
                return Arrays.asList(new String[] {"1", "2"});
            }else{
                return new ArrayList<String>();
            }
        }else if(cmd.getName().equalsIgnoreCase("ban")){
            if(args.length==0){
                return plrs;
            }else if(args.length==1){
                return Arrays.asList(new String[] {"appealable", "permanent"});
            }else{
                return new ArrayList<String>();
            }
        }else if(cmd.getName().equalsIgnoreCase("ob")){
            if(args.length==0){
                return plrs;
            }else if(args.length==1){
                return Arrays.asList(new String[] {"1", "2"});
            }else{
                return new ArrayList<String>();
            }
        }
        return null;
    }
}
