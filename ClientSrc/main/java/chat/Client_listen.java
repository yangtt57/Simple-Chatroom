package src.main.java.chat;

import src.main.java.window.Main;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//客户端监听，需补充关键代码
public class Client_listen implements Runnable {
    private Socket socket;
    private SecretKey sessionKey;
    private String decryptedData;
    private static String filePath = "./out";
    private static long timestamp = System.currentTimeMillis();
    private static String fileName = String.valueOf(timestamp);
    private String message;

    public Client_listen(Socket socket, SecretKey sessionKey) {
        this.socket = socket;
        this.sessionKey = sessionKey;
    }

    @Override
    //客户端数据与文件接收代码，结合socket、AES算法编程
    public void run() {
        /*
     	* 流程如下：
     	* 1.接收服务端传过来的数据
     	* 2.使用AES算法对数据内容解密
     	* 3.判断是否是文件数据
     	* 4.如果是文件数据，调用filesave方法保存文件并调用Main类中方法刷新前端页面显示
     	* 5.如果是消息数据，调用Main类中方法刷新前端页面显示
     	* 
     	*/
        while(true) {
            BufferedReader bufferReader = null;
            try {
                bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String requestData = null;
            try {
                requestData = bufferReader.readLine();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String regex = "^(\\S+)\\s(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(requestData);
            String data = null , method = null;
            if (matcher.find()) {
                method = matcher.group(1);
                data = matcher.group(2);
            } else {
                System.out.println("Unknown Command!!!");
            }

            try {
                this.decryptedData = AES.decrypt(data , this.sessionKey);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assert method != null;
            if(method.equals("MSG_SEND")) {
                this.message =  method + this.decryptedData;
                System.out.println("Message Receive:\n"+data);
                Main.refresh_listen( data , this.decryptedData);
            }
            else if(method.equals("FILE_SEND")) {
                this.message=data;
                System.out.println("File Receive\n"+data);
                this.fileSave(filePath, fileName);
                Main.refresh_listen_file();
            }

        }

    }

    //文件保存，传入filePath与fileName，返回文件
    public File fileSave(String filePath,String fileName){
        /*
         * 流程如下：
         * 1.定义两个文件名fileName + ".txt"和fileName + "_cipher.txt"
         * 2.用文件路径与文件名new两个文件
         * 3.将decryptedData与message分别写入两个文件
         * 4.返回明文文件
         *
         */
        String file_path1 = fileName + ".txt";
        String file_path2 = fileName + "_cipher.txt";

        File file1 = new File(file_path1);
        File file2 = new File(file_path2);

        try (FileOutputStream fos = new FileOutputStream(file1)) {
            fos.write(this.decryptedData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try(FileOutputStream fos_ = new FileOutputStream(file2)) {
            fos_.write(this.message.getBytes());;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
        System.out.println("File Write Success");
        return file1;
    }
}
