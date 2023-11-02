package chat;

import window.Main;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

//服务端发送数据，需补充关键代码
public class Server_send implements Runnable {

	private Socket socket;
	SecretKey sessionKey;
	private String plainText;
	private String encryptedData;

	public Server_send(Socket socket, SecretKey sessionKey, String plainText) {
		this.socket = socket;
		this.sessionKey = sessionKey;
		this.plainText = plainText;
	}

	@Override
	//服务端数据发送代码，结合socket、AES算法编程
	public void run() {
		/*
		 * 流程如下：
		 * 1.使用AES算法加密明文数据得到密文
		 * 2.socket发送密文数据与密文类型到客户端
		 * 3.调用Main类中方法刷新前端页面显示
		 *
		 */
		try {
			this.encryptedData = chat.AES.encrypt(plainText, sessionKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter print_writer = null;
		try {
			print_writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Data send:"+this.encryptedData);
		print_writer.println("MSG_SEND "+encryptedData);
		print_writer.flush();
		Main.refresh_send(encryptedData);
	}

}
