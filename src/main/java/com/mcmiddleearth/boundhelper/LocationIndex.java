/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Donovan
 */
public class LocationIndex {
    
    
    
    public static Rectangle2D getLocation(String name){
        if(name.equalsIgnoreCase(name)){
//            int zbounds[] = {bukkitLoc.getBlockZ() - jobRadius, bukkitLoc.getBlockZ() + jobRadius};
//            int xbounds[] = {bukkitLoc.getBlockX() - jobRadius, bukkitLoc.getBlockX() + jobRadius};
//            this.area = new Polygon(xbounds, zbounds, xbounds.length);
//            this.bounds = area.getBounds2D();
        }else if(name.equalsIgnoreCase(name)){
            
        }
    }
    public static String genLoc(int sev){
        Random randomGenerator = new Random();
        if(sev == 1){
            List<String> firstLocs = Arrays.asList(new String[] {"shire"});
            return firstLocs.get(randomGenerator.nextInt(firstLocs.size()));
        }else if(sev == 2){
            List<String> secondLocs = Arrays.asList(new String[] {"mordor"});
            return secondLocs.get(randomGenerator.nextInt(secondLocs.size()));
        }
        return " error ";
    }
}
