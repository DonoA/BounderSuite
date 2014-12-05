/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.enforcersuite.DBmanager;

import com.mcmiddleearth.enforcersuite.Destination;
import com.mcmiddleearth.enforcersuite.OathBreaker;
import com.mcmiddleearth.enforcersuite.enforcerSuite;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Donovan
 */
public class DBmanager {
    public static HashMap<String, OathBreaker> OBs = new HashMap<>();
    
    private static File DataFile = new File(enforcerSuite.getPlugin().getDataFolder() + System.getProperty("file.separator") + "OBdat");
    
    public static void save(String pName){
        if(!DataFile.exists()){
            DataFile.mkdirs();
        }
        File saveStart = new File(DataFile + System.getProperty("file.separator") + pName + ".new.obdat");
        File saveFin = new File(DataFile + System.getProperty("file.separator") + pName + ".obdat");
        if(saveFin.exists()&&saveStart.exists()){
            saveFin.delete();
            saveFin.renameTo(saveFin);
        }
        
        FileWriter fr = null;
        try {
            fr = new FileWriter(saveStart.toString());
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter writer = new PrintWriter(fr);
        writer.println(pName);
        writer.close();
        try {
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static boolean load(String pName){
        File save = new File(DataFile + System.getProperty("file.separator") + pName + ".obdat");
        if(!save.exists())
            return false;                                   
        
        return true;
    }
    public static void archive(String pName){
        
    }
    public static Destination LoadDest(int sev){
        String uri = enforcerSuite.getPlugin().getDataFolder() + enforcerSuite.getPlugin().getFileSep() + "DestinationDB" + enforcerSuite.getPlugin().getFileSep() + String.valueOf(sev) + enforcerSuite.getPlugin().getFileSep();
        new File(uri).listFiles()[];//fack
        Scanner s = new Scanner(uri);
        try{
            int Bounds[] = new int[] {Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine()), Integer.parseInt(s.nextLine())};
            String name = s.nextLine();
        }catch(NumberFormatException e){
            System.out.println("Bad Destination File");
        }
        int line = Integer.parseInt(s.nextLine());
        
        
        
        return null;
    }
}
