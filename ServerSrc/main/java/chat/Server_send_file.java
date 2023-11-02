package chat;

import window.Main;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

//服务端发送文件，需补充关键代码
public class Server_send_file implements Runnable {
    private Socket socket;
    private SecretKey sessionKey;
    private String encryptedFile;
    private String plainTextFile;

    public Server_send_file(Socket socket, SecretKey sessionKey, String filePath) {
        this.socket = socket;
        this.sessionKey = sessionKey;
        this.plainTextFile = new String(readFileData(filePath));
    }


    //以下为补充的内容
    private byte[] readFileData(String filePath) {
        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            long fileSize = file.length();
            byte[] fileData = new byte[(int) fileSize];
            fis.read(fileData);
            this.plainTextFile = fileData.toString();
            return fileData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    //服务端文件发送代码，结合socket、AES算法编程
    public void run() {
        /*
         * 流程如下：
         * 1.使用AES算法加密明文文件数据得到密文
         * 2.socket发送密文文件数据与密文类型到客户端
         *
         */

        try {
            this.encryptedFile=AES.encrypt(plainTextFile,sessionKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter;
        try {
            printWriter=new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.println("FILE_SEND "+encryptedFile);
        printWriter.flush();
        System.out.println("File send : "+encryptedFile);
    }

}
