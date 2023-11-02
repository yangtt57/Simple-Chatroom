package chat;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.sql.SQLException;
import java.util.regex.*;
//服务端密钥交换代码，需补充关键代码
public class chat_server {
	private static Socket socket;
	private static PublicKey public_key;
    private static PrivateKey private_key;
    public chat_server(int serverPort) throws IOException, SQLException, ClassNotFoundException {
    	
        @SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(serverPort);
        socket = serverSocket.accept();
        System.out.println("connected to client");
        //连接数据库
        SQLUtil.DBConnect();
    }

    public Socket getSocket(){
        return socket;
    }

    //服务端密钥交换代码，结合socket、RSA算法、AES算法编程
    public static SecretKey ENV_server() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        /*
         * 流程如下：
         * 1.生成RSA密钥对作为服务端公私钥
         * 2.发送服务端公钥
         * 3.接收客户端发来的加密后会话密钥
         * 4.接收加密后的AES密钥字节数组的长度
         * 5.接收加密后的AES密钥字节数组
         * 6.使用私钥解密AES密钥
         * 7.根据解密后的AES密钥字节数组生成SecretKey对象并返回
         */

        RSA server_rsa = new RSA();
        PublicKey publicKey = server_rsa.getPublicKey();
        PrivateKey privateKey = server_rsa.getPrivateKey();
        String send_key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("Key Generate!");
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Public Key:\n"+Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        printWriter.println(send_key);
        System.out.println("Private Key:\n" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));

        String rev_data = bufferReader.readLine();
        byte[] byte_key = Base64.getDecoder().decode(rev_data);
        System.out.println("Get Encrypted Key:\n" + rev_data);

        byte[] AES_key_bytes = RSA.decrypt(byte_key, privateKey);

        SecretKey secretKey = new SecretKeySpec(AES_key_bytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        System.out.println("Get Decrypted Key:\n" + Base64.getEncoder().encodeToString(AES_key_bytes));

        return secretKey;
    }
}

