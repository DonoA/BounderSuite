/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite.DBmanager;

import com.mcmiddleearth.enforcersuite.Records.Destination;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Records.Infraction;
import com.mcmiddleearth.enforcersuite.Records.Record;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Random;
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
    
    public static HashMap<UUID, Infraction> Bans = new HashMap<>();
    
//    public static HashMap<UUID, Record> Records = new HashMap<>();
    
    private final static File OBFiles = new File(EnforcerSuite.getPlugin().getDataFolder() + System.getProperty("file.separator") + "Infraction-DB");
    
    
    static{
        if(!OBFiles.exists()){
            OBFiles.mkdirs();
        }
        if(!(new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator")).exists())){
            new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator")).mkdirs();
        }
        if(!(new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator")).exists())){
            new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator")).mkdirs();
        }
        if(!(new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator")).exists())){
            new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator")).mkdirs();
        }
    }
    
    public static void saveOB(UUID uuid){
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator") + uuid.toString() + ".new.obdat");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
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
        File save = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        Record r = null;
        if(save.exists()){
            try {
                r = EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        r.setCurrentInfraction(DBmanager.OBs.get(uuid));
        saveStart = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".new.record");
        saveFin = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, r);
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
    public static boolean loadOB(UUID uuid){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(!save.exists())
            return false;
        try {
            DBmanager.OBs.put(uuid, EnforcerSuite.getJSonParser().readValue(save, Infraction.class));
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    
    public static void archiveOB(UUID uuid){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        Record r = null;
        if(save.exists()){
            try {
                r = EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        
        Integer next = r.getOldInfractions().size();
        r.getOldInfractions().put(next, r.getCurrentInfraction());
        r.setCurrentInfraction(null);
        File OldInf = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        OldInf.delete();
        
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".new.record");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        boolean successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, r);
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
        try{
            RandomAccessFile s = new RandomAccessFile(f, "r");
            int Bounds[] = new int[] {Integer.parseInt(s.readLine()), Integer.parseInt(s.readLine()), Integer.parseInt(s.readLine()), Integer.parseInt(s.readLine())};
            String name = s.readLine();
            return new Destination(Bounds, name); 
        }catch(NumberFormatException e){
            System.out.println("Bad Destination File " + e.toString());
            return null;
        } catch (FileNotFoundException ex) {
            System.out.println("Bad Destination File " + ex.toString());
            return null;
        } catch (IOException ex) {
            System.out.println("Bad Destination File " + ex.toString());
            return null;
        }
    }
    
    public static void saveBan(UUID uuid){
//        UUID uuid = inf.getOBuuid();
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator") + uuid.toString() + ".new.obdat");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
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
        File save = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        Record r = null;
        if(save.exists()){
            try {
                r = EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        r.setCurrentInfraction(DBmanager.Bans.get(uuid));
        saveStart = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".new.record");
        saveFin = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, r);
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
    
    public static void archiveBan(UUID uuid){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        Record r = null;
        if(save.exists()){
            try {
                r = EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        
        Integer next = r.getOldInfractions().size();
        r.getOldInfractions().put(next, r.getCurrentInfraction());
        r.setCurrentInfraction(null);
        File OldInf = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        OldInf.delete();
        
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".new.record");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        boolean successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, r);
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
    
    public static boolean loadBan(UUID uuid){
        File save = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(!save.exists())
            return false;
        try {
            DBmanager.Bans.put(uuid, EnforcerSuite.getJSonParser().readValue(save, Infraction.class));
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
     public static Infraction getArchivedInfraction(UUID uuid, Integer id){
         File save = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        Record r = null;
        if(save.exists()){
            try {
                r = EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        
        return r.getOldInfractions().get(id);
     }
}
