package window;

import chat.Server_listen;
import chat.Server_send;
import chat.Server_send_file;
import chat.chat_server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//页面代码，不需补充
public class Main extends Application{
    public static void main(String[] args){
        Application.launch(args);
    }

    private static VBox chatVBox;
    Socket socket;
    chat_server server;
    String Cipher;
    static TextArea textArea_defaultencrypted;
    static TextArea textArea_defaultReceive;
    static TextArea textArea_encrypted;
    static TextArea textArea_Receive;
    Server_listen serverListen;
    SecretKey sessionKey;

    String picPath = System.getProperty("user.dir");

    @Override
    public void start(Stage stage) throws Exception {

        /**
         * "Ip connect page"
         * Hbox hbox_ip
         * Vbox vbox_ip
         * BorderPane pane
         */

        HBox hBox_port = new HBox();
        hBox_port.setSpacing(19);

        Label label_port = new Label("Port:");

        TextField textField_port = new TextField();
        textField_port.setPrefSize(100,10);
        textField_port.setEditable(true);
        textField_port.setPromptText("Please input port"); // 设置单行输入框的提示语
        textField_port.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

        hBox_port.getChildren().addAll(label_port,textField_port);
        hBox_port.setAlignment(Pos.CENTER);

        VBox vbox_port = new VBox();
        Button connection_button = new Button("connect");   //Ip连接按钮
        BorderPane pane = new BorderPane();

        vbox_port.setSpacing(20);
        vbox_port.getChildren().addAll(hBox_port,connection_button);
        vbox_port.setAlignment(Pos.CENTER);
        pane.setCenter(vbox_port); // 把水平箱子放到边界窗格的中央



        Scene scene = new Scene(pane,400,600);

        /**
         * "chat page"
         */

        BorderPane pane_chat = new BorderPane();

        chatVBox = new VBox();
        chatVBox.setSpacing(10);
        //chatVBox.setPadding(new Insets(10));
        chatVBox.setPrefSize(375,400);

        ScrollPane scrollPane_chat = new ScrollPane(chatVBox);
        scrollPane_chat.setPrefSize(390,400);

        pane_chat.setTop(scrollPane_chat);

        // 创建图标按钮1（文本框输入）
        Button button_data = new Button();
        ImageView imageView_data = new ImageView(new Image("file:src\\main\\resources\\c.png"));
        imageView_data.setFitWidth(20);
        imageView_data.setFitHeight(20);
        button_data.setGraphic(imageView_data);

        // 创建图标按钮2（文件输入）
        Button button_file = new Button();
        ImageView imageView_file = new ImageView(new Image("file:src\\main\\resources\\wj.png"));
        imageView_file.setFitWidth(20);
        imageView_file.setFitHeight(20);
        button_file.setGraphic(imageView_file);

        // 创建图标按钮3（收藏夹）
        /*
         * Button button_mark = new Button(); ImageView imageView_mark = new
         * ImageView(new Image("file:src\\main\\resources\\star.png"));
         * imageView_mark.setFitWidth(20); imageView_mark.setFitHeight(20);
         * button_mark.setGraphic(imageView_mark);
         */

        HBox hBox_menu = new HBox();
        hBox_menu.getChildren().addAll(button_data,button_file);
        //,button_mark);
        BorderPane.setMargin(hBox_menu,new Insets(0,0,150,0));

        HBox hBox_defaultsend = new HBox();
        hBox_defaultsend.setSpacing(20);

        TextField textField_defaultinput = new TextField();
        textField_defaultinput.setPrefSize(310,10);
        textField_defaultinput.setEditable(true);
        textField_defaultinput.setPromptText("Please input data"); // 设置单行输入框的提示语
        textField_defaultinput.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

        Button button_defaultsend = new Button("send");

        hBox_defaultsend.getChildren().addAll(textField_defaultinput,button_defaultsend);
        hBox_defaultsend.setAlignment(Pos.CENTER);

        HBox hBox_defaultencrypted = new HBox();
        hBox_defaultencrypted.setSpacing(20);

        textArea_defaultencrypted = new TextArea();
        textArea_defaultencrypted.setPrefSize(296,30);
        textArea_defaultencrypted.setEditable(false);
        textArea_defaultencrypted.setWrapText(true);
        textArea_defaultencrypted.setPromptText("Here is the encrypted data");

        Label label_defaultencrypted = new Label("encrypted");

        hBox_defaultencrypted.getChildren().addAll(textArea_defaultencrypted,label_defaultencrypted);
        hBox_defaultencrypted.setAlignment(Pos.CENTER);

        HBox hBox_defaultReceive = new HBox();
        hBox_defaultReceive.setSpacing(20);

        textArea_defaultReceive = new TextArea();
        textArea_defaultReceive.setPrefSize(298,30);
        textArea_defaultReceive.setEditable(false);
        textArea_defaultReceive.setWrapText(true);
        textArea_defaultReceive.setPromptText("Here is the encrypted data");

        Label label_defaultReceive = new Label("Received ");

        hBox_defaultReceive.getChildren().addAll(textArea_defaultReceive,label_defaultReceive);
        hBox_defaultReceive.setAlignment(Pos.CENTER);

        VBox vBox_defaultdata = new VBox();
        vBox_defaultdata.setSpacing(15);
        vBox_defaultdata.getChildren().addAll(hBox_menu,hBox_defaultsend,hBox_defaultencrypted,hBox_defaultReceive);

        pane_chat.setBottom(vBox_defaultdata);
        BorderPane.setMargin(vBox_defaultdata,new Insets(0,0,17,0));

        Scene scene_chat = new Scene(pane_chat,400,600);

        connection_button.setOnAction(event -> {
            int port = Integer.valueOf(textField_port.getText()).intValue();
            try {
                server = new chat_server(port);
            } catch (IOException | SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            socket = server.getSocket();
            try {
                sessionKey = chat_server.ENV_server();
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }

            serverListen = new Server_listen(socket, sessionKey);
            new Thread(serverListen).start();

            stage.setScene(scene_chat);
        });

        button_defaultsend.setOnAction(event -> {
            String message = textField_defaultinput.getText().trim();
            if (!message.isEmpty()) {
                addMessageToChat_Right(message);
                textField_defaultinput.clear();
            }
            Server_send serverSend = new Server_send(socket, sessionKey, message);
            new Thread(serverSend).start();
        });

        button_data.setOnAction(event -> {
            HBox hBox_send = new HBox();
            hBox_send.setSpacing(20);

            TextField textField_input = new TextField();
            textField_input.setPrefSize(310,10);
            textField_input.setEditable(true);
            textField_input.setPromptText("Please input data"); // 设置单行输入框的提示语
            textField_input.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

            Button button_send = new Button("send");

            hBox_send.getChildren().addAll(textField_input,button_send);
            hBox_send.setAlignment(Pos.CENTER);

            HBox hBox_encrypted = new HBox();
            hBox_encrypted.setSpacing(20);

            textArea_encrypted = new TextArea();
            textArea_encrypted.setPrefSize(296,30);
            textArea_encrypted.setEditable(false);
            textArea_encrypted.setWrapText(true);
            textArea_encrypted.setPromptText("Here is the encrypted data");

            Label label_encrypted = new Label("encrypted");

            hBox_encrypted.getChildren().addAll(textArea_encrypted,label_encrypted);
            hBox_encrypted.setAlignment(Pos.CENTER);

            HBox hBox_Receive = new HBox();
            hBox_Receive.setSpacing(20);

            textArea_Receive = new TextArea();
            textArea_Receive.setPrefSize(298,30);
            textArea_Receive.setEditable(false);
            textArea_Receive.setWrapText(true);
            textArea_Receive.setPromptText("Here is the encrypted data");

            Label label_Receive = new Label("Received ");

            hBox_Receive.getChildren().addAll(textArea_Receive,label_Receive);
            hBox_Receive.setAlignment(Pos.CENTER);

            VBox vBox_data = new VBox();
            vBox_data.setSpacing(15);
            vBox_data.getChildren().addAll(hBox_menu,hBox_send,hBox_encrypted,hBox_Receive);

            pane_chat.setBottom(vBox_data);
            BorderPane.setMargin(vBox_data,new Insets(0,0,17,0));
            textArea_Receive.setText(Cipher);

            button_send.setOnAction(event1 -> {
                String message = textField_input.getText().trim();
                if (!message.isEmpty()) {
                    addMessageToChat_Right(message);
                    textField_input.clear();
                }
                Server_send serverSend = new Server_send(socket, sessionKey, message);
                new Thread(serverSend).start();
            });

            stage.setScene(scene_chat);
        });

        button_file.setOnAction(event -> {
            VBox vBox_file = new VBox();
            vBox_file.setSpacing(20);

            HBox hBox_fileChoose = new HBox();
            hBox_fileChoose.setSpacing(10);

            TextField textField_fileChoose = new TextField();
            textField_fileChoose.setPrefSize(250,10);
            textField_fileChoose.setEditable(true);
            textField_fileChoose.setPromptText("File path"); // 设置单行输入框的提示语
            textField_fileChoose.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

            Button button_filechoose = new Button("choose");
            Button button_fileSend = new Button("send");

            button_filechoose.setOnAction(event1 -> {
                Stage stage_fileChoose = new Stage();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FileType","*.txt"));
                File selectedFile = fileChooser.showOpenDialog(stage_fileChoose);

                if(selectedFile != null){
                    textField_fileChoose.setText(selectedFile.getPath());
                }

                button_fileSend.setOnAction(event2 -> {
                    if (selectedFile != null) {
                        addFileToChat_right(selectedFile);
                    }
                    Server_send_file serverSend = new Server_send_file(socket, sessionKey,selectedFile.getPath());
                    new Thread(serverSend).start();
                });

            });


            hBox_fileChoose.getChildren().addAll(textField_fileChoose,button_filechoose,button_fileSend);
            hBox_fileChoose.setAlignment(Pos.CENTER);

            vBox_file.setSpacing(10);
            vBox_file.getChildren().addAll(hBox_menu,hBox_fileChoose);

            pane_chat.setBottom(vBox_file);
            BorderPane.setMargin(vBox_file,new Insets(0,0,126,0));

            stage.setScene(scene_chat);


        });

//        button_mark.setOnAction(event1 -> {
//            Stage stage_mark = new Stage();
//            stage_mark.setTitle("Bookmarks");
//            ListView<String> messagesListView = new ListView<>();
//            ObservableList<String> messages = SQLUtil.getMessagesFromDB();
//            messagesListView.setItems(messages);
//            VBox star = new VBox(messagesListView);
//            ScrollPane scrollPane_mark = new ScrollPane(star);
//            Scene scene_mark = new Scene(scrollPane_mark,250,400);
//            //scrollPane_mark.setPrefSize(390,400);
//            stage_mark.setScene(scene_mark);
//            stage_mark.show();
//        });


        stage.setScene(scene);

        stage.setTitle("Server");
        stage.getIcons().add(new Image("file:src\\main\\resources\\R-C.png"));
        stage.setResizable(false);  //窗口大小不变
        stage.show();

    }

//    public static void refresh(String cipher){
//        Platform.runLater(() -> textArea_defaultencrypted.setText(cipher));
//        Platform.runLater(() -> textArea_encrypted.setText(cipher));
//    }

    public static void refresh_send(String cipher){
        Platform.runLater(() -> textArea_defaultencrypted.setText(cipher));
        Platform.runLater(() -> textArea_encrypted.setText(cipher));
    }

    public static void refresh_listen(String cipher,String plain){
        Platform.runLater(() -> textArea_defaultReceive.setText(cipher));
        Platform.runLater(() -> textArea_Receive.setText(cipher));
        Platform.runLater(() -> addMessageToChat(plain));
    }

    public static void refresh_listen_file(){
        Platform.runLater(() -> addFileToChat());
    }
    public static void refresh_listen_emoji(String emojiNum) {
        Platform.runLater(() -> addEmojiToChat(emojiNum));
    }
    public static void refresh_listen_pic(String path) {
        Platform.runLater(() -> addPicToChat(path));
    }


    private static void addMessageToChat(String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";
        String chatMessage_message = message;
        Label messageLabel = new Label(chatMessage);
        Label messageLabel_message = new Label(chatMessage_message);

        VBox vBox_chatMessage = new VBox();
        vBox_chatMessage.getChildren().addAll(messageLabel,messageLabel_message);
        vBox_chatMessage.setAlignment(Pos.CENTER_LEFT);

        HBox hBox_left = new HBox(vBox_chatMessage);
        hBox_left.setAlignment(Pos.BASELINE_LEFT);
        //chatVBox.setAlignment(Pos.BASELINE_LEFT);

        chatVBox.getChildren().add(hBox_left);
    }

    private static void addMessageToChat_Right(String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";
        String chatMessage_message = message;
        Label messageLabel = new Label(chatMessage);
        Label messageLabel_message = new Label(chatMessage_message);

        VBox vBox_chatMessage = new VBox();
        vBox_chatMessage.getChildren().addAll(messageLabel,messageLabel_message);
        vBox_chatMessage.setAlignment(Pos.CENTER_RIGHT);

        HBox hBox_right = new HBox(vBox_chatMessage);
        hBox_right.setAlignment(Pos.BASELINE_RIGHT);
        //chatVBox.setAlignment(Pos.BASELINE_RIGHT);111

        chatVBox.getChildren().add(hBox_right);
    }

    private static void addFileToChat() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        ImageView fileIcon = new ImageView(new Image("file:src\\main\\resources\\wj.png"));
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);

