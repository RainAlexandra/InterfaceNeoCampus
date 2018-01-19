package main_server.dataBase;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * class of functions the generate a final salt and hash value
 * @author RainAlex
 */
public class SaltGen {
	private static final int SIZE = 16;

	/**
	 * Generates a random salt value
	 * @return the salt randomly generated
	 */
	public String saltValueGen(){
		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[SIZE]; // salt len = size * 2
		random.nextBytes(saltBytes);
		String salt = "";
		for (byte b : saltBytes){
			salt += (String.format("%02x", b));
		}
		return salt;
	}

	/**
	 * hashes the concatenation of pwd and salt
	 * @return the hashVaule of the concatenation
	 */
	public String hashValueGen(String pwd, String salt){
		String md5 = "";
		String pwdAndSalt = pwd + salt;
		try {
			// creates MessageDigest object for md5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			// update input string in message digest
			digest.update(pwdAndSalt.getBytes(), 0, pwdAndSalt.length());

			// convert message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(SIZE);
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return md5;
	}

	/**
	 * @param pwd
	 * @param salt
	 * @param hashValue
	 * @return true if the hashed value of pwd + salt equals hashValue
	 */
	public boolean verifPwd(String pwd, String salt, String hashValue){
		String hashToVerify = hashValueGen(pwd, salt);
		return (hashValue.compareTo(hashToVerify) == 0);
	}
}