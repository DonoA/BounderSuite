/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite.DBamanger;

import com.mcmiddleearth.enforcersuite.Destination;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Donovan
 */
public class LocationIndex {
    
    public static HashMap<String, Destination> Destinations = new HashMap<>();
    
    private static HashMap<Integer, ArrayList<String>> DesBySev = new HashMap<>();
    
    public static int[][] getLocation(String name){
        int Boundx[] = {0,0};
        int Boundz[] = {0,0};
        if(name.equalsIgnoreCase("bree")){
            Boundx[0] = -1335;
            Boundx[1] = -1847;
            Boundz[0] = -4099;
            Boundz[1] = -4608;
        }else if(name.equalsIgnoreCase("michel delving")){
            Boundx[0] = -4932;
            Boundx[1] = -5111;
            Boundz[0] = -3812;
            Boundz[1] = -4126;
        }else if(name.equalsIgnoreCase("rivendell")){
            Boundx[0] = 3520;
            Boundx[1] = 3708;
            Boundz[0] = -4331;
            Boundz[1] = -4489;
        }
        else if(name.equalsIgnoreCase("lothlorien")){
            Boundx[0] = 4130;
            Boundx[1] = 4618;
            Boundz[0] = -1948;
            Boundz[1] = -2380;
        }
        else if(name.equalsIgnoreCase("fornost")){
            Boundx[0] = -2662;
            Boundx[1] = -2157;
            Boundz[0] = -5769;
            Boundz[1] = -6199;
        }
        else if(name.equalsIgnoreCase("moria west gate")){
            Boundx[0] = 2171;
            Boundx[1] = 2209;
            Boundz[0] = -2098;
            Boundz[1] = -2029;
        }
        else if(name.equalsIgnoreCase("os-in-edhil")){
            Boundx[0] = 594;
            Boundx[1] = 925;
            Boundz[0] = -1434;
            Boundz[1] = -1264;
        }
        else if(name.equalsIgnoreCase("isengard")){
            Boundx[0] = 1104;
            Boundx[1] = 1537;
            Boundz[0] = 329;
            Boundz[1] = -111;
        }
        else if(name.equalsIgnoreCase("edoras")){
            Boundx[0] = 3064;
            Boundx[1] = 2632;
            Boundz[0] = 1416;
            Boundz[1] = 1747;
        }
        else if(name.equalsIgnoreCase("amon hen")){
            Boundx[0] = 6787;
            Boundx[1] = 6423;
            Boundz[0] = 1339;
            Boundz[1] = 1457;
        }
        else if(name.equalsIgnoreCase("osgo")){
            Boundx[0] = 7990;
            Boundx[1] = 8745;
            Boundz[0] = 3816;
            Boundz[1] = 3340;
        }
        else if(name.equalsIgnoreCase("minas tirith")){
            Boundx[0] = 7738;
            Boundx[1] = 7295;
            Boundz[0] = 3796;
            Boundz[1] = 4367;
        }else if(name.equalsIgnoreCase("dead marshes")){
            Boundx[0] = 9692;
            Boundx[1] = 8535;
            Boundz[0] = 516;
            Boundz[1] = 1386;
        }else if(name.equalsIgnoreCase("helms deep")){
            Boundx[0] = 1466;
            Boundx[1] = 1287;
            Boundz[0] = 1324;
            Boundz[1] = 1581;
        }
        else if(name.equalsIgnoreCase("farbad")){
            Boundx[0] = -1733;
            Boundx[1] = -1149;
            Boundz[0] = -1568;
            Boundz[1] = -1121;
        }
        return new int[][] {{Boundx[0],Boundx[1]},{Boundz[0],Boundz[1]}};
    }
    public static void loadLocs(){
        
    }
    public static Destination genLoc(int sev){
        Random randomGenerator = new Random();
        String Des = new ArrayList<String>(DesBySev.get(sev)).get(randomGenerator.nextInt(DesBySev.get(sev).size()-1));
        return Destinations.get(Des);
    }
}
