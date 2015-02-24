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
package com.mcmiddleearth.enforcersuite.Utils;

import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Donovan
 */
public class LogUtil {
    
    public static void printDebug(Object msg){
        if(EnforcerSuite.isDebug())
            Bukkit.getLogger().info(EnforcerSuite.getPrefix() + " [DEBUG] " + msg.toString());
    }
    
    public static void printDebugStack(Exception ex){
        if(EnforcerSuite.isDebug()){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            Bukkit.getLogger().info(EnforcerSuite.getPrefix() + " [DEBUG] [ERROR] " + sw.toString());
        }
    }
    
    public static void printInfo(Object msg){
        Bukkit.getLogger().info(EnforcerSuite.getPrefix() + msg.toString());
    }
    
    public static void printErr(Object msg){
        Bukkit.getLogger().severe(EnforcerSuite.getPrefix() + ChatColor.RED + " [ERROR] " + msg.toString());
    }
}
