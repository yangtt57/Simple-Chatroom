package src.main.java.chat;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

//客户端密钥交换代码，需补充关键代码
public class chat_client {
    private static Socket socket;

    public chat_client(String serverIP, int serverPort) throws IOException, SQLException, ClassNotFoundException {
        socket = new Socket(serverIP, serverPort);
        System.out.println("connected to server");
        //连接数据库
        SQLUtil.DBConnect();
    }

    public Socket getSocket(){
        return socket;
    }
    

    //客户端密钥交换代码，结合socket、RSA算法、AES算法编程
    public static SecretKey ENV_client() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        /*
         * 流程如下：
         * 1.接收服务端公钥
         * 2.生成128位AES对称密钥作为会话密钥
         * 3.使用服务端公钥加密会话密钥
         * 4.发送加密后的会话密钥
         * 5.返回会话密钥
         * 
         */
        // printWriter向服务器发送消息，bufferReader接收服务器的消息
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String recv_key= bufferReader.readLine();
        byte[] byte_key=Base64.getDecoder().decode(recv_key);
        System.out.println("Public key: "+recv_key);
        // 生成128位AES对称密钥作为会话密钥
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey=keyGenerator.generateKey();

        // 使用服务端公钥加密会话密钥
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey=keyFactory.generatePublic(new X509EncodedKeySpec(byte_key));
        byte[] aes_encrypted_byte =RSA.encrypt(secretKey.getEncoded(),publicKey);
        String aes_encrypted=new String(aes_encrypted_byte);

        System.out.println("Encrypted AES key: "+ Base64.getEncoder().encodeToString(aes_encrypted_byte));
        String send_key = Base64.getEncoder().encodeToString(aes_encrypted_byte);
        printWriter.println(send_key);
        System.out.println("Send AES Encrypted key!");
        return secretKey;
    }
}



