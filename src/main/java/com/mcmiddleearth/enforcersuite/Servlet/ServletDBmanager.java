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
import com.mcmiddleearth.enforcersuite.Infraction;
import com.mcmiddleearth.enforcersuite.Record;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Donovan
 */
public class ServletDBmanager {
    
    private static File DB = new File(EnforcerSuite.getPlugin().getDataFolder() + System.getProperty("file.separator") + "OB-DB");
    
    public static List<Infraction> Incomplete = new ArrayList<>();
    
    public static Record getOBrecord(UUID ob){
        File save = new File(DB + System.getProperty("file.separator") + "Archive" + System.getProperty("file.separator") + ob.toString() + ".obdat");
        if(save.exists()){
            try {
                return EnforcerSuite.getJSonParser().readValue(save, Record.class);
            } catch (IOException ex) {
                Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Record r = new Record();
        r.setOB(new UUID(0,0));
        return r;
    }
    public static Infraction loadReturn(UUID uuid){
        File save = new File(DB + System.getProperty("file.separator") + "Current" + System.getProperty("file.separator") + uuid.toString() + ".obdat");
        if(!save.exists())
            return new Infraction();
        try {
            return EnforcerSuite.getJSonParser().readValue(save, Infraction.class);
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return new Infraction();
        }
    }
    
    public static List<String> getOBs(boolean curr){
        List<String> rtn = new ArrayList<>();
        if(curr){
            for(File f : new File(DB + System.getProperty("file.separator") + "Current").listFiles()){
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
                    String s = rcrd.getOB().toString() + " - ";
                    for(Infraction inf : rcrd.getInfractions()){
                        if(!s.contains(inf.getOBname())){
                            s += inf.getOBname();
                        }
                    }
                    rtn.add(s);
                } catch (IOException ex) {}
            }
        }
        return rtn;
    }
}
