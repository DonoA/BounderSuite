/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

/**
 *
 * @author Donovan
 */
public class OathBreaker {
    public String Destination;
    public int severity;
    public boolean isCurrent;
    public int lvl;
    public OathBreaker(int sev){
        this.Destination = LocationIndex.genLoc(sev);
    }
}
