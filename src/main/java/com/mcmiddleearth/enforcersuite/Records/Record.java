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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Donovan
 */
public class Record {
    @Getter @Setter
    private UUID OB;
    
    @Getter @Setter
    private HashMap<Integer, Infraction> oldInfractions = new HashMap<>();
    
    @Getter @Setter
    private Infraction CurrentInfraction;
    
    @Getter @Setter
    private List<String> Names = new ArrayList<>();
    
    public Record(){}
    
    public boolean Archive(){//wip
        DBmanager.archiveOB(OB);
        return false;
    }
}
