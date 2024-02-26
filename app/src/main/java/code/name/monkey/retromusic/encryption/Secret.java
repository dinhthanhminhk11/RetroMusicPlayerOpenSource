package code.name.monkey.retromusic.encryption;

import java.io.Serializable;
import java.security.PublicKey;

public class Secret implements Serializable {

    private PublicKey publicKey;
    private String aesKey;
    private String aesIV;

    public Secret(PublicKey publicKey, String aesKey, String aesIV) {
        this.publicKey = publicKey;
        this.aesKey = aesKey;
        this.aesIV = aesIV;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getAesKey() {
        return aesKey;
    }

    public String getAesIV() {
        return aesIV;
    }

    @Override
    public String toString() {
        return "Secret{" +
                "publicKey=" + publicKey +
                ", aesKey='" + aesKey + '\'' +
                ", aesIV='" + aesIV + '\'' +
                '}';
    }
}
