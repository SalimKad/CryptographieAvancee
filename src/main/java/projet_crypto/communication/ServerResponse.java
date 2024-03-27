package main.java.projet_crypto.communication;


import projet_crypto.KeyPair;
import projet_crypto.SettingParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String pk;
    private Element sk;
    private Element P;
    private Element P_pub;
    
    private String skS;
    private String PS;
    private String P_pubS;
   


	public ServerResponse(Element generator, Element ppub, String pk, Element sk2) {
		// TODO Auto-generated constructor stub
		this.pk = pk;
        this.sk = sk2;
        this.P = generator;
        this.P_pub = ppub;
	}


	public ServerResponse(String pk, Element sk2) {
		// TODO Auto-generated constructor stub
		this.pk = pk;
        this.sk = sk2;
        
	}
	
    public String getPk() {
		return pk;
	}



	public void setPk(String pk) {
		this.pk = pk;
	}



	public Element getSk() {
		return sk;
	}



	public void setSk(Element sk) {
		this.sk = sk;
	}



	public Element getP() {
		return P;
	}



	public void setP(Element p) {
		P = p;
	}



	public Element getP_pub() {
		return P_pub;
	}



	public void setP_pub(Element p_pub) {
		P_pub = p_pub;
	}



    public ServerResponse() {
		// TODO Auto-generated constructor stub
	}





}