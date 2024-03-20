package com.example.projet_crypto_v2;

import java.io.Serializable;



import it.unisa.dia.gas.jpbc.Element;



public class IBEcipher implements Serializable {

    

    private byte[] U; // rP (vu dans le cours)

    

    byte[] V; // K xor e(Q_id,P_pub) avec K la clef symmetrique AES

    

    byte[] Aescipher; // résultat du chiffrement avec AES



    public IBEcipher(byte[] U, byte[] V, byte[] Aescipher) {

        this.U = U;

        this.V = V;

        this.Aescipher = Aescipher;

    }



    public byte[] getAescipher() {

        return Aescipher;

    }



    public byte[] getU() {

        return U;

    }



    public byte[] getV() {

        return V;

    }

    

    

}
