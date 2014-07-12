/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.boundhelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Donovan
 */
public class DBmanager {
    public static HashMap<String, OathBreaker> OBs = new HashMap<>();
    private static File DataFile = new File(BounderHelper.plugin.getDataFolder() + System.getProperty("file.separator") + "OBdat");
    public static HashMap<String, Date> waitinglist = new HashMap<>();
    
    public static void save(String pName){
        if(!DataFile.exists()){
            DataFile.mkdirs();
        }
        File saveStart = new File(DataFile + System.getProperty("file.separator") + pName + ".new.obdat");
        File saveFin = new File(DataFile + System.getProperty("file.separator") + pName + ".obdat");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        
        
        FileWriter fr = null;
        try {
            fr = new FileWriter(saveStart.toString());
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter writer = new PrintWriter(fr);
        writer.println("lolz");
        writer.close();
        try {
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static boolean load(String pName){
        File save = new File(DataFile + System.getProperty("file.separator") + pName + ".obdat");
        if(!save.exists())
            return false;
        
        return true;
    }
}
