package hu.kripto.hf;

import java.math.BigInteger;
import java.util.Random;

public class DifHelm {

	private BigInteger gen;
	private BigInteger mod;
	private BigInteger priv;
	private BigInteger pub;
	
    private static final int DH_MOD  = 1;
    private static final int DH_GEN  = 2;
    private static final int DH_PRIV = 3;
    private static final int DH_PUB  = 4;
    private static final int DH_KEY  = 5;

	public DifHelm(BigInteger generator, BigInteger modulus) {
		gen = generator;
        mod = modulus;
	}
	
	
	public BigInteger createInterKey() {
        priv = new BigInteger(mod.bitCount(),new Random());
        return pub = DifHelm.modPow(gen,priv,mod);
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
