package cryptoExo2;

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

public class chiffHybride {

    public static Element generateQid(Pairing pairing, String identity) {
        // Compute Qid = H1(identity)
        Element Qid = pairing.getG1().newElement().setFromHash(identity.getBytes(), 0, identity.getBytes().length);
        return Qid;
    }

    public static PairKeys keygen(Pairing p, Element generator) {
        Element sk = p.getZr().newRandomElement();

        Element pk = generator.duplicate().mulZn(sk);

        return new PairKeys(pk, sk);
    }

    public static byte[][] basicIdentityEncrypt(String message, String identity, Pairing pairing, PairKeys pairkeys) {

        // Compute Qid = H1(identity)
        Element Qid = generateQid(pairing, identity);
        Element r = pairing.getZr().newRandomElement();

        // Compute e(Qid, Ppub)^r
        Element eQidPpubPowr = pairing.pairing(Qid, pairkeys.getPubkey()).powZn(r);

        // Combine message with the hashed value of the last result using toBytes() for H2
        byte[] combinedMessage = concatenate(message.getBytes(), eQidPpubPowr.toBytes());

        // Compute C1
        Element C1 = pairkeys.getPubkey().duplicate().mulZn(r);

        return new byte[][] { C1.toBytes(), combinedMessage };

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

    public static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }


 // Function to perform hybrid encryption
    public static byte[][] hybridEncrypt(String message, String identity, Pairing pairing, PairKeys pairkeys)
            throws Exception {
        // AES encryption of the message
        byte[] aesKey = generateAESKey(pairing, identity);
        
        // Identity-based encryption of the AES Key
        byte[][] basicIdentityCiphertext = basicIdentityEncrypt(aesKey.toString(), identity, pairing, pairkeys);
        byte[] aesCiphertext = AEScrypto.encrypt(message, aesKey);

        return new byte[][] { basicIdentityCiphertext[0], basicIdentityCiphertext[1], aesCiphertext };
    }

    // Function to perform hybrid decryption
    public static String hybridDecrypt(byte[] C1Bytes, byte[] combinedMessage, byte[] aesCiphertext, Pairing pairing,
            PairKeys pairkeys, String identity) throws Exception {
        // Identity-based decryption to retrieve the AES Key
        byte[] aesKey = basicIdentityDecrypt(C1Bytes, combinedMessage, pairing, pairkeys, identity).getBytes();

        // AES decryption of the message
        String decryptedMessage = AEScrypto.decrypt(aesCiphertext, aesKey);

        return decryptedMessage;
    }

    // Function to generate AES Key from identity
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
        String message = "Hello, World!"; // Example message

        // Generate keys for the user
        PairKeys pairKeys = keygen(pairing, generator);

        try {
            // Encrypt the message
            byte[][] ciphertext = hybridEncrypt(message, identity, pairing, pairKeys);
            System.out.println("Encrypted Message: " + Arrays.toString(ciphertext));

            // Decrypt the message
            String decryptedMessage = hybridDecrypt(ciphertext[0], ciphertext[1], ciphertext[2], pairing, pairKeys,
                    identity);
            System.out.println("Decrypted Message: " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


