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
package com.mcmiddleearth.enforcersuite.Listeners;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.Servlet.ServletDBmanager;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

/**
 *
 * @author Donovan
 */
public class SaveListen implements Listener{
    
    @EventHandler
    public void onWorldSave(WorldSaveEvent e){ //sync worldsave with data save
        for(UUID uuid : DBmanager.Bans.keySet()){
            DBmanager.saveBan(uuid);
        }
        for(UUID uuid : DBmanager.OBs.keySet()){
            DBmanager.saveOB(uuid);
        }
        ServletDBmanager.saveIncomplete();
    }
    
}
