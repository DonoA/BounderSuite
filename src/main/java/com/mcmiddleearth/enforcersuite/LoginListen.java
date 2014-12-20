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

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Donovan
 */
public class LoginListen implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(DBmanager.load(p.getUniqueId())){//if they are ob
            p.sendMessage("enter");
            Infraction curr = DBmanager.OBs.get(p.getUniqueId());
            if(!curr.isStarted()){
                p.teleport(EnforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                curr.setStarted(true);
            }
            if(curr.getSeverity()==2 && curr.isDone()){
                if(curr.getFinished().before(new Date())){
                    p.sendMessage(ChatColor.YELLOW + "You are no longer OB");
                    DBmanager.archive(p.getUniqueId());
                }else{
                    p.sendMessage(ChatColor.YELLOW + "You are OB until: " + ChatColor.RED + curr.getFinished().toString());
                }
            }
            if(!curr.isDone()){
                p.sendMessage(ChatColor.YELLOW + "Your Location is " + ChatColor.RED + curr.getDestination().getName());
            }
        }
    }
}
