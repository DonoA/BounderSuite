/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.Servlet.RequestKey;
import com.mcmiddleearth.enforcersuite.Servlet.ServletDBmanager;
import com.mcmiddleearth.enforcersuite.Servlet.ServletHandle;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;
/**
 *
 * @author Donovan
 */
public class Commands implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd,String Label, String[] args){
        Player p = (Player) sender;
        if(p.hasPermission("enforcerHelper.ob")&&args.length>1&&cmd.getName().equalsIgnoreCase("ob")){
            try{ // check that args[1] is and int
                Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                return false;
            }
            //demote
            if(Bukkit.getOfflinePlayer(args[0]).isOnline()){
                Player ob = Bukkit.getPlayer(args[0]);
                if(!DBmanager.OBs.containsKey(ob.getUniqueId())){
                    ob.teleport(EnforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                    Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob.getUniqueId(), ob.getName());
                    inf.setStarted(true);
                    DBmanager.OBs.put(ob.getUniqueId(), inf);
                    for(int j=0; j <= 3; j++){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + args[0]);
                    }
                    DBmanager.save(ob.getUniqueId()); //save OB to file

                    p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                    InetAddress addr;
                    try {
                        addr = InetAddress.getLocalHost();
                        String ip = p.getAddress().toString().replace("/", "");
                        ip = ip.substring(0, ip.indexOf(":"));
                        RequestKey key = new RequestKey(String.valueOf(new Date().getTime()), inf, ip);
                        p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                        ServletDBmanager.Keys.put(ip, key);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
                    ob.sendMessage(EnforcerSuite.getPrefix()+"Your destination is " + ChatColor.RED + inf.getDestination().getName());
                }else{
                    p.sendMessage(ob.getName() + " is already OB!");
                }
            }else{
                OfflinePlayer ob = Bukkit.getOfflinePlayer(args[0]);
                Infraction inf = new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob.getUniqueId());
                for(int j=0; j <= 3; j++){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + args[0]);
                }
                DBmanager.save(ob.getUniqueId());
                p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                InetAddress addr;
                try {
                    addr = InetAddress.getLocalHost();
                    String ip = p.getAddress().toString().replace("/", "");
                    ip = ip.substring(0, ip.indexOf(":"));
                    RequestKey key = new RequestKey(String.valueOf(new Date().getTime()), inf, ip);
                    p.sendMessage(EnforcerSuite.getPrefix()+"Connect to the forums to finish this OB, uuid " + ob.getUniqueId().toString());
                    ServletDBmanager.Keys.put(ip, key);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
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
        }else if(p.hasPermission("enforcerHelper.ob")&&cmd.getName().equalsIgnoreCase("webregister")){
            InetAddress addr;
            try {
                addr = InetAddress.getLocalHost();
                String ip = p.getAddress().toString().replace("/", "");
                ip = ip.substring(0, ip.indexOf(":"));
                RequestKey key = new RequestKey(String.valueOf(new Date().getTime()), ip, p.getName());
                p.sendMessage(EnforcerSuite.getPrefix()+"Connect to " + addr.getHostName() + ":" + EnforcerSuite.getPlugin().getServlet().getBoundPort() + "/form/ to finish this OB");
                ServletDBmanager.Keys.put(ip, key);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}
