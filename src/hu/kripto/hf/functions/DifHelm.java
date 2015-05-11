package hu.kripto.hf.functions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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

    private static HashMap<Integer, BigInteger> modulos = new HashMap<Integer, BigInteger>();
    
    static{
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
//				 System.out.println("modulus: "+modulus);
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
        mod = modulos.get(modulusSize);
//        System.out.println(modulusSize.toString());
//        System.out.println(mod.toString());
	}
	
	public BigInteger createInterKey() {
//		System.out.println(mod.toString());
        priv = new BigInteger(mod.bitCount(),new Random());
        // System.out.println(priv);
        return pub = DifHelm.modPow(gen, priv, mod);
	}

	public BigInteger createEncryptionKey(BigInteger interKey){
		BigInteger iKey = DifHelm.modPow(interKey,priv,mod);
		// SHA-1 hash
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1 = md.digest(iKey.toByteArray());
			key = new BigInteger(Arrays.copyOf(sha1, 16)); // 16 byte = 128 bit-re vágás
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			key = iKey;
		}
		return key;
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
		  BigInteger result = BigInteger.ONE;
		  while (exponent.signum() > 0) {
		    if (exponent.testBit(0)) {
		    	result = result.multiply(base).mod(modulus);
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
            case DH_KEY:
            		return key;
            default:
                    return new BigInteger("0");
        }
}
}
