/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite.DBmanager;

import com.mcmiddleearth.enforcersuite.Destination;
import com.mcmiddleearth.enforcersuite.OathBreaker;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Donovan
 */
public class DBmanager {
    public static HashMap<String, OathBreaker> OBs = new HashMap<>();
    
    private static File OBFiles = new File(EnforcerSuite.getPlugin().getDataFolder() + System.getProperty("file.separator") + "OB-DB");
    
    public static void save(String pName){
        if(!OBFiles.exists()){
            OBFiles.mkdirs();
        }
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + pName + ".new.obdat");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + pName + ".obdat");
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
        writer.println(pName);
        writer.close();
        try {
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static boolean load(String pName){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + pName + ".obdat");
        if(!save.exists())
            return false;                                   
        
        return true;
    }
    public static void archive(String pName){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Archive" + System.getProperty("file.separator") + pName + ".obdat");
    }
    public static Destination LoadDest(int sev){
        String uri = EnforcerSuite.getPlugin().getDataFolder() + EnforcerSuite.getPlugin().getFileSep() + "Destination-DB" + EnforcerSuite.getPlugin().getFileSep() + String.valueOf(sev) + EnforcerSuite.getPlugin().getFileSep();
        
        File f = new File(uri).listFiles()[new Random().nextInt(new File(uri).listFiles().length)];
        Scanner s = new Scanner(f.toString());
        try{
            int Bounds[] = new int[] {Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine())};
            String name = s.nextLine();
            return new Destination(Bounds, name); 
        }catch(NumberFormatException e){
            System.out.println("Bad Destination File");
            return null;
        }
    }
}
