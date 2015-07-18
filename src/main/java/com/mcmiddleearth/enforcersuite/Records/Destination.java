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

package com.mcmiddleearth.enforcersuite.Records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 *
 * @author Donovan
 */
public class Destination {
    @Getter @Setter
    private int Zbound1;
    @Getter @Setter
    private int Zbound2;
    @Getter @Setter
    private int Xbound1;
    @Getter @Setter
    private int Xbound2;
    @Getter @Setter
    private String name;
    public Destination(int Bounds[], String name){
        Zbound1 = Math.min(Bounds[0], Bounds[1]);//small
        Zbound2 = Math.max(Bounds[0], Bounds[1]);
        Xbound1 = Math.min(Bounds[2], Bounds[3]);//small
        Xbound2 = Math.max(Bounds[2], Bounds[3]);
        this.name = name;
    }
    public Destination(){
    }
    public boolean inDes(Location loc){
        return ((loc.getX()>this.Xbound1&&loc.getX()<this.Xbound2)&&
                (loc.getZ()>this.Zbound1&&loc.getZ()<this.Zbound2));
    }
    @JsonIgnore
    public Location getCenter(){
        return new Location(EnforcerSuite.getPlugin().getMainWorld(), 
                    (Xbound1 + Xbound2)/2, 100, (Zbound1 + Zbound2)/2);
    }
}
