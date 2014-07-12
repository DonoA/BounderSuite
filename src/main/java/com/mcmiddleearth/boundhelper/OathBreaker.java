/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

import org.bukkit.Location;

/**
 *
 * @author Donovan
 */
public class OathBreaker {
    public String Destination;
    public int severity;
//    public String rank;
//    public int lvl;
    public String pName;
    public OathBreaker(int sev, String pName){ //, String rank
        this.Destination = LocationIndex.genLoc(sev);
        this.pName = pName;
//        this.rank = rank;
    }
    public boolean isDone(Location loc){
//        if
    }
}
