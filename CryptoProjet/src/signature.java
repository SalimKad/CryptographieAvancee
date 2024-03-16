import java.security.MessageDigest;

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


public class signature {
	
	public static SchnorrSignature sign(Pairing p, Element generator, String m, Element Sk) throws NoSuchAlgorithmException {
	    Element k = p.getZr().newRandomElement();
	    Element R = generator.duplicate().mulZn(k);

	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    md.update(m.getBytes());
	    md.update(R.toBytes());

	    Element e = p.getZr().newElement();
	    e.setFromBytes(md.digest());

	    // Convert the private key to the appropriate type
	    Element SkElement = p.getZr().newElementFromBytes(Sk.toBytes());

	    // Now perform operations with Sk
	    Element s = k.duplicate().sub(e.duplicate().mul(SkElement));

	    return new SchnorrSignature(e, s);
	}



	public static boolean verify(Pairing p, Element generator, String m, SchnorrSignature sig, String pk) throws NoSuchAlgorithmException {

	    Element R_p = generator.duplicate().mulZn(sig.getS()).add(p.getG1().newElementFromBytes(pk.getBytes()).mulZn(sig.getE())); // Use p.getG1() instead of p.getZr()

	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    md.update(m.getBytes());
	    md.update(R_p.toBytes());

	    Element e_p = p.getZr().newElement();
	    e_p.setFromBytes(md.digest());

	    System.out.println("e_p=" + e_p);

	    return e_p.isEqual(sig.getE());

	}
	


	
	

}
