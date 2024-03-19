package cryptoExo2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class basicident {
	
	public static Element generateQid(Pairing pairing, String identity) {
        // Compute Qid = H1(identity)
        Element Qid = pairing.getG1().newElement().setFromHash(identity.getBytes(), 0, identity.getBytes().length);
        return Qid;
    }
	
	public static PairKeys keygen(Pairing p, Element generator){
        Element sk=p.getZr().newRandomElement();
        
        Element pk=generator.duplicate().mulZn(sk);
        
        return new PairKeys(pk, sk);
    }
	
	public static byte[][] basicIdentityEncrypt(String message, String identity, Pairing pairing, PairKeys pairkeys) {
		
		//la fonction de hashage H1 (setFromHash) Qid = H1(id)
        Element Qid = pairing.getG1().newElement().setFromHash(identity.getBytes(), 0, identity.getBytes().length);
        Element r = pairing.getZr().newRandomElement();
        
        //Je calcule e(Qid, Ppub)^r
        Element eQidPpubPowr = pairing.pairing(Qid, pairkeys.getPubkey()).powZn(r);

        // Je combine message avec la valeur hashée du dernier resultat en utilisant toBytes() pour H2
        byte[] combinedMessage = concatenate(message.getBytes(), eQidPpubPowr.toBytes());
        

        // Compute C1
        Element C1 = pairkeys.getPubkey().duplicate().mulZn(r);

        return new byte[][]{C1.toBytes(), combinedMessage};
      
    }

	
	
	public static String basicIdentityDecrypt(byte[] C1Bytes, byte[] combinedMessage, Pairing pairing, PairKeys pairkeys, String identity) {
	    // Convert C1Bytes to Element U
	    Element U = pairing.getG1().newElementFromBytes(C1Bytes);
	    Element V = pairing.getG1().newElementFromBytes(combinedMessage);

	    // Compute Qid = H1(identity)
	    Element Qid = generateQid(pairing, identity);

	    // Calculate did = Qid * s
	    Element did = Qid.mulZn(pairkeys.getSecretkey());

	    // Calculate e(U, did)
	    Element eUdid = pairing.pairing(U, did);

	    // Convert e(U, did) to bytes
	    byte[] eUdidBytes = eUdid.toBytes();

	    // Extract the original message bytes from combined
	    int originalMessageLength = combinedMessage.length - eUdidBytes.length;
	    byte[] originalMessageBytes = Arrays.copyOfRange(combinedMessage, 0, originalMessageLength);

	    // Combine hashed result with V
	    Element combined = V.add(pairing.getG1().newElementFromBytes(eUdidBytes));

	    // Extract the message from combined
	    byte[] decryptedMessageBytes = combined.toBytes();

	    // Convert the decrypted message to a String
	    String decryptedMessage = new String(originalMessageBytes, StandardCharsets.UTF_8);

	    return decryptedMessage;
	}
	

	public static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
	
	
	public static byte[] encrypt(String m, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
	    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//création de l'objet chiffreur
	    MessageDigest digest = MessageDigest.getInstance("SHA1"); //la fonction de hachage
	    digest.update(key); //maj du hash avec notre cle
	    byte[] AESkey = Arrays.copyOf(digest.digest(), 16);//prend les 16 premiers octets du hash résultant et les utilise comme clé AES
	    SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");//la clé secrète utilisée dans le chiffrement AES
	    cipher.init(Cipher.ENCRYPT_MODE, keyspec);//initialise le chiffreur en mode chiffrement avec la clé secrète
	    
	    //prend le message m en octets UTF-8, le chiffre avec AES
	    byte[] ciphertext = cipher.doFinal(m.getBytes(StandardCharsets.UTF_8));
	    
	    return ciphertext;
	}

	public static String decrypt(byte[] ciphertext, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
	    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    MessageDigest digest = MessageDigest.getInstance("SHA1");
	    digest.update(key);
	    byte[] AESkey = Arrays.copyOf(digest.digest(), 16);
	    SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");
	    cipher.init(Cipher.DECRYPT_MODE, keyspec);
	    
	    //Décodez les données déchiffrées en Base64
	    byte[] decryptedBytes = cipher.doFinal(ciphertext);
	    
	    // Convertissez les octets déchiffrés en chaîne UTF-8
	    String m = new String(decryptedBytes, StandardCharsets.UTF_8);
	    
	    return m;
	}

	
	// Fonction pour réaliser le chiffrement hybride
	public static byte[][] hybridEncrypt(String message, String identity, Pairing pairing, PairKeys pairkeys) throws Exception {
	    //generation de la cle AES
	    byte[] aesKey = generateAESKey(pairing, identity);
	    
	    // Chiffrement à base d'identité du message AES Key
	    byte[][] basicIdentityCiphertext = basicident.basicIdentityEncrypt(Base64.getEncoder().encodeToString(aesKey), identity, pairing, pairkeys);
	 // Chiffrement AES du message
	    byte[] aesCiphertext = encrypt(message, aesKey);
	    
	    // Encodez les données chiffrées en Base64 avant de les retourner
	    String encodedC1 = Base64.getEncoder().encodeToString(basicIdentityCiphertext[0]);
	    String encodedCombinedMessage = Base64.getEncoder().encodeToString(basicIdentityCiphertext[1]);
	    return new byte[][] {encodedC1.getBytes(), encodedCombinedMessage.getBytes(), aesCiphertext};
	}

    
	// Fonction pour réaliser le déchiffrement hybride
	public static String hybridDecrypt(byte[] C1Bytes, byte[] combinedMessage, byte[] aesCiphertext, Pairing pairing, PairKeys pairkeys, String identity) throws Exception {
	    // Décodez les données chiffrées en Base64
	    byte[] decodedC1Bytes = Base64.getDecoder().decode(C1Bytes);
	    byte[] decodedCombinedMessage = Base64.getDecoder().decode(combinedMessage);
	    
	    // Déchiffrement à base d'identité pour récupérer la clé AES
	    String aesKeyBase64 = basicident.basicIdentityDecrypt(decodedC1Bytes, decodedCombinedMessage, pairing, pairkeys, identity);
	    byte[] aesKey = Base64.getDecoder().decode(aesKeyBase64);
	    
	    // Déchiffrement AES du message
	    String decryptedMessage = decrypt(aesCiphertext, aesKey);
	    
	    return decryptedMessage;
	}
	
	// Function to perform hybrid encryption on a file

	public static byte[][] hybridEncryptFile(String filePath, String identity, Pairing pairing, PairKeys pairkeys) throws Exception {
	    // Read the file into a byte array
	    FileInputStream in = new FileInputStream(filePath);
	    byte[] fileBytes = new byte[in.available()];
	    in.read(fileBytes);
	    in.close();
	    
	    // Encrypt the file content using AES
	    byte[] aesKey = generateAESKey(pairing, identity);
	    byte[] aesCiphertext = encrypt(fileBytes.toString(), aesKey);
	    
	    // Encrypt the AES key using basic identity encryption
	    byte[][] basicIdentityCiphertext = basicIdentityEncrypt(Base64.getEncoder().encodeToString(aesKey), identity, pairing, pairkeys);
	    
	    // Encode the ciphertexts in Base64 before returning
	    String encodedC1 = Base64.getEncoder().encodeToString(basicIdentityCiphertext[0]);
	    String encodedCombinedMessage = Base64.getEncoder().encodeToString(basicIdentityCiphertext[1]);
	    return new byte[][] { encodedC1.getBytes(), encodedCombinedMessage.getBytes(), aesCiphertext };
	}

	public static void hybridDecryptFile(byte[] C1Bytes, byte[] combinedMessage, byte[] aesCiphertext, String outputFilePath, Pairing pairing, PairKeys pairkeys, String identity) throws Exception {
	    // Decode the ciphertexts from Base64
	    byte[] decodedC1Bytes = Base64.getDecoder().decode(C1Bytes);
	    byte[] decodedCombinedMessage = Base64.getDecoder().decode(combinedMessage);
	    
	    // Decrypt the AES key using basic identity decryption
	    String aesKeyBase64 = basicIdentityDecrypt(decodedC1Bytes, decodedCombinedMessage, pairing, pairkeys, identity);
	    byte[] aesKey = Base64.getDecoder().decode(aesKeyBase64);
	    
	    // Decrypt the file content using AES
	    String decryptedFileBytes = (String) decrypt(aesCiphertext, aesKey);
	    
	    // Write the decrypted file content to the output file
	    FileOutputStream out = new FileOutputStream(outputFilePath);
	    out.write(decryptedFileBytes.getBytes());
	    out.close();
	}

  
    public static byte[] generateAESKey(Pairing pairing, String identity) throws NoSuchAlgorithmException {
        // Use a hash function to generate AES Key from identity
    	MessageDigest digest = MessageDigest.getInstance("SHA1"); //la fonction de hashage
		byte[] AESkey = Arrays.copyOf(digest.digest(identity.getBytes(StandardCharsets.UTF_8)), 16);//prend les 16 premiers octets du hash resultant et les utilise comme cle AES
		SecretKeySpec keyspec = new SecretKeySpec(AESkey, "AES");//la cle secrete utilisee dans le chiffrement AES
		return AESkey;
        
    }
	
	 public static void main(String[] args) {
	        // Example parameters initialization
	        Pairing pairing = PairingFactory.getPairing("/home/yasmine/.cache/.fr-U93YWZ/params/curves/a.properties");
	        Element generator = pairing.getG1().newRandomElement(); // Example generator
	        String identity = "example_identity"; // Example identity
	        String message = "lol"; // Example message

	        // Generate keys for the user
	        PairKeys pairKeys = basicident.keygen(pairing, generator);

	        // Encrypt the message
	       /* byte[][] ciphertext = basicident.basicIdentityEncrypt(message, identity, pairing, pairKeys);
	        //String[] ciphertext = basicident.basicIdentityEncrypt1(message, identity, pairing, pairKeys);
	        System.out.println("crypted Message: " + ciphertext);
	   
	        // Decrypt the message
	        String decryptedMessage = basicIdentityDecrypt(ciphertext[0], ciphertext[1], pairing, pairKeys, identity);
	        System.out.println("Decrypted Message: " + decryptedMessage);
	        */
	        
	        try {
	            // Encrypt the message
	            byte[][] ciphertext = basicident.hybridEncrypt(message, identity, pairing, pairKeys);
	            System.out.println("Encrypted Message: " + Arrays.toString(ciphertext));

	            // Decrypt the message
	            String decryptedMessage = basicident.hybridDecrypt(ciphertext[0], ciphertext[1], ciphertext[2], pairing, pairKeys, identity);
	            System.out.println("Decrypted Message: " + decryptedMessage);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        
	        
	      /*  String inputFilePath = "/home/yasmine/filetest"; // Path to input file
	        String outputFilePath = "/home/yasmine/decrypted_file.txt"; // Path to output decrypted file

	        try {
	           

	            // Encrypt the file
	            byte[][] ciphertext = hybridEncryptFile(inputFilePath, identity, pairing, pairKeys);
	            System.out.println("File encrypted successfully.");

	            // Write the encrypted content to a file
	            FileOutputStream out = new FileOutputStream("encrypted_file.bin");
	            out.write(ciphertext[0]); // C1Bytes
	            out.write(ciphertext[1]); // combinedMessage
	            out.write(ciphertext[2]); // aesCiphertext
	            out.close();
	            System.out.println("Encrypted file written successfully.");

	            // Decrypt the file
	            hybridDecryptFile(ciphertext[0], ciphertext[1], ciphertext[2], outputFilePath, pairing, pairKeys, identity);
	            System.out.println("File decrypted successfully.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        

	        try {
	           
	            // Encrypt the file
	            byte[][] ciphertext = hybridEncryptFile(inputFilePath, identity, pairing, pairKeys);
	            System.out.println("File encrypted successfully.");

	            // Write the encrypted content to a file
	            FileOutputStream out = new FileOutputStream("encrypted_file.bin");
	            out.write(ciphertext[0]); // C1Bytes
	            out.write(ciphertext[1]); // combinedMessage
	            out.write(ciphertext[2]); // aesCiphertext
	            out.close();
	            System.out.println("Encrypted file written successfully.");

	            // Decrypt the file
	            byte[] decryptedContent = hybridDecryptFile(ciphertext[0], ciphertext[1], ciphertext[2], outputFilePath, pairing, pairKeys, identity);

	            // Write the decrypted content to a file
	            FileOutputStream decryptedOut = new FileOutputStream(outputFilePath);
	            decryptedOut.write(decryptedContent);
	            decryptedOut.close();
	            System.out.println("Decrypted file written successfully.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }*/
	    }
	    
	 
	 
	 
		
		/*public static String[] basicIdentityEncrypt1(String message, String identity, Pairing pairing, PairKeys pairkeys) {
		    // La fonction de hachage H1 (setFromHash) Qid = H1(id)
		    Element Qid = generateQid(pairing, identity);
		    Element r = pairing.getZr().newRandomElement();
		    
		    // Je calcule e(Qid, Ppub)^r
		    Element eQidPpubPowr = pairing.pairing(Qid, pairkeys.getPubkey()).powZn(r);

		    // Je combine message avec la valeur hashée du dernier resultat en utilisant toBytes() pour H2
		    byte[] combinedMessage = concatenate(message.getBytes(), eQidPpubPowr.toBytes());

		    // Compute C1
		    Element C1 = pairkeys.getPubkey().duplicate().mulZn(r);

		    // Convertir les octets en Base64 pour un affichage plus lisible
		    String C1Base64 = Base64.getEncoder().encodeToString(C1.toBytes());
		    String combinedMessageBase64 = Base64.getEncoder().encodeToString(combinedMessage);

		    return new String[]{C1Base64, combinedMessageBase64};
		}

		public static String basicIdentityDecrypt2(String C1Base64, String combinedMessageBase64, Pairing pairing, PairKeys pairkeys, String identity) {
		    // Convertir les données Base64 en octets
		    byte[] C1Bytes = Base64.getDecoder().decode(C1Base64);
		    byte[] combinedMessage = Base64.getDecoder().decode(combinedMessageBase64);

		    // Convert C1Bytes to Element U
		    Element U = pairing.getG1().newElementFromBytes(C1Bytes);
		    Element V = pairing.getG1().newElementFromBytes(combinedMessage);

		    // Compute Qid = H1(identity)
		    Element Qid = generateQid(pairing, identity);

		    // Calculate did = Qid * s
		    Element did = Qid.mulZn(pairkeys.getSecretkey());

		    // Calculate e(U, did)
		    Element eUdid = pairing.pairing(U, did);

		    // Convert e(U, did) to bytes
		    byte[] eUdidBytes = eUdid.toBytes();

		    // Combine hashed result with V
		    Element combined = V.add(pairing.getG1().newElementFromBytes(eUdidBytes));
		    
		    // Extraire le message de combined
		    byte[] decryptedMessageBytes = combined.toBytes();

		    // Convertir le message déchiffré en une chaîne en utilisant l'encodage ISO-8859-1
		    String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.ISO_8859_1);

		    return decryptedMessage;
		}
		
		public static String basicIdentityDecrypt(byte[] C1Bytes, byte[] combinedMessage, Pairing pairing, PairKeys pairkeys, String identity) {
	    // Convert C1Bytes to Element U
	    Element U = pairing.getG1().newElementFromBytes(C1Bytes);
	    Element V = pairing.getG1().newElementFromBytes(combinedMessage);

	    // Compute Qid = H1(identity)
	    Element Qid = generateQid(pairing, identity);

	    // Calculate did = Qid * s
	    Element did = Qid.mulZn(pairkeys.getSecretkey());

	    // Calculate e(U, did)
	    Element eUdid = pairing.pairing(U, did);

	    // Convert e(U, did) to bytes
	    byte[] eUdidBytes = eUdid.toBytes();

	    // Combine hashed result with V
	    Element combined = V.add(pairing.getG1().newElementFromBytes(eUdidBytes));
	    
	    // Extract the message from combined
	    byte[] decryptedMessageBytes = combined.toBytes();

	    // Convert the decrypted message to a String
	    String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
	    

	    return decryptedMessage;
	}
	
		*/

	 }