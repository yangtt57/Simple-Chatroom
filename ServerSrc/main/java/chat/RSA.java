package chat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;

//RSA算法实现，需补充关键代码，推荐使用javax.crypto.Cipher
public class RSA {
    private final KeyPair keyPair;

    // 生成RSA密钥对
    public RSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    //返回公钥
    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }

    //返回私钥
    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }
    
    //加密算法，传入明文和公钥，返回密文
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        return cipher.doFinal(data);
    }

    //解密算法，传入密文和私钥，返回明文
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(data);
    }
}
