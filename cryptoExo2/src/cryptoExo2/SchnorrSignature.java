package cryptoExo2;

import it.unisa.dia.gas.jpbc.Element;

public class SchnorrSignature {
    private Element e,s;
    
    public SchnorrSignature(Element e, Element s) {
    	this.e = e;
	    this.s = s;
    }
    public Element getS() {
    	return s;
    }

    public Element getE() {
    	return e;
	}

    @Override
    public String toString() {
    	return "s"+this.s+"\n e"+this.e; 
	}
	    
	    
	    
}
	

