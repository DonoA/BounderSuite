/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan
 */
public class Infraction {
    @Getter @Setter
    private Destination Destination;
    
    @Getter @Setter
    private String rank;
    
    @Getter @Setter
    private int Severity;
    
    @Getter @Setter
    private Date finished;
    
    @Getter @Setter
    private Date demotion;
    
    @Getter @Setter
    private boolean Started = false;
    
    @Getter @Setter
    private boolean rePromoted = false;
    
    @Getter @Setter
    private boolean isDone = false;
    
    @Getter @Setter
    private UUID Enforcer;
    
    @Getter @Setter
    private String notes;
    
    public Infraction(int sev, String rank, Player enforcer, Player OB){
        this.Destination = DBmanager.LoadDest(sev);
        this.rank = rank;
        this.Severity = sev;
        this.Enforcer = enforcer.getUniqueId();
        this.demotion = new Date();
//        this.OB = OB.getUniqueId();
    }
    public boolean inDestination(Location loc){
        if(Destination.inDes(loc)){
            isDone = true;
            return true;//is player in dest
        }else{
            return false;
        }
    }
}
