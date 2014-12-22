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

import com.mcmiddleearth.enforcersuite.Infraction;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Donovan
 */
public class RequestKey {
    @Getter
    private String key;
    
    @Getter @Setter
    private Infraction inf;
    
    @Getter
    private String ip;
    
    @Getter
    private String ign;
    
    @Getter
    private boolean forReg = false;
    
    public RequestKey(String key, Infraction inf, String ip){
        this.key = key;
        this.inf = inf;
        this.ip = ip;
    }
    
    public RequestKey(String key, String ip, String ign){
        this.key = key;
        this.ip = ip;
        this.ign = ign;
        this.forReg = true;
    }
}
