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

package com.mcmiddleearth.enforcersuite.Servlet;

import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import lombok.Getter;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author Donovan
 */
public class Servlet {
    @Getter
    private Server server;
    
    @Getter
    private int BoundPort;
    
    public Servlet(int PortToBind){
        server = new Server(PortToBind);
        this.BoundPort = PortToBind;
        server.setHandler(new ServletHandle());
    }
    
    public void start(){
        try{
            if(server != null){
                server.start();
                (new ServletHandle.TCPconnect()).start();
//                server.join();
            }
        } catch (Exception ex) {
            LogUtil.printErr("Failed to start servlet on port: " + BoundPort);
             LogUtil.printDebugStack(ex);
        }
    }
}
