package projet_crypto;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class IBEBasicIdent {
   
  public static SettingParameters setup(Pairing pairing){ // setup phase
     
      Element p=pairing.getG1().newRandomElement(); // choix d'un générateur
     
      Element msk=pairing.getZr().newRandomElement(); //choix de la clef du maitre
     
      Element p_pub=p.duplicate().mulZn(msk); // calcule de la clef publique du système
     
      return new SettingParameters(p, p_pub, msk); //instanciation d'un objet comportant les parametres du système
  }
   
    public static KeyPair keygen(Pairing pairing,Element msk, String id) throws NoSuchAlgorithmException{
       
        byte [] bytes=id.getBytes(); // représentation de l'id sous format binaire
       
        Element Q_id=pairing.getG1().newElementFromHash(bytes, 0, bytes.length); //H_1(id)
       
        Element sk=Q_id.duplicate().mulZn(msk); // calcule de la clef privée correspandante à id
       
        return new KeyPair(id, sk); // instanciation d'un objet comportant les composants de la clefs (clef publique=id et clef privée)
    }
   
    public static byte[] Xor(byte[]a,byte[]b ){ //returns a Xor b
       
        byte[] result=new byte[a.length];
       
       
        for (int i = 0; i < a.length; i++) {
           
            result[i]=(byte)((int)a[i]^(int)b[i]);
        }
        return result;
    }
   
    public static IBEcipher IBEencryption(Pairing pairing, Element generator,Element p_pub,byte[] message, String pk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        // methode de chiffrement BasicID
       
         Element aeskey=pairing.getGT().newRandomElement(); //choix de la clef symmetrique AES
         System.out.println("la valeur de aeskey: " + aeskey);
       
         byte [] bytes=pk.getBytes(); // transformation de la clef publique (id) au format binaire
       
         Element r=pairing.getZr().newRandomElement(); // nombre aléatoire choisi dans Z_r
         
         Element U=generator.duplicate().mulZn(r); // rP (dans le slide du cours)
         
         System.out.println("la valeur de U de encrypt : " + U);
         
         Element Q_id=pairing.getG1().newElementFromHash(bytes, 0, bytes.length); // H_1(id) (dans le slide du cours)
       
         Element pairingresult=pairing.pairing(Q_id, p_pub); //e(Q_id,P_pub) dans le slide du cours
         
         System.out.println("before pairing result:"+pairingresult);
         
       
         pairingresult.powZn(r);
       
        System.out.println("after pairing result:"+pairingresult);
       
        byte[] V=Xor(aeskey.toBytes(), pairingresult.toBytes()); //K xor e(Q_id,P_pub)^r
       
        byte[] Aescipher=AEScrypto.encrypt(message, aeskey.toBytes());  // chiffrement AES
        
        System.out.println("aes cipher au chiff  "+Aescipher);
       
        return new IBEcipher(U.toBytes(), V, Aescipher); //instanciation d'un objet representant un ciphertext hybride combinant (BasicID et AES)
    }
    
   
   
   
     public static byte[] IBEdecryption(Pairing pairing, Element generator,Element p_pub,Element sk,IBEcipher C) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        //Déchiffrement IBE
   
    Element U = pairing.getG1().newElementFromBytes(C.getU());
    
    System.out.println("la valeur de U en decrypt: " + U);
       
        Element pairingresult= pairing.pairing(sk, U); //e(d_id,U) dans le slide du cours avec d_id= la clef  privée de l'utilisateur
        
        System.out.println("la valeur de pairing result : " + pairingresult);
       
        byte[] resultingAeskey=Xor(C.getV(), pairingresult.toBytes());  // V xor H_2(e(d_id,U))=K avec K est la clef symmetrique AES
        System.out.println("la valeur de pairing result en bytes : " + pairingresult.toBytes());
        System.out.println("la valeur de C.getV : " + C.getV());
        System.out.println("la valeur de resultingAesKey : " + resultingAeskey);
        System.out.println("la valeur de  C.getAescipher(): " + C.getAescipher());
        byte[] resultingdecryptionbytes = AEScrypto.decrypt(C.getAescipher(), resultingAeskey); // déchiffrement AES
        System.out.println("la valeur de resultingdecryptionbytes : " + resultingdecryptionbytes);
       
     return resultingdecryptionbytes; //retourner le résultat du déchiffrement= plaintext si le déchiffement a été fait avec succès
     }
   
   
   
}
