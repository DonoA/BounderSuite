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
            if(Bukkit.getServer().getOfflinePlayer(args[0]).isOnline()){
                Player ob = Bukkit.getPlayer(args[0]);
                ob.teleport(EnforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                
                DBmanager.OBs.put(ob.getUniqueId(), new Infraction(Integer.parseInt(args[1]), /*PermissionsEx.getUser(ob.getName()).getPrefix()*/ "none", p, ob));
                for(int j=0; j <= 3; j++){
                    Bukkit.dispatchCommand(sender, "demote " + args[0]);
                }
                DBmanager.save(ob.getUniqueId()); //save OB to file
                
                p.sendMessage(EnforcerSuite.getPrefix()+"You have OathBreakered "+ob.getName());
                ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
            }
            //When the OB isn't online...
            /* 
            else if (!Bukkit.getServer().getOfflinePlayer(args[0]).isOnline()) {
                Bukkit.broadcastMessage(enforcerSuite.getPrefix()+"OB ISN'T ONLINE");   
            }
            */
            return true;
        }else if(cmd.getName().equalsIgnoreCase("done")){
            if(!DBmanager.OBs.containsKey(p.getUniqueId())){
                p.sendMessage(EnforcerSuite.getPrefix()+"You are not OB!");
                return true;
            }else{
                Infraction ob = DBmanager.OBs.get(p.getUniqueId());
                if(ob.isDone()){
                    if(ob.getSeverity()>1){
                        ob.setFinished(new Date());
                    }else{
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
