package cryptoExo2;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import it.unisa.dia.gas.jpbc.Element;

public class elgamalfct {
	
	public static PairKeys keygen(Pairing p, Element generator) {
		Element sk = p.getZr().newRandomElement();
		Element pk = generator.duplicate().mulZn(sk);
		
		return new PairKeys(pk, sk);
	}
	
	public static elgamal elgamalcr(Pairing p, Element generator, String m, Element pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		
		Element r = p.getZr().newRandomElement();
		Element K = p.getZr().newRandomElement();
		Element V = pk.duplicate().mulZn(r).add(K);
		System.out.println("V="+ V);
		byte[] ciphertext = AEScrypto.encrypt(m, K.toBytes());
		Element U = generator.duplicate().mulZn(r);	
		
		return new elgamal(U, V, ciphertext);
		
	}
	
	public static String elgamaldecr(Pairing p, Element generator, elgamal c, Element sk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		Element u_p = c.getU().duplicate().mulZn(sk);
		Element plainkey = c.getV().duplicate().sub(u_p);
		String plaintext = AEScrypto.decrypt(c.getAEScyphertext(), plainkey.toBytes());
		return null;
	}
	
	public static void encdectMessage(String message, Pairing pairing, Element generator, PairKeys pairkeys) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		
		System.out.println("bytelength : " + message.getBytes().length);
		elgamal c = elgamalcr(pairing, generator, message, pairkeys.getPubkey());
		System.out.println("the message is : \n"+ elgamaldecr(pairing, generator, c, pairkeys.getSecretkey()));
		
	}
	
	public static void fileEncDec(String filepath, Pairing pairing, Element generator, PairKeys pairkeys) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		FileInputStream in = new FileInputStream(filepath);
		
		byte[] filebytes = new byte[in.available()];
		System.out.println("taille du fichier en byte:" + filebytes.length);
		in.read(filebytes);
		System.out.println("bytelength : "+ filepath.getBytes().length);
		String message = new String(filebytes);
		
		elgamal c = elgamalcr(pairing, generator, message, pairkeys.getPubkey());
		
		String retrieved_message = elgamaldecr(pairing, generator, c, pairkeys.getSecretkey());
		System.out.println("the decrypted message is : "+ retrieved_message);	
		
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Pairing pairing = PairingFactory.getPairing("/home/yasmine/.cache/.fr-U93YWZ/params/curves/a.properties");
		Element generator = pairing.getG1().newRandomElement();
		PairKeys pairkeys = keygen(pairing, generator);
		fileEncDec("/home/yasmine/Documents/test", pairing, generator, pairkeys);
	}
}
