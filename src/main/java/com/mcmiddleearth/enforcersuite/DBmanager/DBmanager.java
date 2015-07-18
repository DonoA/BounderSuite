/*
 * This file is part of BoundHelper.
 * 
 * BoundHelper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BoundHelper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BoundHelper.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.enforcersuite.DBmanager;

import com.mcmiddleearth.enforcersuite.Records.Destination;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Records.Infraction;
import com.mcmiddleearth.enforcersuite.Records.Record;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
             LogUtil.printErr("Failed to save OB");
             LogUtil.printDebugStack(ex);
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
                LogUtil.printErr("Failed to update OB");
                LogUtil.printDebugStack(ex);
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
             LogUtil.printErr("Failed to save OB");
             LogUtil.printDebugStack(ex);
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
            LogUtil.printErr("Failed to load OB");
            LogUtil.printDebugStack(ex);
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
                LogUtil.printErr("Failed to archive OB");
                LogUtil.printDebugStack(ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        
        Integer next = r.getOldInfractions().size();
        r.getOldInfractions().put(next, r.getCurrentInfraction());
        r.setCurrentInfraction(null);
        if(DBmanager.OBs.get(uuid).getOBname() != null){
            if(!r.getNames().contains(DBmanager.OBs.get(uuid).getOBname())){
                r.getNames().add(DBmanager.OBs.get(uuid).getOBname());
            }
        }
        File OldInf = new File(OBFiles + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        OldInf.delete();
        
        File saveStart = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".new.record");
        File saveFin = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        DBmanager.OBs.remove(uuid);
        boolean successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, r);
         } catch (IOException ex) {
             LogUtil.printErr("Failed to archive OB");
             LogUtil.printDebugStack(ex);
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
        }catch(NumberFormatException ex){
            LogUtil.printErr("Bad Destination File " + ex.toString());
            return null;
        } catch (FileNotFoundException ex) {
            LogUtil.printErr("Bad Destination File " + ex.toString());
            return null;
        } catch (IOException ex) {
            LogUtil.printErr("Bad Destination File " + ex.toString());
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
            LogUtil.printDebug(Bans.toString());
            EnforcerSuite.getJSonParser().writeValue(saveStart, Bans.get(uuid));
         } catch (IOException ex) {
             LogUtil.printErr("Failed to save Ban");
             LogUtil.printDebugStack(ex);
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
                LogUtil.printErr("Failed to save Ban");
                LogUtil.printDebugStack(ex);
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
             LogUtil.printErr("Failed to save Ban");
             LogUtil.printDebugStack(ex);
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
                LogUtil.printErr("Failed to archive Ban");
                LogUtil.printDebugStack(ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        
        Integer next = r.getOldInfractions().size();
        r.getOldInfractions().put(next, r.getCurrentInfraction());
        r.setCurrentInfraction(null);
        if(DBmanager.OBs.get(uuid).getOBname() != null){
            if(!r.getNames().contains(DBmanager.OBs.get(uuid).getOBname())){
                r.getNames().add(DBmanager.OBs.get(uuid).getOBname());
            }
        }
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
             LogUtil.printErr("Failed to archive Ban");
             LogUtil.printDebugStack(ex);
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
            LogUtil.printErr("Failed to load ban");
            LogUtil.printDebugStack(ex);
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
     
    public static void addName(UUID uuid, String newName){ // make sure that all past names are saved
        File save = new File(OBFiles + System.getProperty("file.separator") + "Records" + System.getProperty("file.separator") + uuid.toString() + ".record");
        Record r = null;
        if(save.exists()){
            try {
                r = EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                LogUtil.printErr("Failed to add name to record");
                LogUtil.printDebugStack(ex);
                return;
            }
        }else{
            r = new Record();
            r.setOB(uuid);
        }
        if(!r.getNames().contains(newName)){
            r.getNames().add(newName);
        }
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
             LogUtil.printErr("Failed to add name to record");
             LogUtil.printDebugStack(ex);
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
}
