package hu.kripto.hf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class DifHelm {

	private BigInteger gen;
	private BigInteger mod;
	private BigInteger priv;
	private BigInteger pub;
	private BigInteger key;
	
    public static final int DH_MOD  = 1;
    public static final int DH_GEN  = 2;
    public static final int DH_PRIV = 3;
    public static final int DH_PUB  = 4;
    public static final int DH_KEY  = 5;

    private HashMap<Integer, BigInteger> modulos = new HashMap<Integer, BigInteger>();
    
    {
		try {
			BufferedReader br = new BufferedReader(new FileReader("DHmods.txt"));
			while (br.ready()) {
				String read = br.readLine();
				int bitLen = Integer.parseInt(read.substring(0, read.length()-5));
				StringBuffer sb = new StringBuffer();
				if (read.matches("(\\d)*( )(bit:)")) {
					do {
						read = br.readLine();
						sb.append(read);
					} while (br.ready() && read.length() > 0); 
				}
				BigInteger modulus = new BigInteger(sb.toString().replace(" ",""),16);
				modulos.put(bitLen, modulus);
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public DifHelm(BigInteger generator, Integer modulusSize) {
		gen = generator;
		System.out.println(modulusSize.toString());
        mod = modulos.get(modulusSize);
	}
	
	public BigInteger createInterKey() {
        priv = new BigInteger(mod.bitCount(),new Random());
        return pub = DifHelm.modPow(gen, priv, mod);
	}

	public BigInteger createEncryptionKey(BigInteger interKey){
		return key = DifHelm.modPow(interKey,priv,mod);
	}
	/**
	 * @param base
	 * @param exponent
	 * @param modulus 
	 * @return
	 */
	public static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger modulus) {
		if (modulus.signum() < 1 ) {
			throw new ArithmeticException("non-positive modulo");
		}
//		System.out.println(base + " " + exponent + " " + modulus);
		  BigInteger result = BigInteger.ONE;
		  while (exponent.signum() > 0) {
//			  System.out.println("base: " + base + ", result: " + result + ", exponent: " + exponent + ", testbit: " + exponent.testBit(0));
		    if (exponent.testBit(0)) {
		    	result = result.multiply(base).mod(modulus);
//		    	System.out.println("result: " + result);
		    }
		    base = base.multiply(base).mod(modulus);
		    exponent = exponent.shiftRight(1);
		  }
		  return result;
	}
	
	public BigInteger getValue(int flags) {
        switch (flags) {
            case DH_MOD:
                    return mod;
            case DH_GEN:
                    return gen;
            case DH_PRIV:
                    return priv;
            case DH_PUB:
                    return pub;
            default:
                    return new BigInteger("0");
        }
}
}
