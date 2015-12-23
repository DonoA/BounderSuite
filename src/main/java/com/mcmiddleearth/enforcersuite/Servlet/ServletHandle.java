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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcmiddleearth.enforcersuite.Commands.Commands;
import com.mcmiddleearth.enforcersuite.DBmanager.DBmanager;
import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Records.Infraction;
import com.mcmiddleearth.enforcersuite.Records.Record;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
            if(args[1].equalsIgnoreCase("current")){
                if(args[2].equalsIgnoreCase("ob")){
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(ServletDBmanager.getOBs(true));
                }else if(args[2].equalsIgnoreCase("ban")){
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(ServletDBmanager.getBans(true));
                }
            }else if(args[1].equalsIgnoreCase("archive")){
                if(args[2].equalsIgnoreCase("ob")){
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(ServletDBmanager.getOBs(false));
                }else if(args[2].equalsIgnoreCase("ban")){
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(ServletDBmanager.getBans(false));
                }
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
                response.getWriter().print(EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.getRecord(requestUUID).getCurrentInfraction()) + "\n");
                response.getWriter().println("Archived:");
                response.getWriter().print(EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.getRecord(requestUUID).getOldInfractions()) + "\n");
            }else{
                baseRequest.setHandled(true);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Use one of the following" + "\n" + "/<current|archive>/<ob|ban>" + "\n" + "/files/<player (uuid or name) >");
            }
        }else{
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Use one of the following" + "\n" + "/<current|archive>/<ob|ban>" + "\n" + "/files/<player (uuid or name) >");
        }
    }
    
    public static class TCPconnect extends Thread {
        
        @Override
        public void run() {
            String[] req;
            ServerSocket welcomeSocket = null;
            TCPkeyHandle keyHandle = new TCPkeyHandle();
            try {
                welcomeSocket = new ServerSocket(6789);
                List<String> rtn = new ArrayList<>();
                ServletDBmanager.loadIncomplete();
                while(true){
                    try (Socket connectionSocket = welcomeSocket.accept()) {
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        String h = (inFromClient.readLine());
                        req = h.split("/-/");
                        LogUtil.printDebug(h);
                        LogUtil.printDebug(Arrays.toString(req));
                        if(req[0].equalsIgnoreCase("ping")){
                            ArrayList<Infraction> forRemoval = new ArrayList<>();
                            rtn.clear();
                            LogUtil.printDebug(ServletDBmanager.Incomplete);
                            for(Infraction inf : ServletDBmanager.Incomplete){
                                if(inf.getOBname() == null){
                                    if(inf.isBan()){
                                        rtn.add(inf.getOBuuid().toString() + " - Ban");
                                    }else{
                                        rtn.add(inf.getOBuuid().toString() + " - OathBreaker");
                                    }
                                }else{
                                    if(inf.isBan()){
                                        rtn.add(inf.getOBuuid().toString() + " - " + inf.getOBname() + " - " + "Ban");
                                    }else{
                                        rtn.add(inf.getOBuuid().toString() + " - " + inf.getOBname() + " - " + "OathBreaker");
                                    }
                                }
                            }
                            ServletDBmanager.Incomplete.removeAll(forRemoval);
                            LogUtil.printDebug("Successful Ping");
                            outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtn));
                        }else if(req[0].equalsIgnoreCase("fetch")){
                            if(req[1].equalsIgnoreCase("archive")){
                                HashMap<String, HashMap<String, ArrayList<String>>> toSend = new HashMap<String, HashMap<String, ArrayList<String>>>();
                                HashMap<String, ArrayList<String>> bag =  new HashMap<String, ArrayList<String>>();
                                //obs
                                bag.put("Current", new ArrayList<String>(ServletDBmanager.getOBs(true)));
                                List<String> hold = ServletDBmanager.getOBs(false);
                                hold.removeAll(ServletDBmanager.getOBs(true));
                                bag.put("Archived", new ArrayList<String>(hold));
                                hold = ServletDBmanager.getOBs(true);
                                hold.removeAll(ServletDBmanager.getOBs(false));
                                toSend.put("OBs", bag);
                                //bans
                                bag =  new HashMap<String, ArrayList<String>>();
                                bag.put("Current", new ArrayList<String>(ServletDBmanager.getBans(true)));
                                hold = ServletDBmanager.getBans(false);
                                hold.removeAll(ServletDBmanager.getBans(true));
                                bag.put("Archived", new ArrayList<String>(hold));
                                toSend.put("Bans", bag);
                                //send
                                outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(toSend));
                                LogUtil.printDebug(EnforcerSuite.getJSonParser().writeValueAsString(toSend));
                            }else if(req[1].equalsIgnoreCase("record")){
                                String uuid = req[2].split(" - ")[0];
                                LogUtil.printDebug("wrote: "+EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.getRecord(UUID.fromString(uuid))));
                                outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.getRecord(UUID.fromString(uuid))));
                            }else{
                                for(Infraction inf : ServletDBmanager.Incomplete){
                                    if(req[1].contains(inf.getOBuuid().toString())){
                                        LogUtil.printDebug("sent: " + EnforcerSuite.getJSonParser().writeValueAsString(inf));
                                        outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(inf));
                                    }
                                }
                            }
                        }else if(req[0].equalsIgnoreCase("return")){
                            ReturnClasses.Update rtnclss = EnforcerSuite.getJSonParser().readValue(req[1], ReturnClasses.Update.class);
                            if(!TCPkeyHandle.validRequest(rtnclss.getKey(), true)){
                                outToClient.writeBytes("Invalid key!");
                                return;
                            }else{
                                outToClient.writeBytes("All changes saved!");
                            }
                            Infraction[] infs = new Infraction[ServletDBmanager.Incomplete.size()];
                            ServletDBmanager.Incomplete.toArray(infs);
                            Infraction inf = null;
                            for(Infraction i : infs){
                                if(req[1].contains(i.getOBuuid().toString())){
                                    inf = i;
                                    if(rtnclss.isComplete()){
                                        ServletDBmanager.Incomplete.remove(i);
                                    }
                                }
                            }
                            if(inf == null)
                                return;
                            inf.getBannedOn().clear();
                            inf.getReasons().clear();
                            //bans
                            if(rtnclss.getBannedOn().isBuild()){
                                inf.getBannedOn().add("build");
                                if(rtnclss.getBannedOn().isEauto()){
                                    //ban them
                                }
                            }
                            if(rtnclss.getBannedOn().isFreebuild()){
                                inf.getBannedOn().add("freebuild");
                                if(rtnclss.getBannedOn().isEauto()){
                                    //ban them
                                }
                            }
                            if(rtnclss.getBannedOn().isTeamspeak()){
                                inf.getBannedOn().add("teamspeak");
                                if(rtnclss.getBannedOn().isEauto()){
                                    //ban them
                                }
                            }
                            if(rtnclss.getBannedOn().isForums()){
                                inf.getBannedOn().add("forums");
                            }
                            if(rtnclss.getBannedOn().isOthercheck()){
                                inf.getBannedOn().add("OTHER_"+rtnclss.getBannedOn().getOthertxt());
                            }
                            //reasons
                            if(rtnclss.getReasons().isBlocks()){
                                inf.getReasons().add("Unauthorised Block Break/Place");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isSpam()){
                                inf.getReasons().add("Spamming Chat");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isSocial()){
                                inf.getReasons().add("Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, "
                                        + "Political/Religious Discussions, Referencing Vulgar/Explicit Material)");
                            }
                            if(rtnclss.getReasons().isIgnoring()){
                                inf.getReasons().add("Ignoring Staff Direction / Impeding Work");
                            }
                            if(rtnclss.getReasons().isImpersonating()){
                                inf.getReasons().add("Impersonating Staff Member");
                            }
                            if(rtnclss.getReasons().isMods()){
                                inf.getReasons().add("Use of Detrimental 3rd Party Mods");
                            }
                            if(rtnclss.getReasons().isAds()){
                                inf.getReasons().add("Advertising");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isObassist()){
                                inf.getReasons().add("Assisting Oathbreakers");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isTeamspeak()){
                                inf.getReasons().add("TeamSpeak (Infractions)");
                            }
                            if(rtnclss.getReasons().isAlt()){
                                inf.getReasons().add("Alt Account");
                            }
                            if(rtnclss.getReasons().isOthercheck()){
                                inf.getReasons().add("OTHER_"+rtnclss.getReasons().getOthertxt());
                            }
                            inf.setEvidence(new ArrayList<String>(Arrays.asList(rtnclss.getEvidence())));
                            inf.setNotes(rtnclss.getNotes());
                            if(!inf.isBan()){
                                if(DBmanager.OBs.containsKey(UUID.fromString(rtnclss.getObuuid()))){
                                    Infraction saved = DBmanager.OBs.get(UUID.fromString(rtnclss.getObuuid()));
                                    inf.setDone(saved.isDone());
                                    inf.setFinished(saved.getFinished());
                                    inf.setRePromoted(saved.isRePromoted());
                                    inf.setStarted(saved.isStarted());
                                    DBmanager.OBs.put(UUID.fromString(rtnclss.getObuuid()), inf);
                                    DBmanager.saveOB(UUID.fromString(rtnclss.getObuuid()));
                                }else{
                                    Record r = DBmanager.getRecord(UUID.fromString(rtnclss.getObuuid()));
                                    Infraction saved = null;
                                    if(r.getCurrentInfraction() == null){
                                        saved = r.getOldInfractions().get(r.getOldInfractions().size()-1);
                                    }else{
                                        saved = r.getCurrentInfraction();
                                    }
                                    inf.setDone(saved.isDone());
                                    inf.setFinished(saved.getFinished());
                                    inf.setRePromoted(saved.isRePromoted());
                                    inf.setStarted(saved.isStarted());
                                    if(inf.getFinished() == null){
                                        DBmanager.OBs.put(UUID.fromString(rtnclss.getObuuid()), inf);
                                        DBmanager.saveOB(UUID.fromString(rtnclss.getObuuid()));
                                        DBmanager.OBs.remove(UUID.fromString(rtnclss.getObuuid()));
                                        r.setCurrentInfraction(inf);
                                        DBmanager.saveRecord(r);
                                    }else{
                                        r.getOldInfractions().put(r.getOldInfractions().size()-1, inf);
                                        DBmanager.saveRecord(r);
                                    }
                                }
                            }else{
                                Record r = DBmanager.getRecord(UUID.fromString(rtnclss.getObuuid()));
                                Infraction saved = null;
                                if(r.getCurrentInfraction() == null){
                                    saved = r.getOldInfractions().get(r.getOldInfractions().size()-1);
                                }else{
                                    saved = r.getCurrentInfraction();
                                }
                                inf.setDone(saved.isDone());
                                inf.setFinished(saved.getFinished());
                                inf.setRePromoted(saved.isRePromoted());
                                inf.setStarted(saved.isStarted());
                                if(inf.getFinished() == null){
                                    DBmanager.Bans.put(UUID.fromString(rtnclss.getObuuid()), inf);
                                    DBmanager.saveBan(UUID.fromString(rtnclss.getObuuid()));
                                    DBmanager.Bans.remove(UUID.fromString(rtnclss.getObuuid()));
                                    r.setCurrentInfraction(inf);
                                    DBmanager.saveRecord(r);
                                }else{
                                    r.getOldInfractions().put(r.getOldInfractions().size()-1, inf);
                                    DBmanager.saveRecord(r);
                                }
                            }
                        }else if(req[0].equalsIgnoreCase("add")){
                            ReturnClasses.Create rtnclss = EnforcerSuite.getJSonParser().readValue(req[1], ReturnClasses.Create.class);
                            ErrorType rtnmsg = new ErrorType();
                            if(!TCPkeyHandle.validRequest(rtnclss.getKey(), false)){
                                rtnmsg.setMsg("Invalid key!");
                                rtnmsg.setErr(1);
                                outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                                return;
                            }
                            Infraction[] infs = new Infraction[ServletDBmanager.Incomplete.size()];
                            ServletDBmanager.Incomplete.toArray(infs);
                            Infraction inf = null;
                            
                            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(rtnclss.getObuuid()));
                            if(!rtnclss.isBan()){
                                if(op.isOnline()){
                                    Player ob = op.getPlayer();
                                    if(!DBmanager.OBs.containsKey(ob.getUniqueId())){
                                        ob.teleport(EnforcerSuite.getPlugin().getMainWorld().getSpawnLocation());
                                        inf = new Infraction(Integer.parseInt(rtnclss.getSev().substring(0, 1)), "none", Bukkit.getOfflinePlayer(rtnclss.getEnforcer()).getUniqueId(), ob.getUniqueId(), ob.getName());
                                        inf.setStarted(true);
                                        DBmanager.OBs.put(ob.getUniqueId(), inf);
                                        for(int j=0; j <= 3; j++){
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + ob.getName());
                                        }
                                        DBmanager.saveOB(ob.getUniqueId()); //save OB to file

                                        rtnmsg.setMsg("You have OathBreakered "+ob.getName());
                                        rtnmsg.setErr(0);
                                        ob.sendMessage(EnforcerSuite.getPrefix()+"You are an OathBreaker now");
                                        ob.sendMessage(EnforcerSuite.getPrefix()+"Your destination is " + ChatColor.RED + inf.getDestination().getName());
                                    }else{
                                        rtnmsg.setMsg(ob.getName() + " is already OB!");
                                        rtnmsg.setErr(1);
                                        outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                                        return;
                                    }
                                }else{
                                    if(!DBmanager.loadOB(op.getUniqueId())){
                                        if(op.hasPlayedBefore()){
                                            inf = new Infraction(Integer.parseInt(rtnclss.getSev().substring(0, 1)), "none", Bukkit.getOfflinePlayer(rtnclss.getEnforcer()).getUniqueId(), op.getUniqueId());
                                            DBmanager.OBs.put(op.getUniqueId(), inf);
                                            for(int j=0; j <= 3; j++){
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "demote " + op.getName());
                                            }
                                            DBmanager.saveOB(op.getUniqueId());
                                            DBmanager.OBs.remove(op.getUniqueId());
                                            rtnmsg.setMsg("You have OathBreakered "+op.getUniqueId());
                                            rtnmsg.setErr(0);
                                        }else{
                                            rtnmsg.setMsg("That Player has never played before");
                                            rtnmsg.setErr(1);
                                            outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                                            return;
                                        }
                                    }else{
                                        DBmanager.OBs.remove(op.getUniqueId());
                                        rtnmsg.setMsg("That player is already OB!");
                                        rtnmsg.setErr(1);
                                        outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                                        return;
                                    }
                                }
                            }else if(rtnclss.isBan()){
                                if(!op.isBanned()){
                                    int type = Integer.parseInt(rtnclss.getSev().substring(0, 1));
                                    if(op.hasPlayedBefore()){
                                        inf = new Infraction(type, "none", Bukkit.getOfflinePlayer(rtnclss.getEnforcer()).getUniqueId(), op.getUniqueId(), op.getName());
                                        inf.setBan(true);
                                        DBmanager.Bans.put(op.getUniqueId(), inf);
                                        DBmanager.saveBan(op.getUniqueId());
                                        try {
                                            LogUtil.printDebug(EnforcerSuite.getJSonParser().writeValueAsString(DBmanager.Bans));
                                        } catch (JsonProcessingException ex) {
                                            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        op.setBanned(true);
                                        if(op.isOnline()){
                                            op.getPlayer().kickPlayer("You have been banned");
                                        }
                                        rtnmsg.setMsg("You have Banned "+op.getName());
                                        rtnmsg.setErr(0);
                                    }else{
                                        rtnmsg.setMsg("That Player has never played before");
                                        rtnmsg.setErr(1);
                                        outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                                        return;
                                    }
                                }else{
                                    rtnmsg.setMsg(op.getName() + " is already Banned!");
                                    rtnmsg.setErr(1);
                                    outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                                    return;
                                }
                            }
                            inf.getBannedOn().clear();
                            inf.setDemotion(new Date((long)Integer.parseInt(rtnclss.getDemoted())*1000));
                            inf.setBan(rtnclss.isBan());
                            //bans
                            if(rtnclss.getBannedOn().isBuild()){
                                inf.getBannedOn().add("build");
                                if(rtnclss.getBannedOn().isEauto()){
                                    //ban them
                                }
                            }
                            if(rtnclss.getBannedOn().isFreebuild()){
                                inf.getBannedOn().add("freebuild");
                                if(rtnclss.getBannedOn().isEauto()){
                                    //ban them
                                }
                            }
                            if(rtnclss.getBannedOn().isTeamspeak()){
                                inf.getBannedOn().add("teamspeak");
                                if(rtnclss.getBannedOn().isEauto()){
                                    //ban them
                                }
                            }
                            if(rtnclss.getBannedOn().isForums()){
                                inf.getBannedOn().add("forums");
                            }
                            if(rtnclss.getBannedOn().isOthercheck()){
                                inf.getBannedOn().add("OTHER_"+rtnclss.getBannedOn().getOthertxt());
                            }
                            //reasons
                            if(rtnclss.getReasons().isBlocks()){
                                inf.getReasons().add("Unauthorised Block Break/Place");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isSpam()){
                                inf.getReasons().add("Spamming Chat");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isSocial()){
                                inf.getReasons().add("Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, "
                                        + "Political/Religious Discussions, Referencing Vulgar/Explicit Material)");
                            }
                            if(rtnclss.getReasons().isIgnoring()){
                                inf.getReasons().add("Ignoring Staff Direction / Impeding Work");
                            }
                            if(rtnclss.getReasons().isImpersonating()){
                                inf.getReasons().add("Impersonating Staff Member");
                            }
                            if(rtnclss.getReasons().isMods()){
                                inf.getReasons().add("Use of Detrimental 3rd Party Mods");
                            }
                            if(rtnclss.getReasons().isAds()){
                                inf.getReasons().add("Advertising");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isObassist()){
                                inf.getReasons().add("Assisting Oathbreakers");
                                if(rtnclss.getReasons().isEauto()){
                                    //Collect Evidence
                                }
                            }
                            if(rtnclss.getReasons().isTeamspeak()){
                                inf.getReasons().add("TeamSpeak (Infractions)");
                            }
                            if(rtnclss.getReasons().isAlt()){
                                inf.getReasons().add("Alt Account");
                            }
                            if(rtnclss.getReasons().isOthercheck()){
                                inf.getReasons().add("OTHER_"+rtnclss.getReasons().getOthertxt());
                            }
                            inf.setEvidence(new ArrayList<String>(Arrays.asList(rtnclss.getEvidence())));
                            inf.setNotes(rtnclss.getNotes());
                            if(!inf.isBan()){
                                if(DBmanager.OBs.containsKey(UUID.fromString(rtnclss.getObuuid()))){
                                    Infraction saved = DBmanager.OBs.get(UUID.fromString(rtnclss.getObuuid()));
                                    inf.setDone(saved.isDone());
                                    inf.setFinished(saved.getFinished());
                                    inf.setRePromoted(saved.isRePromoted());
                                    inf.setStarted(saved.isStarted());
                                    DBmanager.OBs.put(UUID.fromString(rtnclss.getObuuid()), inf);
                                    DBmanager.saveOB(UUID.fromString(rtnclss.getObuuid()));
                                }else{
                                    Record r = DBmanager.getRecord(UUID.fromString(rtnclss.getObuuid()));
                                    Infraction saved = null;
                                    if(r.getCurrentInfraction() == null){
                                        saved = r.getOldInfractions().get(r.getOldInfractions().size()-1);
                                    }else{
                                        saved = r.getCurrentInfraction();
                                    }
                                    inf.setDone(saved.isDone());
                                    inf.setFinished(saved.getFinished());
                                    inf.setRePromoted(saved.isRePromoted());
                                    inf.setStarted(saved.isStarted());
                                    if(inf.getFinished() == null){
                                        DBmanager.OBs.put(UUID.fromString(rtnclss.getObuuid()), inf);
                                        DBmanager.saveOB(UUID.fromString(rtnclss.getObuuid()));
                                        DBmanager.OBs.remove(UUID.fromString(rtnclss.getObuuid()));
                                        r.setCurrentInfraction(inf);
                                        DBmanager.saveRecord(r);
                                    }else{
                                        r.getOldInfractions().put(r.getOldInfractions().size()-1, inf);
                                        DBmanager.saveRecord(r);
                                    }
                                }
                            }else{
                                Record r = DBmanager.getRecord(UUID.fromString(rtnclss.getObuuid()));
                                Infraction saved = null;
                                if(r.getCurrentInfraction() == null){
                                    saved = r.getOldInfractions().get(r.getOldInfractions().size()-1);
                                }else{
                                    saved = r.getCurrentInfraction();
                                }
                                inf.setDone(saved.isDone());
                                inf.setFinished(saved.getFinished());
                                inf.setRePromoted(saved.isRePromoted());
                                inf.setStarted(saved.isStarted());
                                if(inf.getFinished() == null){
                                    DBmanager.Bans.put(UUID.fromString(rtnclss.getObuuid()), inf);
                                    DBmanager.saveBan(UUID.fromString(rtnclss.getObuuid()));
                                    DBmanager.Bans.remove(UUID.fromString(rtnclss.getObuuid()));
                                    r.setCurrentInfraction(inf);
                                    DBmanager.saveRecord(r);
                                }else{
                                    r.getOldInfractions().put(r.getOldInfractions().size()-1, inf);
                                    DBmanager.saveRecord(r);
                                }
                            }
                            if(!rtnclss.isComplete()){
                                ServletDBmanager.Incomplete.add(inf);
                            }
                            outToClient.writeBytes(EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                            LogUtil.printDebug("wrote: " + EnforcerSuite.getJSonParser().writeValueAsString(rtnmsg));
                        }else if(req[0].contains("request")){
                            RequestType request = EnforcerSuite.getJSonParser().readValue(req[0], RequestType.class);
                            if(request.getBase().equalsIgnoreCase("archive")){
                                DBmanager.archiveBan(UUID.fromString(request.getArgs()[0]));
                            }
                        }else if(req[0].contains("validate")){
                            outToClient.writeBytes(String.valueOf(TCPkeyHandle.addKey(req[1], req[2])));
                        }
                    } catch (IOException ex) {
                        LogUtil.printErr("Failed to Decode from forum");
                        LogUtil.printDebugStack(ex);
                    }
                }
            } catch (IOException ex) {
                LogUtil.printErr("Failed to Decode from forum");
                LogUtil.printDebugStack(ex);
            }
        }
    }
    
    public class RequestType{
        @Getter @Setter
        private String base;
        @Getter @Setter
        private String[] args;
        
        public RequestType(){}
        
    }
    
    public static class ErrorType{
        @Getter @Setter
        private String msg;
        @Getter @Setter
        private int err;
        
        public ErrorType(){}
        
    }
}