        // 显示文件图
        //Label fileLabel = new Label(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon);

        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_LEFT);

        HBox chatMessageBox_left = new HBox(chatMessageBox);
        chatMessageBox_left.setAlignment(Pos.BASELINE_LEFT);

        chatVBox.getChildren().add(chatMessageBox_left);
    }

    private static void addFileToChat_right(File file) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        ImageView fileIcon = new ImageView(new Image("file:src\\main\\resources\\wj.png"));
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);

        // 显示文件图
        Label fileLabel = new Label(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon,fileLabel);

        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_RIGHT);

        HBox chatMessageBox_right = new HBox(chatMessageBox);
        chatMessageBox_right.setAlignment(Pos.BASELINE_RIGHT);

        chatVBox.getChildren().add(chatMessageBox_right);
    }

    private static void addEmojiToChat(String emojiNum) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        ImageView fileIcon = new ImageView(new Image("file:src\\main\\resources\\"+emojiNum+".png"));
        fileIcon.setFitWidth(50);
        fileIcon.setFitHeight(50);

        // 显示文件图
        //Label fileLabel = new Label(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon);
        fileBox.setAlignment(Pos.BASELINE_LEFT);
        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_LEFT);

        HBox chatMessageBox_left = new HBox(chatMessageBox);
        chatMessageBox_left.setAlignment(Pos.BASELINE_LEFT);

        chatVBox.getChildren().add(chatMessageBox_left);
    }
    private static void addPicToChat(String path) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        System.out.println("file:"+path.replaceAll("\\\\","/"));
        ImageView fileIcon = new ImageView(new Image("file:"+path.replaceAll("\\\\","/")));
        fileIcon.setFitWidth(150);
        fileIcon.setFitHeight(150);

        // 显示文件图
        //Label fileLabel = new Label(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon);
        fileBox.setAlignment(Pos.BASELINE_LEFT);
        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_LEFT);

        HBox chatMessageBox_left = new HBox(chatMessageBox);
        chatMessageBox_left.setAlignment(Pos.BASELINE_LEFT);

        chatVBox.getChildren().add(chatMessageBox_left);
    }
}
