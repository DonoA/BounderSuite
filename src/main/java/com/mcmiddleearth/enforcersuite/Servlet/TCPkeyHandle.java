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

import com.mcmiddleearth.enforcersuite.EnforcerSuite;
import com.mcmiddleearth.enforcersuite.Utils.LogUtil;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Donovan
 */
public class TCPkeyHandle {
    
    private static SecretKey secret;
    
    private static String Password = EnforcerSuite.getPlugin().getConfig().getString("cipher");
    
    private static String AdminKey = EnforcerSuite.getPlugin().getConfig().getString("adminkey");
    
    private static HashMap<String, Date> ValidKeys = new HashMap<String, Date>();
    
    private static byte[] Salt = new byte[8];
    
    static{
        try {
            new SecureRandom().nextBytes(Salt);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(Password.toCharArray(), Salt, 65536, 256);
            secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean addKey(String AKey, String Key){
        if(AdminKey.equals(AKey)){
            LogUtil.printDebug("added: "+Key);
            ValidKeys.put(Key, new Date());
            return true;
        }
        return false;
    }
    
    public static boolean validRequest(String Key){
        LogUtil.printDebug("checking key "+Key);
        if(ValidKeys.containsKey(Key)){
            if(ValidKeys.get(Key).after(new Date(System.currentTimeMillis() - (12 * 60 * 60 * 1000)))){
                ValidKeys.remove(Key);
                return true;
            }else{
                ValidKeys.remove(Key);
            }
        }
        return false;
    }

    public static EncryptClass Encrypt(String request){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(request.getBytes("UTF-8"));
            return new EncryptClass(ciphertext, iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidParameterSpecException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String Decrypt(String response){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] plaintext = cipher.doFinal(response.getBytes("UTF-8"));
            return String.valueOf(plaintext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidParameterSpecException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TCPkeyHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static class EncryptClass{
        @Getter @Setter
        private String ciphertext;
        
        @Getter @Setter
        private byte[] iv;
        
        public EncryptClass(byte[] CT, byte[] iv){
            this.ciphertext = String.valueOf(CT);
            this.iv = iv;
        }
    }
}
