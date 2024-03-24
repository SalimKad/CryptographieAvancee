package projet_crypto;

import java.io.Serializable;

import it.unisa.dia.gas.jpbc.Element;

public class KeyPair implements Serializable {
   
  public  String pk; //identité de l'utilisateur
  private  Element sk; // clef privée de l'utilisateur

    public KeyPair(String pk, Element sk) {
        this.pk = pk;
        this.sk = sk;
    }

    public String getPk() {
        return pk;
    }

    public Element getSk() {
        return sk;
    }
    
    
}
