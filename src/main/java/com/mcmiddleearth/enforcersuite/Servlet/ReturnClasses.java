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

import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Donovan
 */
public class ReturnClasses {
    
    public static class Create{
        @Getter @Setter
        private String type;
        
        @Getter @Setter
        private String name;
        
        @Getter @Setter
        private String demoted;
        
        @Getter @Setter
        private String enforcer;
        
        @Getter @Setter
        private String sev;
        
        @Getter @Setter
        private boolean ban;

        @Getter @Setter
        private String obuuid;

        @Getter @Setter
        private bannedOn bannedOn; 

        @Getter @Setter
        private reasons reasons; 

        @Getter @Setter
        private String[] evidence;

        @Getter @Setter
        private String notes;

        @Getter @Setter
        private boolean complete;

        @Getter @Setter
        private String key;

        public Create(){}
    }
    
    public static class Update{
        @Getter @Setter
        private String type;

        @Getter @Setter
        private String obuuid;

        @Getter @Setter
        private bannedOn bannedOn; 

        @Getter @Setter
        private reasons reasons; 

        @Getter @Setter
        private String[] evidence;

        @Getter @Setter
        private String notes;

        @Getter @Setter
        private boolean complete;

        @Getter @Setter
        private String key;

        public Update(){}
        
        public Update(ReturnClasses.UpdateArchive arch){
            this.type = arch.getType();
            this.obuuid = arch.getObuuid();
            this.bannedOn = arch.getBannedOn();
            this.reasons = arch.getReasons();
            this.evidence = arch.getEvidence();
            this.notes = arch.getNotes();
            this.complete = arch.isComplete();
            this.key = arch.getKey();
        }
    }

        
    
    public static class UpdateArchive{
        @Getter @Setter
        private String type;

        @Getter @Setter
        private String obuuid;

        @Getter @Setter
        private bannedOn bannedOn; 

        @Getter @Setter
        private reasons reasons; 

        @Getter @Setter
        private String[] evidence;

        @Getter @Setter
        private String notes;

        @Getter @Setter
        private boolean complete;

        @Getter @Setter
        private String key;
        
        @Getter @Setter
        private int infid;

        public UpdateArchive(){}
    }
    
    public static class bannedOn {
        @Getter @Setter
        private boolean build;
        @Getter @Setter
        private boolean freebuild;
        @Getter @Setter
        private boolean teamspeak;
        @Getter @Setter
        private boolean forums;
        @Getter @Setter
        private boolean othercheck;
        @Getter @Setter
        private boolean eauto;
        @Getter @Setter
        private String othertxt;

        public bannedOn(){}

        @Override
        public String toString(){
            try {
                return EnforcerSuite.getJSonParser().writeValueAsString(this);
            } catch (IOException ex) {
                Logger.getLogger("update").log(Level.SEVERE, null, ex);
            }
            return "error";
        }
    }

    public static class reasons {
        @Getter @Setter
        private boolean blocks;
        @Getter @Setter
        private boolean spam;
        @Getter @Setter
        private boolean social;
        @Getter @Setter
        private boolean ignoring;
        @Getter @Setter
        private boolean impersonating;
        @Getter @Setter
        private boolean mods;
        @Getter @Setter
        private boolean ads;
        @Getter @Setter
        private boolean obassist;
        @Getter @Setter
        private boolean teamspeak;
        @Getter @Setter
        private boolean alt;
        @Getter @Setter
        private boolean othercheck;
        @Getter @Setter
        private String othertxt;
        @Getter @Setter
        private boolean eauto;

        public reasons(){}
    }
    
    public static class Search{
        @Getter @Setter
        private String type;
        @Getter @Setter
        private String param;
        @Getter @Setter
        private String key;
        
        public Search(){}
    }
}
