//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package src.main.java.chat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

//AES算法实现，需补充关键代码，推荐使用javax.crypto.Cipher
public class AES {
    private static final String AES_ALGORITHM = "AES";

    public AES() {
    }

    //加密算法，传入明文和密钥，返回密文
    public static String encrypt(String plaintext, SecretKey key) throws Exception {
    	/*
    	补充
    	*/
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes(Charset.forName("UTF-8"))));
    }

    //解密算法，传入密文和密钥，返回明文
    public static String decrypt(String ciphertext, SecretKey key) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(ciphertext);
        Cipher cipher=Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptBytes = cipher.doFinal(encrypted);

        return new String(decryptBytes);
    }
}
