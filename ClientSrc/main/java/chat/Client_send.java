package src.main.java.chat;

import src.main.java.window.Main;


import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

//客户端发送数据，需补充关键代码
public class Client_send implements Runnable {
    private Socket socket;
    private SecretKey sessionKey;
    private String plainText;
    private String encryptedData;


    public Client_send(Socket socket, SecretKey sessionKey, String plainText) {
        this.socket = socket;
        this.sessionKey = sessionKey;
        this.plainText = plainText;
    }

    @Override
    //客户端数据发送代码，结合socket、AES算法编程
    public void run() {
    	 /*
                         * 流程如下：
         * 1.使用AES算法加密明文数据得到密文
         * 2.socket发送密文数据与密文类型到服务端
         * 3.调用Main类中方法刷新前端页面显示
         * 
         */
        try {
            this.encryptedData=AES.encrypt(plainText,sessionKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter;
        try {

            printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.println("MSG_SEND "+encryptedData);
        printWriter.flush();
        Main.refresh_send(encryptedData);
        System.out.println("Data send: "+encryptedData);
    }
}
