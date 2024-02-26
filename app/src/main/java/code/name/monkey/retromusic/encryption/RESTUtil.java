package code.name.monkey.retromusic.encryption;

public class RESTUtil {

    private static final String BASE_URL = "http://10.0.2.2:3000/api/";
    private static Secret secret;

    public static void setSecret(Secret secret) {
        RESTUtil.secret = secret;
    }

    public static void securePOST(String body) {
//        BodyRequest bodyRequest = new BodyRequest(
//                "key", RSAUtil.encrypt(secret.getAesKey(), secret.getPublicKey()),
//                "iv", RSAUtil.encrypt(secret.getAesIV(), secret.getPublicKey()),
//                "body", AESUtil.encrypt(new BodyRequest(
//                "username", "minhk11642002@gmail.com",
//                "password", "m01675784487").toString(), secret.getAesKey(), secret.getAesIV())
//        );
//        Log.e("Minh", "bodyLogin " + bodyRequest.toString());
    }

    public static String getPlainBody(String body) {
        return AESUtil.decrypt(body, secret.getAesKey(), secret.getAesIV());
    }

}
