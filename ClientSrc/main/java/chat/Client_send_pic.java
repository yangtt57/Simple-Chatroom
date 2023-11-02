package src.main.java.chat;
import src.main.java.window.Main;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
public class Client_send_pic implements Runnable{
    private Socket socket;
    private SecretKey sessionKey;
    private String plainPic;
    private String encryptedPic;

    public Client_send_pic(Socket socket,SecretKey sessionKey,String filePath) throws IOException {
        this.socket = socket;
        this.sessionKey = sessionKey;
        this.plainPic = filePath;
    }
    // 读取图片数据，返回byte数组
    private byte[] readFileData(String filePath) throws IOException {
        File file=new File(filePath);
        byte[] bytes=new byte[(int)file.length()];
        FileInputStream fileInputStream=new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        return bytes;
    }
    @Override
    public void run() {
        try {
            this.encryptedPic= AES.encrypt(plainPic,sessionKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.println("PIC_SEND "+encryptedPic);
        printWriter.flush();
        System.out.println("Picture send : "+encryptedPic);
    }
}
