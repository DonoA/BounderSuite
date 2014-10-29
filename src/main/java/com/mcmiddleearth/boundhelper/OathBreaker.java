/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

import com.mcmiddleearth.boundhelper.DBamanger.LocationIndex;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Donovan
 */
public class OathBreaker {
    @Getter @Setter
    private Destination Destination;
    
    @Getter @Setter
    private String rank;
    
    @Getter @Setter
    private String pName;
    
    @Getter @Setter
    private int Sev;
    
    @Getter @Setter
    private Date fin;
    
    public OathBreaker(int sev, String pName, String rank){
        this.Destination = LocationIndex.genLoc(sev);
        this.pName = pName;
        this.rank = rank;
        this.Sev = sev;
    }
    public boolean isDone(Location loc){
        return Destination.inDes(Bukkit.getPlayer(pName).getLocation());
    }
}
