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

package com.mcmiddleearth.enforcersuite.Records;

import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Donovan
 */
public class Infraction {
    @Getter @Setter
    private Destination Destination;
    
    @Getter @Setter
    private String rank;
    
    @Getter @Setter
    private int Severity;
    
    @Getter @Setter
    private Date finished;
    
    @Getter @Setter
    private Date demotion;
    
    @Getter @Setter
    private boolean Started = false;
    
    @Getter @Setter
    private boolean rePromoted = false;
    
    @Getter @Setter
    private boolean isDone = false;
    
    @Getter @Setter
    private UUID Enforcer;
    
    @Getter @Setter
    private ArrayList<String> BannedOn = new ArrayList<>();
    
    @Getter @Setter
    private ArrayList<String> Reasons = new ArrayList<>();
    
    @Getter @Setter
    private ArrayList<String> Evidence = new ArrayList<>();
    
    @Getter @Setter
    private String notes;
    
    @Getter @Setter
    private String OBname;
    
    @Getter @Setter @JsonIgnore
    private UUID OBuuid;
    
    @Getter @Setter
    private boolean ban = false;
    
//    @Getter @Setter
//    private boolean ReadyForArchive = false;
    
    public Infraction(int sev, String rank, UUID enforcer, UUID OB){
        this.Destination = DBmanager.LoadDest(sev);
        this.rank = rank;
        this.Severity = sev;
        this.Enforcer = enforcer;
        this.demotion = new Date();
//        this.OBname = OB.getName();
        this.OBuuid = OB;
        BannedOn.add("freebuild");
    }
    
    public Infraction(int sev, String rank, UUID enforcer, UUID OB, String OBname){
        this.Destination = DBmanager.LoadDest(sev);
        this.rank = rank;
        this.Severity = sev;
        this.Enforcer = enforcer;
        this.demotion = new Date();
        this.OBname = OBname;
        this.OBuuid = OB;
        BannedOn.add("freebuild");
    }
    
    public Infraction(int sev, String rank, UUID OB){
        this.Destination = DBmanager.LoadDest(sev);
        this.rank = rank;
        this.Severity = sev;
        this.Enforcer = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.demotion = new Date();
//        this.OBname = OB.getName();
        this.OBuuid = OB;
        BannedOn.add("freebuild");
    }
    
    public Infraction(int sev, String rank, UUID OB, String OBname){
        this.Destination = DBmanager.LoadDest(sev);
        this.rank = rank;
        this.Severity = sev;
        this.Enforcer = UUID.fromString("00000000-0000-0000-0000-000000000000");;
        this.demotion = new Date();
        this.OBname = OBname;
        this.OBuuid = OB;
        BannedOn.add("freebuild");
    }
    
    public Infraction(){}
    
    public boolean inDestination(Location loc){
        if(Destination.inDes(loc)){
            isDone = true;
            return true;//is player in dest
        }else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        try {
            return EnforcerSuite.getJSonParser().writeValueAsString(this);
        } catch (IOException ex) {
            LogUtil.printErr("Failed to convert Infraction");
             LogUtil.printDebugStack(ex);
        }
        return "error";
    }
}
