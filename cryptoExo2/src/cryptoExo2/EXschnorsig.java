package cryptoExo2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author imino
 */
public class EXschnorsig {
   
    public static PairKeys keygen(Pairing p, Element generator){
        Element sk=p.getZr().newRandomElement();
        
        Element pk=generator.duplicate().mulZn(sk);
        
        return new PairKeys(pk, sk);
    }
    
    public static elgamal elGamalencr(Pairing p, Element generator,String m, Element Pk) throws UnsupportedEncodingException{
      //méthode de chiffrement hybrid combinant El-gamal et AES
       try {
           Element r=p.getZr().newRandomElement();
           Element K=p.getG1().newRandomElement(); //clef symmetrique
           Element V=Pk.duplicate().mulZn(r);
           V.add(K);
           System.out.println("V="+V);
           byte[]ciphertext=AEScrypto.encrypt(m, K.toBytes());
           
           Element U=generator.duplicate().mulZn(r);
           return new elgamal(U, V,ciphertext);
       } catch (NoSuchAlgorithmException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (NoSuchPaddingException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (InvalidKeyException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IllegalBlockSizeException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (BadPaddingException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }
    
     public static String elGamaldec(Pairing p, Element generator,elgamal c, Element Sk){
          //méthode de déchiffrement hybrid combinant El-gamal et AES
    
       try {
           Element u_p=c.getU().duplicate().mulZn(Sk);
           System.out.println("V_p="+u_p);
           
           Element plain=c.getV().duplicate().sub(u_p); //clef symmetrique retrouvée
           System.out.println("retrievd key="+plain);
           
           String plainmessage=AEScrypto.decrypt(c.getAEScyphertext(), plain.toBytes());
           
           
           return plainmessage;
       } catch (NoSuchAlgorithmException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (NoSuchPaddingException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (InvalidKeyException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IllegalBlockSizeException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (BadPaddingException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }
    
    public static SchnorrSignature sign(Pairing p, Element generator,String m, Element Sk) throws NoSuchAlgorithmException{
    
        Element k=p.getZr().newRandomElement();
        Element R=generator.duplicate().mulZn(k);
        
        MessageDigest md=MessageDigest.getInstance("SHA-256");
        md.update(m.getBytes());
        md.update(R.toBytes());
        
        Element e=p.getZr().newElement();
        e.setFromBytes(md.digest());
        
        System.out.println("e="+e);
        
        Element s=k.duplicate().sub(e.duplicate().mul(Sk));
        
        System.out.println("s="+s);
        
        return new SchnorrSignature(e, s);
    }
    
    public static boolean verify(Pairing p, Element generator,String m, SchnorrSignature sig, Element pk) throws NoSuchAlgorithmException{
        
        Element R_p=generator.duplicate().mulZn(sig.getS()).add(pk.duplicate().mulZn(sig.getE()));
   
        
        MessageDigest md=MessageDigest.getInstance("SHA-256");
        md.update(m.getBytes());
        md.update(R_p.toBytes());
        
        Element e_p=p.getZr().newElement();
        e_p.setFromBytes(md.digest());
        
        System.out.println("e_p="+e_p);
        
        return e_p.isEqual(sig.getE());
    }
    
    public static void messageEncryption_decryptiondemo(String message,Pairing pairing, Element generator, PairKeys pairkeys){
        
       try {
           System.out.println("bytelenght:"+message.getBytes().length);
           
           
           elgamal c=elGamalencr(pairing, generator, message, pairkeys.getPubkey());
           
           System.out.println("the message is: \n"+elGamaldec(pairing, generator, c, pairkeys.getSecretkey()));
           
       } catch (UnsupportedEncodingException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       }
           
        
    }
    
     public static void fileEncryption_decryptiondemo(String filepath,Pairing pairing, Element generator, PairKeys pairkeys) throws NoSuchAlgorithmException{
        
       try {
            FileInputStream in=new FileInputStream(filepath);
            
            byte[] filebytes=new byte[in.available()];
            
            System.out.println("taille de fichier en byte:"+filebytes.length);
            
            in.read(filebytes);
            
           System.out.println("bytelenght:"+filepath.getBytes().length);
           
           String message=new String(filebytes);
           
           
           elgamal c=elGamalencr(pairing, generator, message, pairkeys.getPubkey());
          SchnorrSignature sig=sign(pairing, generator, message,pairkeys.getSecretkey());
           
           String retrived_message=elGamaldec(pairing, generator, c, pairkeys.getSecretkey());
         
           System.out.println("the decrypted message is: \n"+retrived_message);
            
            if(verify(pairing, generator, retrived_message, sig,pairkeys.getPubkey())) System.out.println("the signature is verified ... ");
            else System.out.println("the signature is not verified ... ");
         
       } catch (UnsupportedEncodingException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (FileNotFoundException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
       }
           
        
    }
    
    
    public static void main(String[] args) throws NoSuchAlgorithmException {
       
        Pairing pairing = PairingFactory.getPairing("/home/yasmine/.cache/.fr-U93YWZ/params/curves/a.properties"); //chargement des paramètres de la courbe elliptique  
                                                                        //(replacer "curveParamsd159" par un chemin vers le fichier de configuration de la courbe)
        Element generator=pairing.getG1().newRandomElement(); //génerateur
   
        PairKeys pairkeys=keygen(pairing, generator); //keygen
     
        //test chiffrement, déchiffrement, signature et vérification
        fileEncryption_decryptiondemo("/home/yasmine/Documents/test", pairing, generator, pairkeys);
     
       
        
        
       /*  
        //test signature
         String message="saluuuuut ça va ? oui trés bien et toi ? ";
        SchnorrSignature sig=sign(pairing, generator, message,pairkeys.getSecretkey());
        
        
      //  String message2="saluuuuut ça va ? oui trés bien et toi "; //tester avec le message modifié et comparer le résultat
     
        if(verify(pairing, generator, message, sig,pairkeys.getPubkey())) System.out.println("signature verified ... ");
        else System.out.println("signature not verified ... ");
        */
        
        
        
    }
}