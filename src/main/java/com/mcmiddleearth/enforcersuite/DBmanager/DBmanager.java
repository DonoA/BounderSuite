/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite.DBmanager;

import com.mcmiddleearth.enforcersuite.Destination;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Infraction;
import com.mcmiddleearth.enforcersuite.Record;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Donovan
 */
public class DBmanager {
    public static HashMap<UUID, Infraction> OBs = new HashMap<>();
    
    public static HashMap<UUID, Record> Records = new HashMap<>();
    
    private final static File OBFiles = new File(EnforcerSuite.getPlugin().getDataFolder() + System.getProperty("file.separator") + "OB-DB");
    
    static{
        if(!OBFiles.exists()){
            OBFiles.mkdirs();
        }
    }
    
    public static void save(UUID uuid){
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + uuid.toString() + ".new.obdat");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        boolean successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, OBs.get(uuid));
         } catch (IOException ex) {
             successful = false;
         } finally {
             if (successful) {
                 if (saveFin.exists()) {
                     saveFin.delete();
                 }
                 saveStart.renameTo(saveFin);
             }
         }
    }
    public static boolean load(UUID uuid){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(!save.exists())
            return false;
        
        try {
            EnforcerSuite.getJSonParser().readValue(save, Infraction.class);
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    public static void archive(UUID uuid){
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Archive" + System.getProperty("file.separator") + uuid.toString() + ".new.obdat");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Archive" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        boolean successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, DBmanager.Records.get(uuid));
         } catch (IOException ex) {
             successful = false;
         } finally {
             if (successful) {
                 if (saveFin.exists()) {
                     saveFin.delete();
                 }
                 saveStart.renameTo(saveFin);
             }
         }
        
    }
    public static Destination LoadDest(int sev){
        String uri = EnforcerSuite.getPlugin().getDataFolder() + EnforcerSuite.getPlugin().getFileSep() + "Destination-DB" + EnforcerSuite.getPlugin().getFileSep() + String.valueOf(sev) + EnforcerSuite.getPlugin().getFileSep();
        File f = new File(uri).listFiles()[new Random().nextInt(new File(uri).listFiles().length)];
        Scanner s = new Scanner(f.toString());
        try{
            Bukkit.getLogger().info(f.toString());
            Bukkit.broadcastMessage(f.toString());
            String Bounds[] = new String[] {(s.nextLine()), (s.nextLine()), (s.nextLine()), (s.nextLine())};
//            int Bounds[] = new int[] {Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine())};
            Bukkit.getLogger().severe(Bounds.toString());
            String name = s.nextLine();
            return null;//new Destination(Bounds, name); 
        }catch(NumberFormatException e){
            System.out.println("Bad Destination File " + e.toString());
            return null;
        }
    }
}
