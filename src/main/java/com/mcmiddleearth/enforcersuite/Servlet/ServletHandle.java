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

import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
/**
 *
 * @author Donovan
 */
public class ServletHandle extends AbstractHandler{
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] args = target.split("/");
        response.setHeader("Server", EnforcerSuite.getPrefix());
        if(args.length>=2){
            if(args[1].equalsIgnoreCase("form")){
                if(ServletDBmanager.Keys.containsKey(request.getRemoteAddr())&&args.length>=3){
                    if(ServletDBmanager.Keys.get(request.getRemoteAddr()).getKey().equalsIgnoreCase(args[2])){
                        if(ServletDBmanager.Keys.get(request.getRemoteAddr()).isForReg()){
                            //cred create
                        }else{
                            //allow edit of infraction
                        }
                    }
                }else{
                    response.setContentType("text/html;");
                    for(File f : new File(EnforcerSuite.getPlugin().getDataFolder() + EnforcerSuite.getPlugin().getFileSep() + "webpage").listFiles()){
                        response.getWriter().println(new Scanner(f).useDelimiter("\\Z").next());
                    }
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    //allow for login of bounders
                }
            }else if(args[1].equalsIgnoreCase("records")&&args.length>=3){
                if(args[2].equalsIgnoreCase("current")){
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(ServletDBmanager.getOBs(true));
                }else if(args[2].equalsIgnoreCase("archive")){
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(ServletDBmanager.getOBs(false));
                }else if(args[2].equalsIgnoreCase("files") && args.length>=4){
                    UUID requestUUID;
                    try{
                        requestUUID = UUID.fromString(args[3]);
                    }catch (Exception e){
                        requestUUID = Bukkit.getOfflinePlayer(args[3]).getUniqueId();
                    }
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println("Current:");
                    response.getWriter().print(EnforcerSuite.getJSonParser().writeValueAsString(ServletDBmanager.loadReturn(requestUUID)) + "\n");
                    response.getWriter().println("Archived:");
                    response.getWriter().print(EnforcerSuite.getJSonParser().writeValueAsString(ServletDBmanager.getOBrecord(requestUUID)) + "\n");
                }
            }
            
        }
    }
}
