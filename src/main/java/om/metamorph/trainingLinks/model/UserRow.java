package om.metamorph.trainingLinks.model;

/**
 * Created by OJP on 21/05/2017.
 */
public class UserRow extends User{
    private String passwordHash;
    private byte[] passwordSalt;

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
}
