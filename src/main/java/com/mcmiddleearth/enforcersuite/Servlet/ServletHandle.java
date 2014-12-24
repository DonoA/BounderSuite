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
import com.mcmiddleearth.enforcersuite.Infraction;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        if(args.length>=1){
            if(args[1].equalsIgnoreCase("current")){
                baseRequest.setHandled(true);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(ServletDBmanager.getOBs(true));
            }else if(args[1].equalsIgnoreCase("archive")){
                baseRequest.setHandled(true);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(ServletDBmanager.getOBs(false));
            }else if(args[1].equalsIgnoreCase("files") && args.length>=2){
                UUID requestUUID;
                try{
                    requestUUID = UUID.fromString(args[2]);
                }catch (Exception e){
                    requestUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
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
    
    public static class TCPconnect extends Thread {
        
        @Override
        public void run() {
            String clientSentence;
            ServerSocket welcomeSocket = null;
            try {
                welcomeSocket = new ServerSocket(6789);
                List<String> rtn = new ArrayList<String>();
                        
                while(true){
                    try (Socket connectionSocket = welcomeSocket.accept()) {
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        clientSentence = inFromClient.readLine();
                        if(clientSentence.equalsIgnoreCase("ping")){
                            rtn.clear();
                            for(Infraction inf : ServletDBmanager.Incomplete){
                                if(inf.getOBname() == null){
                                    rtn.add(inf.getOBuuid().toString() + " - " + inf.isBan());
                                }else{
                                    rtn.add(inf.getOBname() + " - " + inf.getOBuuid().toString() + " - " + inf.isBan());
                                }
                            }
                            System.out.println(EnforcerSuite.getJSonParser().writeValueAsString(rtn));
                            outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtn));
                        }else if(clientSentence.contains("fetch")){
                            for(Infraction inf : ServletDBmanager.Incomplete){
                                if(clientSentence.contains(inf.getOBuuid().toString())){
//                                    System.out.println("fetch: " + EnforcerSuite.getJSonParser().writeValueAsString(inf));
                                    outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(inf));
                                }
                            }
                        }else if(clientSentence.contains("return")){
                            System.out.println(clientSentence);
                        }
                            
                    } catch (IOException ex) {
                        Logger.getLogger(ServletHandle.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ServletHandle.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }
}
