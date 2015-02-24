/*
 * This file is part of EnforcerSuite.
 * 
 * EnforcerSuite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EnforcerSuite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with EnforcerSuite.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package com.mcmiddleearth.enforcersuite.Servlet;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Records.Infraction;
import com.mcmiddleearth.enforcersuite.Records.Record;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Donovan
 */
public class ServletDBmanager {
    
    private static File DB = new File(EnforcerSuite.getPlugin().getDataFolder() + System.getProperty("file.separator") + "Infraction-DB");
    
    public static List<Infraction> Incomplete = new ArrayList<>();
    
    public static Record getRecord(UUID ob){
        File save = new File(DB + System.getProperty("file.separator") + "Archive" + System.getProperty("file.separator") + ob.toString() + ".record");
        if(save.exists()){
            try {
                return EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                LogUtil.printErr("Failed to load Record");
                LogUtil.printDebugStack(ex);
            }
        }
        Record r = new Record();
        r.setOB(new UUID(0,0));
        return r;
    }
    public static Infraction loadReturnOB(UUID uuid){
        File save = new File(DB + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(!save.exists())
            return new Infraction();
        try {
            return EnforcerSuite.getJSonParser().readValue(save, Infraction.class);
        } catch (IOException ex) {
            LogUtil.printErr("Failed to load OB");
                LogUtil.printDebugStack(ex);
            return new Infraction();
        }
    }
    public static Infraction loadReturnBan(UUID uuid){
        File save = new File(DB + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(!save.exists())
            return new Infraction();
        try {
            return EnforcerSuite.getJSonParser().readValue(save, Infraction.class);
        } catch (IOException ex) {
            LogUtil.printErr("Failed to load Ban");
                LogUtil.printDebugStack(ex);
            return new Infraction();
        }
    }
    
    public static List<String> getOBs(boolean curr){
        List<String> rtn = new ArrayList<>();
        if(curr){
            for(File f : new File(DB + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "OB").listFiles()){
                try {
                    Infraction inf = EnforcerSuite.getJSonParser().readValue(f, Infraction.class);
                    if(inf.getOBname() != null){
                        rtn.add(f.getName().replace(".obdat", "") + " - " + inf.getOBname());
                    }else{
                        rtn.add(f.getName().replace(".obdat", ""));
                    }
                } catch (IOException ex) {}
            }
        }else{
            for(File f : new File(DB + System.getProperty("file.separator") + "Archive").listFiles()){
                try {
                    Record rcrd = EnforcerSuite.getJSonParser().readValue(f, Record.class);
                    boolean isBan = false;
                    for(Infraction i : rcrd.getOldInfractions().values()){
                        if(i.isBan()){
                            isBan = true;
                        }
                    }
                    if(!isBan){
                        String s = rcrd.getOB().toString() + " - ";
                        for(Infraction inf : rcrd.getOldInfractions().values()){
                            if(inf.getOBname() != null){
                                if(!s.contains(inf.getOBname())){
                                    s += inf.getOBname() + "  ";
                                }
                            }
                        }
                        rtn.add(s);
                    }
                } catch (IOException ex) {}
            }
        }
        return rtn;
    }
    
    public static List<String> getBans(boolean curr){
        List<String> rtn = new ArrayList<>();
        if(curr){
            for(File f : new File(DB + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + "Ban").listFiles()){
                try {
                    Infraction inf = EnforcerSuite.getJSonParser().readValue(f, Infraction.class);
                    if(inf.getOBname() != null){
                        rtn.add(f.getName().replace(".obdat", "") + " - " + inf.getOBname());
                    }else{
                        rtn.add(f.getName().replace(".obdat", ""));
                    }
                } catch (IOException ex) {}
            }
        }else{
            for(File f : new File(DB + System.getProperty("file.separator") + "Archive").listFiles()){
                try {
                    
                    Record rcrd = EnforcerSuite.getJSonParser().readValue(f, Record.class);
                    boolean isBan = false;
                    for(Infraction i : rcrd.getOldInfractions().values()){
                        if(i.isBan()){
                            isBan = true;
                        }
                    }
                    if(isBan){
                        String s = rcrd.getOB().toString() + " - ";
                        for(Infraction inf : rcrd.getOldInfractions().values()){
                            if(inf.getOBname() != null){
                                if(!s.contains(inf.getOBname())){
                                    s += inf.getOBname() + "  ";
                                }
                            }
                        }
                        rtn.add(s);
                    }
                } catch (IOException ex) {}
            }
        }
        return rtn;
    }
    
    public static void saveIncomplete(){
        File saveStart = new File(DB + System.getProperty("file.separator") + "Servlet.new.data");
        File saveFin = new File(DB + System.getProperty("file.separator") + "Servlet.data");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        boolean successful = true;
        try {
            EnforcerSuite.getJSonParser().writeValue(saveStart, Incomplete.toArray());
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
    
    public static boolean loadIncomplete(){
        File save = new File(DB + System.getProperty("file.separator") + "Servlet.data");
        if(!save.exists()){
            LogUtil.printDebug(save);
            return false;
        }
        try {
            ServletDBmanager.Incomplete.clear();
            ServletDBmanager.Incomplete.addAll(Arrays.asList(EnforcerSuite.getJSonParser().readValue(save, Infraction[].class)));
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
