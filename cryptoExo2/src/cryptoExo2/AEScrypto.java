package cryptoExo2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AEScrypto {
	
	public static byte[] encrypt(String m, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//creation de l'objet chiffreur
		MessageDigest digest = MessageDigest.getInstance("SHA1"); //la fonction de hashage
		digest.update(key); //maj du hash avec notre cle
		byte[] AESkey = Arrays.copyOf(digest.digest(), 16);//prend les 16 premiers octets du hash resultant et les utilise comme cle AES
		SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");//la cle secrete utilisee dans le chiffrement AES
		cipher.init(Cipher.ENCRYPT_MODE, keyspec);//initialise le chiffreur en mode chiffrement avec la clé secrete
		
		//prend le message m en octets UTF-8, le chiifre avec AES, puis encode le resultat chiffré en  Base64 pour une representation textuelle securisee du texte chifrée
		byte[] ciphertext = Base64.getEncoder().encode(cipher.doFinal(m.getBytes("UTF-8")));
		
		return ciphertext;
		
	}
	
	public static String decrypt(byte[] ciphertext, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		digest.update(key);
		byte[] AESkey = Arrays.copyOf(digest.digest(), 16);
		SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");
		cipher.init(Cipher.DECRYPT_MODE, keyspec);
		
		String m = new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
		
		return m;
		
	}

	public static void encryptFile(String inputFile, String outputFile, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(key);
        byte[] AESkey = Arrays.copyOf(digest.digest(), 16);
        SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keyspec);
        
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] encryptedBlock = cipher.update(buffer, 0, bytesRead);
            outputStream.write(encryptedBlock);
        }
        
        byte[] finalBlock = cipher.doFinal();
        outputStream.write(finalBlock);
        
        inputStream.close();
        outputStream.close();
    }
    
    public static void decryptFile(String inputFile, String outputFile, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(key);
        byte[] AESkey = Arrays.copyOf(digest.digest(), 16);
        SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keyspec);
        
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] decryptedBlock = cipher.update(buffer, 0, bytesRead);
            outputStream.write(decryptedBlock);
        }
        
        byte[] finalBlock = cipher.doFinal();
        outputStream.write(finalBlock);
        
        inputStream.close();
        outputStream.close();
    }
	

}
