package src.main.java.chat;

import src.main.java.window.Main;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

//客户端发送文件，需补充关键代码
public class Client_send_file implements Runnable {
    private Socket socket;
    private SecretKey sessionKey;
    private String encryptedFile;
    private String plainTextFile;



    public Client_send_file(Socket socket, SecretKey sessionKey, String filePath) throws IOException {
        this.socket = socket;
        this.sessionKey = sessionKey;
        this.plainTextFile = new String(readFileData(filePath));
    }


    //读取文件数据，返回byte数组
    private byte[] readFileData(String filePath) throws IOException {
    	File file=new File(filePath);
        byte[] bytes=new byte[(int)file.length()];
        FileInputStream fileInputStream=new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();;
        return bytes;
    }

    @Override
    //客户端文件发送代码，结合socket、AES算法编程
    public void run() {
        /*
                         * 流程如下：
         * 1.使用AES算法加密明文文件数据得到密文
         * 2.socket发送密文文件数据与密文类型到服务端
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
