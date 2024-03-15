

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author imino
 */

public class TestIBEAES {

    public static void IBEalltypeoffilesEncryptiondecryptiondemo(Pairing pairing, SettingParameters sp, KeyPair keys, String filepath) {
        try {
            FileInputStream in = new FileInputStream(filepath);
            byte[] filebytes = new byte[in.available()];
            in.read(filebytes);

            String message = new String(filebytes);
            System.out.println("Encryption ....");

            // Encrypt the message and obtain the signature
            IBEcipher ibecipher = IBEBasicIdent.IBEencryption(pairing, sp.getP(), sp.getP_pub(), filebytes, keys.getPk());
            SchnorrSignature sig = signature.sign(pairing, sp.getP(), message, keys.getSk());

            System.out.println("---------------------");
            System.out.println("Decryption ....");

            // Decrypt the message
            byte[] resulting_bytes = IBEBasicIdent.IBEdecryption(pairing, sp.getP(), sp.getP_pub(), keys.getSk(), ibecipher);

            // Verify the signature after decryption
            String retrieved_message = new String(resulting_bytes);
            System.out.println("the decrypted message is: \n" + retrieved_message);
            if (signature.verify(pairing, sp.getP(), retrieved_message, sig, keys.getPk())) {
                System.out.println("Signature is verified.");
            } else {
                System.out.println("Signature verification failed.");
            }

            // Write the decrypted message to a file
            File f = new File("decryptionresult" + filepath.substring(filepath.lastIndexOf(".")));
            f.createNewFile();
            FileOutputStream fout = new FileOutputStream(f);
            fout.write(resulting_bytes);

            System.out.println("To access the resulting file, check the following path: " + f.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Pairing pairing = PairingFactory.getPairing("/home/yasmine/.cache/.fr-U93YWZ/params/curves/a.properties");
        System.out.println("Setup ....");
        SettingParameters sp = IBEBasicIdent.setup(pairing);
        System.out.println("Paremètre du système :");
        System.out.println("generator:" + sp.getP());
        System.out.println("P_pub:" + sp.getP_pub());
        System.out.println("MSK:" + sp.getMsk());
        String id = "test@gmail.com";
        System.out.println("-----------------------------");

        try {
            System.out.println("Key generation .....");
            KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), id);
            System.out.println("PK:" + keys.getPk());
            System.out.println("SK:" + keys.getSk());
            System.out.println("-----------------------------");
            IBEalltypeoffilesEncryptiondecryptiondemo(pairing, sp, keys, "/home/yasmine/Documents/test.txt");
            System.out.println("Fin ....");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}