package chat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.crypto.spec.SecretKeySpec;
import java.sql.*;

//数据库操作，不需补充
public class SQLUtil {
    private static Connection connection;
    private static PreparedStatement insertStatement;
    private static final String tableName = "messages";
    // 数据库密钥
    private static byte[] aesKeyBytes = "0123456789abcdef".getBytes(); // 使用相同的密钥加解密
    private static SecretKeySpec DBKey = new SecretKeySpec(aesKeyBytes, "AES");

    public static void DBConnect() throws ClassNotFoundException, SQLException {
        // 创建数据库连接
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:src/main/resources/chatroom_server.db";
        connection = DriverManager.getConnection(url);
        // 创建表格
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, cipher TEXT)";
        connection.createStatement().executeUpdate(createTableQuery);
        // 准备插入语句
        String insertQuery = "INSERT INTO " + tableName + " (cipher) VALUES (?)";
        insertStatement = connection.prepareStatement(insertQuery);
    }

    public static void insertEncryptMessages(String decryptedData) throws Exception {
        //加密消息
        String dataToDB = chat.AES.encrypt(decryptedData, DBKey);
        // 将密文存储到数据库中
        insertStatement.setString(1, dataToDB);
        insertStatement.executeUpdate();
    }

    public static void closeDB() throws SQLException {
        connection.close();
    }

    public static ObservableList<String> getMessagesFromDB() {
        ObservableList<String> messages = FXCollections.observableArrayList();
        try {
            String url = "jdbc:sqlite:src/main/resources/chatroom_server.db";
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT cipher FROM messages");
            while (resultSet.next()) {
                String encryptedData = resultSet.getString("cipher");
                String decryptedData = AES.decrypt(encryptedData, DBKey);
                String message = decryptedData;
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
}