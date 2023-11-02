package src.main.java.chat;

import src.main.java.window.Main;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
public class Client_send_emoji implements Runnable {
    private Socket socket;
    private SecretKey sessionKey;
    private String plainEmojiNum;
    private String encryptedNum;
    public Client_send_emoji(Socket socket,SecretKey sessionKey, String plainEmojiNum) {
        this.socket=socket;
        this.sessionKey=sessionKey;
        this.plainEmojiNum=plainEmojiNum;
    }
    @Override
    public void run() {
        try {
            this.encryptedNum = AES.encrypt(plainEmojiNum,sessionKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter;
        try{
            printWriter= new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.println("EMOJI_SEND "+ this.encryptedNum);
        printWriter.flush();
//        Main.refresh_send_emoji(plainEmojiNum);
        System.out.println("Emoji Number: "+encryptedNum);
    }
}
