package cryptoExo2;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class testAES {
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		final String secretKey = "voici la cle secrete";
		Scanner sc = new Scanner(System.in);
		System.out.println("entrez un message a chiffrer");
		String originalString = sc.nextLine();
		
		System.out.println("Entrer le chemin du fichier a chiffrer : ");
		String file = sc.nextLine();
		System.out.println("Entrer le chemin ou vous voulez sauvegarder le fichier chiffre: ");
		String encryptedFile = sc.nextLine();
		//AEScrypto.encryptFile(file, encryptedFile.getBytes(), secretKey.getBytes("UTF-8"));
		
		
		String encryptedString = new String(AEScrypto.encrypt(originalString, secretKey.getBytes("UTF-8")));
		String decryptedString = AEScrypto.decrypt(encryptedString.getBytes("UTF-8"), secretKey.getBytes());
		
		System.out.println(originalString);
		System.out.println(encryptedString);
		System.out.println(decryptedString);
	}

}
