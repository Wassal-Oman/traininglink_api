package om.metamorph.trainingLinks;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import sun.security.util.Password;

public class AuthenticationManager {

    // The higher the number of ITERATIONS the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int ITERATIONS = 20*1000;
    private static final int SALT_LENGTH = 64;
    private static final int DESIRED_KEY_LENGTH = 256;

    private byte[] salt;
    private String hash;

    public AuthenticationManager(String password) throws Exception {
        salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(SALT_LENGTH);
        hash = getSaltedHash(password,salt);
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }

    /** Computes a salted PBKDF2 hash of given plaintext password
     suitable for storing in a database.
     Empty passwords are not supported. */
    private String getSaltedHash(String password, byte[] salt) throws Exception {
        // store the salt with the password
        return hash(password, Base64.decodeBase64(salt));
    }

    /** Checks whether given plaintext password corresponds
     to a stored salted hash of the password. */
    public static boolean check(String password, String hash, byte[] salt){
        String hashOfInput = hash(password, Base64.decodeBase64(salt));
        return hashOfInput.equals(hash);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private static String hash(String password, byte[] salt) {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = null;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey key = null;
        try {
            key = f.generateSecret(new PBEKeySpec(
                    password.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LENGTH)
            );
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(key.getEncoded());
    }

    public static String generateUserToken() {
//        String token = null;
//        try {
//            Random random = new Random(Calendar.getInstance().getTimeInMillis());
//
//            //generate a random number
//            String randomNum = Integer.toString(random.nextInt());
//
//            //get its digest
//            MessageDigest sha = MessageDigest.getInstance("SHA-256");
//            byte[] result =  sha.digest(randomNum.getBytes());
//            token = new String(result);
//        }
//        catch (NoSuchAlgorithmException ex) {
//            System.err.println(ex);
//        }
        String token = UUID.randomUUID().toString();
        return token;
    }
}