package cryptoExo2;

import it.unisa.dia.gas.jpbc.Element;

public class elgamal {
	private Element u;
	private Element v;
	private byte[] AEScyphertext;
	
	public elgamal(Element u, Element v, byte[] AEScyphertext) {
		this.u = u;
		this.v = v;
		this.AEScyphertext = AEScyphertext;
	}

	public Element getU() {
		return u;
	}


	public Element getV() {
		return v;
	}

	public byte[] getAEScyphertext() {
		return AEScyphertext;
	}

}
