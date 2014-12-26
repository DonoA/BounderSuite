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

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Donovan
 */
public class ReturnClass {
    
    @Getter @Setter
    private String obuuid;
    
    @Getter @Setter
    private ReturnClass.bannedOn bannedOn; 
    
    @Getter @Setter
    private ReturnClass.reasons reasons; 
    
    @Getter @Setter
    private String evidence;
    
    @Getter @Setter
    private String notes;
    
    public ReturnClass(){}
            
    public class bannedOn {
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
    }
    
    public class reasons {
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
}
