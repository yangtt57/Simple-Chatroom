package src.main.java.window;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import src.main.java.chat.*;
import javafx.scene.layout.BorderPane;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//页面代码，不需补充
public class Main extends Application{
    public static void main(String[] args){
        System.out.println("main() ="+Thread.currentThread().getName());
        Application.launch(args);
    }

    //private TextArea chatTextArea;
    private static VBox chatVBox;
    Socket socket;
    String serverCipher;
    SecretKey sessionKey;
    src.main.java.chat.chat_client chat_client;
    Client_listen clientListen;
    static TextArea textArea_defaultReceive;
    static TextArea textArea_defaultencrypted;
    static TextArea textArea_encrypted;
    static TextArea textArea_Receive;

    @Override
    public void start(Stage stage) throws Exception {

        /**
         * "Ip connect page"
         * Hbox hbox_ip
         * Vbox vbox_ip
         * BorderPane pane
         */

        //自定义标题栏
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox();
        root.setId("root");
        // 引入样式
        root.getStylesheets().add("src/main/resources/mycss.css");

        //顶部
        VBox top = new VBox();
        top.setId("top");
        top.setPrefSize(300,26);
        // 标题栏
        AnchorPane title = new AnchorPane();
        Button close = new Button();
        close.setPrefWidth(46);
        close.setPrefHeight(46);
        close.setId("winClose");//winClose css样式Id
        Button min = new Button();
        min.setPrefWidth(46);
        min.setPrefHeight(46);
        min.setId("winMini");//winmini css样式Id

        title.getChildren().addAll(min,close);
        AnchorPane.setRightAnchor(close, 0.0);
        AnchorPane.setTopAnchor(close, 0.0);
        AnchorPane.setRightAnchor(min, 46.0);
        AnchorPane.setTopAnchor(min, 0.0);

        top.getChildren().add(title);
        // 注册点击事件
        close.setOnAction(event -> {
            stage.close();
        });
        min.setOnAction(event -> {
            stage.setIconified(true);
        });
//        root.getChildren().add(top);
        //---------------------


        HBox hBox_ip = new HBox();
        hBox_ip.setSpacing(30);

        Label label1 = new Label("Ip:");
        label1.setFont(Font.font("Times Roman", FontWeight.BOLD, 20));
        label1.setTextFill(Color.BLACK);
        TextField textField_ip = new TextField();
        textField_ip.setPrefSize(100,10);
        textField_ip.setEditable(true);
        textField_ip.setPromptText("Please input Ip"); // 设置单行输入框的提示语
        textField_ip.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式
        textField_ip.setStyle("-fx-background-color: transparent;");
        Line line = new Line(0, 0, 100, 0);
        VBox textlinebox = new VBox(textField_ip,line);
        hBox_ip.getChildren().addAll(label1,textlinebox);
        hBox_ip.setAlignment(Pos.CENTER);

        HBox hBox_port = new HBox();
        hBox_port.setSpacing(19);

        Label label_port = new Label("Port:");
        label_port.setFont(Font.font("Times Roman", FontWeight.BOLD, 20));
        label_port.setTextFill(Color.BLACK);

        TextField textField_port = new TextField();
        textField_port.setPrefSize(100,10);
        textField_port.setEditable(true);
        textField_port.setPromptText("Please input port"); // 设置单行输入框的提示语
        textField_port.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式
        textField_port.setStyle("-fx-background-color: transparent;");
        Line line_ = new Line(0, 0, 100, 0);
        VBox textlinebox_ = new VBox(textField_port,line_);
        hBox_port.getChildren().addAll(label_port,textlinebox_);
        hBox_port.setAlignment(Pos.CENTER);

        VBox vbox_ip = new VBox();
        Button connection_button = new Button("connect");   //Ip连接按钮
        BorderPane pane = new BorderPane();

        vbox_ip.setSpacing(20);

        vbox_ip.getChildren().addAll(hBox_ip,hBox_port,connection_button);
        vbox_ip.setAlignment(Pos.CENTER);

        root.getChildren().addAll(top,vbox_ip);
        root.setSpacing(200);

        pane.setCenter(root); // 把水平箱子放到边界窗格的中央
        pane.setStyle("-fx-background-image:url("+"file:src/main/resources/bg.jpg" +");"+"-fx-background-size:cover;"+"-fx-background-radius: 10px;");;
        connection_button.setStyle("-fx-background-color:#cbd6da;"+         //设置背景颜色
                        "-fx-background-radius:20;"+     //设置背景圆角
                        "-fx-text-fill:#ecb729;"+        //设置字体颜色
                        "-fx-border-radius:10;"         //设置边框圆角
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
                );


        Scene scene = new Scene(pane,400,600);
        scene.getStylesheets().add("src/main/resources/mycss.css");
        /**
         * "chat page"
         */

        BorderPane pane_chat = new BorderPane();
        pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg1.jpg" +");"+"-fx-background-size:cover;");
        chatVBox = new VBox();
        chatVBox.setSpacing(10);
        //chatVBox.setPadding(new Insets(10));
        chatVBox.setPrefSize(375,400);

        ScrollPane scrollPane_chat = new ScrollPane(chatVBox);
        scrollPane_chat.setPrefSize(390,600);
        scrollPane_chat.setId("mainScrollPane");
        scrollPane_chat.getStylesheets().add("src/main/resources/mycss.css");
//        scrollPane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg.jpg" +");"+"-fx-background-size:cover;"+"-fx-background-color: transparent;");
        pane_chat.setTop(scrollPane_chat);

        pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg1.jpg" +");"+"-fx-background-size:cover;");
        // 创建重启按钮0
        Button button_restart = new Button();
        ImageView imageView_restart = new ImageView(new Image("file:src/main/resources/restart.png"));
        imageView_restart.setFitWidth(20);
        imageView_restart.setFitHeight(20);
        button_restart.setGraphic(imageView_restart);
        button_restart.getStyleClass().add("button");
        // 创建图标按钮1（文本框输入）
        Button button_data = new Button();
        ImageView imageView_data = new ImageView(new Image("file:src/main/resources/c.png"));
        imageView_data.setFitWidth(20);
        imageView_data.setFitHeight(20);
        button_data.setGraphic(imageView_data);

        // 创建图标按钮2（文件输入）
        Button button_file = new Button();
        ImageView imageView_file = new ImageView(new Image("file:src/main/resources/wj.png"));
        imageView_file.setFitWidth(20);
        imageView_file.setFitHeight(20);
        button_file.setGraphic(imageView_file);

        // 创建图标按钮3（表情包）
        Button button_mark = new Button();
        ImageView imageView_mark = new ImageView(new Image("file:src/main/resources/1.png"));
        imageView_mark.setFitWidth(20);
        imageView_mark.setFitHeight(20);
        button_mark.setGraphic(imageView_mark);

        // 创建图标按钮4（工具）
        Button button_tool = new Button();
        ImageView imageView_tool = new ImageView(new Image("file:src/main/resources/tools.png"));
        imageView_tool.setFitHeight(20);
        imageView_tool.setFitWidth(20);
        button_tool.setGraphic(imageView_tool);

        HBox hBox_menu = new HBox();
        hBox_menu.getChildren().addAll(button_restart,button_data,button_file,button_mark,button_tool);
        hBox_menu.setSpacing(10);
        BorderPane.setMargin(hBox_menu,new Insets(0,0,150,0));

        HBox hBox_defaultsend = new HBox();
        hBox_defaultsend.setSpacing(20);

        TextField textField_defaultinput = new TextField();
        textField_defaultinput.setPrefSize(310,10);
        textField_defaultinput.setEditable(true);
        textField_defaultinput.setPromptText("Please input data"); // 设置单行输入框的提示语
        textField_defaultinput.setStyle("-fx-background-color: transparent;");
        Line line_data=new Line(0,0,100,0);
        textField_defaultinput.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

        Button button_defaultsend = new Button("send");
        button_defaultsend.setStyle("-fx-background-color:#07792c;"+         //设置背景颜色
                        "-fx-background-radius:20;"+     //设置背景圆角
                        "-fx-text-fill:#ecb729;"+        //设置字体颜色
                        "-fx-border-radius:10;" +        //设置边框圆角
                        "-fx-font-style:italic"
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
        );
        hBox_defaultsend.getChildren().addAll(textField_defaultinput,button_defaultsend);
        hBox_defaultsend.setAlignment(Pos.CENTER);

        HBox hBox_defaultencrypted = new HBox();
        hBox_defaultencrypted.setSpacing(20);

        textArea_defaultencrypted = new TextArea();
        textArea_defaultencrypted.setPrefSize(296,30);
        textArea_defaultencrypted.setEditable(false);
        textArea_defaultencrypted.setWrapText(true);
        textArea_defaultencrypted.setPromptText("Here is the encrypted data");
        textArea_defaultencrypted.setStyle("-fx-background-color: yellow;"+"-fx-border-radius:20;");
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
        textArea_defaultencrypted.setStyle("-fx-background-color: yellow;"+"-fx-border-radius:20;");
        Label label_defaultReceive = new Label("Received ");

        hBox_defaultReceive.getChildren().addAll(textArea_defaultReceive,label_defaultReceive);
        hBox_defaultReceive.setAlignment(Pos.CENTER);

        VBox vBox_defaultdata = new VBox();
        vBox_defaultdata.setSpacing(15);
        vBox_defaultdata.getChildren().addAll(hBox_menu,hBox_defaultsend,hBox_defaultencrypted,hBox_defaultReceive);

        pane_chat.setBottom(vBox_defaultdata);
        pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg.jpg" +");"+"-fx-background-size:cover;");
        BorderPane.setMargin(vBox_defaultdata,new Insets(0,0,17,0));

        Scene scene_chat = new Scene(pane_chat,400,800);

        connection_button.setOnAction(event -> {
            String ip = textField_ip.getText();
            int port = Integer.valueOf(textField_port.getText());
            try {
                chat_client = new chat_client(ip,port);
            } catch (IOException | SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            socket = chat_client.getSocket();
            try {
                sessionKey = src.main.java.chat.chat_client.ENV_client();
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException |
                     BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }

            clientListen = new Client_listen(socket, sessionKey);
            new Thread(clientListen).start();

            stage.setScene(scene_chat);
        });


        button_defaultsend.setOnAction(event -> {
            String message = textField_defaultinput.getText().trim();
            if (!message.isEmpty()) {
                addMessageToChat_Right(message);
                textField_defaultinput.clear();
            }
            Client_send clientSend = new Client_send(socket, sessionKey, message);
            new Thread(clientSend).start();
        });

        button_restart.setOnAction(event -> {
            final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
            Process process = null;
            final String cmd = "cmd /c start file://E:\\Desktop\\client\\src\\tools\\restart.bat";
            try {
                process = runtime.exec(cmd);
            } catch (final Exception e) {
                System.out.println("Error exec!");
            }
        });

        button_data.setOnAction(event -> {
            HBox hBox_send = new HBox();
            hBox_send.setSpacing(10);

            TextField textField_input = new TextField();
            textField_input.setPrefSize(30,10);
            textField_input.setEditable(true);
            textField_input.setPromptText("Please input data"); // 设置单行输入框的提示语
            textField_input.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式
            textField_input.setStyle("-fx-background-color:  transparent;");
            Line line1 = new Line(0, 0, 300, 0);
            VBox textlinebox1 = new VBox(textField_input,line1);
            textlinebox1.setSpacing(0);
            Button button_send = new Button("send");
            button_send.setStyle("-fx-background-color:#07792c;"+         //设置背景颜色
                            "-fx-background-radius:20;"+     //设置背景圆角
                            "-fx-text-fill:#ecb729;"+        //设置字体颜色
                            "-fx-border-radius:10;" +        //设置边框圆角
                            "-fx-font-style:italic"
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
            );
            hBox_send.getChildren().addAll(textlinebox1,button_send);
            hBox_send.setAlignment(Pos.CENTER);

            HBox hBox_encrypted = new HBox();
            hBox_encrypted.setSpacing(10);

            textArea_encrypted = new TextArea();
            textArea_encrypted.setPrefSize(296,10);
            textArea_encrypted.setEditable(false);
            textArea_encrypted.setWrapText(true);
            textArea_encrypted.setPromptText("Here is the encrypted data");
            textArea_encrypted.setStyle("-fx-background-color: yellow;"+"-fx-border-radius:20;");
            Line line2 = new Line(12, 0, 300, 0);
            VBox textlinebox2 = new VBox(textArea_encrypted,line2);
            textlinebox2.setSpacing(0);
            Label label_encrypted = new Label("encrypted");
            label_encrypted.setFont(Font.font("Times Roman", FontWeight.BOLD, 14));
            label_encrypted.setTextFill(Color.BLACK);
            hBox_encrypted.getChildren().addAll(textlinebox2,label_encrypted);
            hBox_encrypted.setAlignment(Pos.CENTER);

            HBox hBox_Receive = new HBox();
            hBox_Receive.setSpacing(10);

            textArea_Receive = new TextArea();
            textArea_Receive.setPrefSize(296,10);
            textArea_Receive.setEditable(false);
            textArea_Receive.setWrapText(true);
            textArea_Receive.setPromptText("Here is the encrypted data");

            textArea_Receive.setStyle("-fx-background-color: yellow;"+"-fx-border-radius:20;");
            Line line3= new Line(12, 0, 300, 0);
            VBox textlinebox3 = new VBox(textArea_Receive,line3);
            textlinebox3.setSpacing(0);
            Label label_Receive = new Label("Received ");
            label_Receive.setFont(Font.font("Times Roman", FontWeight.BOLD, 14));
            label_Receive.setTextFill(Color.BLACK);
            hBox_Receive.getChildren().addAll(textlinebox3,label_Receive);
            hBox_Receive.setAlignment(Pos.CENTER);

            VBox vBox_data = new VBox();
            vBox_data.setSpacing(15);
            vBox_data.getChildren().addAll(hBox_menu,hBox_send,hBox_encrypted,hBox_Receive);
//            vBox_data.setStyle("-fx-background-color: #f5eeee;");

            pane_chat.setBottom(vBox_data);
            pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg1.jpg" +");"+"-fx-background-size:cover;");
            BorderPane.setMargin(vBox_data,new Insets(0,0,0,0));

            textArea_Receive.setText(serverCipher);

            button_send.setOnAction(event1 -> {
                String message = textField_input.getText().trim();
                if (!message.isEmpty()) {
                    addMessageToChat_Right(message);
                    textField_input.clear();
                }
                Client_send clientSend = new Client_send(socket, sessionKey, message);
                new Thread(clientSend).start();

            });

            stage.setScene(scene_chat);
        });

        button_file.setOnAction(event -> {
            VBox vBox_file = new VBox();
            vBox_file.setSpacing(10);

            HBox hBox_fileChoose = new HBox();
            hBox_fileChoose.setSpacing(10);

            TextField textField_fileChoose = new TextField();
            textField_fileChoose.setPrefSize(250,10);
            textField_fileChoose.setEditable(true);
            textField_fileChoose.setPromptText("File path"); // 设置单行输入框的提示语
            textField_fileChoose.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

            Button button_filechoose = new Button("choose");
            Button button_fileSend = new Button("send");
            textField_fileChoose.setStyle("-fx-background-color:  transparent;");
            Line line1 = new Line(0, 0, 250, 0);
            VBox textlinebox1 = new VBox(textField_fileChoose,line1);
            textlinebox1.setSpacing(0);
            button_filechoose.setStyle("-fx-background-color:#8689d7;"+         //设置背景颜色
                            "-fx-background-radius:20;"+     //设置背景圆角
                            "-fx-text-fill:#04123d;"+        //设置字体颜色
                            "-fx-border-radius:10;" +        //设置边框圆角
                            "-fx-font-style:italic"
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
            );
            button_fileSend.setStyle("-fx-background-color:#07792c;"+         //设置背景颜色
                            "-fx-background-radius:20;"+     //设置背景圆角
                            "-fx-text-fill:#ecb729;"+        //设置字体颜色
                            "-fx-border-radius:10;" +        //设置边框圆角
                            "-fx-font-style:italic"
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
            );
            // pic
            HBox hBox_picChoose = new HBox();
            hBox_picChoose.setSpacing(10);
            TextField textField_picChoose = new TextField();
            textField_picChoose.setPrefSize(250,10);
            textField_picChoose.setEditable(true);
            textField_picChoose.setPromptText("Picture path"); // 设置单行输入框的提示语
            textField_picChoose.setAlignment(Pos.CENTER_LEFT); // 设置单行输入框的对齐方式

            Button button_picchoose = new Button("choose");
            Button button_picSend = new Button("send");
            textField_picChoose.setStyle("-fx-background-color:  transparent;");
            Line line2 = new Line(0, 0, 250, 0);
            VBox textlinebox2 = new VBox(textField_picChoose,line2);
            textlinebox1.setSpacing(0);

            button_picchoose.setStyle("-fx-background-color:#8689d7;"+         //设置背景颜色
                            "-fx-background-radius:20;"+     //设置背景圆角
                            "-fx-text-fill:#04123d;"+        //设置字体颜色
                            "-fx-border-radius:10;" +        //设置边框圆角
                            "-fx-font-style:italic"
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
            );
            button_picSend.setStyle("-fx-background-color:#07792c;"+         //设置背景颜色
                            "-fx-background-radius:20;"+     //设置背景圆角
                            "-fx-text-fill:#ecb729;"+        //设置字体颜色
                            "-fx-border-radius:10;" +        //设置边框圆角
                            "-fx-font-style:italic"
//                        "-fx-border-color:#FFFF00;"+     //设置边框颜色
//                        "-fx-border-style:dashed;"   //设置边框样式
//                        "-fx-border-width:5;"+           //设置边框宽度
//                        "-fx-border-insets:-5"           //设置边框插入值
            );
            //end pic
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
                    Client_send_file clientSend = null;
                    try {
                        clientSend = new Client_send_file(socket, sessionKey,selectedFile.getPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    new Thread(clientSend).start();
                });

            });

            button_picchoose.setOnAction(event1 -> {
                Stage stage_fileChoose = new Stage();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FileType","*.png","*.jpg","*.gif"));
                File selectedFile = fileChooser.showOpenDialog(stage_fileChoose);

                if(selectedFile != null){
                    textField_picChoose.setText(selectedFile.getPath());
                }

                button_picSend.setOnAction(event2 -> {
                    if (selectedFile != null) {
                        addPicToChat_right(selectedFile);
                    }
                    Client_send_pic clientSend = null;
                    try {
                        clientSend = new Client_send_pic(socket, sessionKey,selectedFile.getPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    new Thread(clientSend).start();
                });
            });

            hBox_fileChoose.getChildren().addAll(textlinebox1,button_filechoose,button_fileSend);
            hBox_fileChoose.setAlignment(Pos.CENTER);
            hBox_picChoose.getChildren().addAll(textlinebox2,button_picchoose,button_picSend);
            hBox_picChoose.setAlignment(Pos.CENTER);
            vBox_file.setSpacing(15);
            vBox_file.getChildren().addAll(hBox_menu,hBox_fileChoose,hBox_picChoose);

            pane_chat.setBottom(vBox_file);
            pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg1.jpg" +");"+"-fx-background-size:cover;");
            BorderPane.setMargin(vBox_file,new Insets(0,0,0,0));

            stage.setScene(scene_chat);


        });

        button_mark.setOnAction(event2 -> {
            VBox vBox_file = new VBox();
            vBox_file.setSpacing(10);
            HBox hBox1 = new HBox();
            hBox1.setSpacing(10);
            HBox hBox2 = new HBox();
            hBox2.setSpacing(10);

            ImageView imageView0 = new ImageView(new Image("file:src/main/resources/add.png",40,40,false,false));
            ImageView imageView1 = new ImageView(new Image("file:src/main/resources/1.png",40,40,false,false));
            ImageView imageView2 = new ImageView(new Image("file:src/main/resources/2.png",40,40,false,false));
            ImageView imageView3 = new ImageView(new Image("file:src/main/resources/3.png",40,40,false,false));
            ImageView imageView4 = new ImageView(new Image("file:src/main/resources/4.png",40,40,false,false));
            ImageView imageView5 = new ImageView(new Image("file:src/main/resources/5.png",40,40,false,false));
            ImageView imageView6 = new ImageView(new Image("file:src/main/resources/6.png",40,40,false,false));
            ImageView imageView7 = new ImageView(new Image("file:src/main/resources/7.png",40,40,false,false));
            ImageView imageView8 = new ImageView(new Image("file:src/main/resources/8.png",40,40,false,false));

            Button btn0 = new Button("",imageView0);
//            btn1.setLayoutX(50);btn1.setLayoutY(50);btn1.setPrefHeight(50);btn1.setPrefWidth(50);
            Button btn1 = new Button("",imageView1);
            Button btn2 = new Button("",imageView2);
//            btn2.setLayoutX(50);btn2.setLayoutY(50);btn2.setPrefHeight(50);btn2.setPrefWidth(50);
            Button btn3 = new Button("",imageView3);
//            btn3.setLayoutX(50);btn3.setLayoutY(50);btn3.setPrefHeight(50);btn3.setPrefWidth(50);
            Button btn4 = new Button("",imageView4);
//            btn4.setLayoutX(50);btn4.setLayoutY(50);btn4.setPrefHeight(50);btn4.setPrefWidth(50);
            Button btn5 = new Button("",imageView5);
//            btn5.setLayoutX(50);btn5.setLayoutY(50);btn5.setPrefHeight(50);btn5.setPrefWidth(50);
            Button btn6 = new Button("",imageView6);
            Button btn7 = new Button("",imageView7);
            Button btn8 = new Button("",imageView8);


            btn0.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn1.setBackground(Background.EMPTY);btn1.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn1.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn1.setBackground(Background.EMPTY);btn1.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn2.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn2.setBackground(Background.EMPTY);btn2.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn3.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn3.setBackground(Background.EMPTY);btn3.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn4.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn4.setBackground(Background.EMPTY);btn4.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn5.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn5.setBackground(Background.EMPTY);btn5.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn6.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn6.setBackground(Background.EMPTY);btn6.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn7.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn7.setBackground(Background.EMPTY);btn7.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
            btn8.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));btn8.setBackground(Background.EMPTY);btn8.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");

            btn1.setOnAction(event3 ->  {
                addEmojiToChat_right("1");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"1");
                new Thread(clientSendEmoji).start();
            });
            btn2.setOnAction(event3 ->  {
                addEmojiToChat_right("2");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"2");
                new Thread(clientSendEmoji).start();
            });
            btn3.setOnAction(event3 ->  {
                addEmojiToChat_right("3");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"3");
                new Thread(clientSendEmoji).start();
            });
            btn4.setOnAction(event3 ->  {
                addEmojiToChat_right("4");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"4");
                new Thread(clientSendEmoji).start();
            });
            btn5.setOnAction(event3 ->  {
                addEmojiToChat_right("5");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"5");
                new Thread(clientSendEmoji).start();
            });
            btn6.setOnAction(event3 ->  {
                addEmojiToChat_right("6");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"6");
                new Thread(clientSendEmoji).start();
            });
            btn7.setOnAction(event3 ->  {
                addEmojiToChat_right("7");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"7");
                new Thread(clientSendEmoji).start();
            });
            btn8.setOnAction(event3 ->  {
                addEmojiToChat_right("8");
                Client_send_emoji clientSendEmoji = new Client_send_emoji(socket,sessionKey,"8");
                new Thread(clientSendEmoji).start();
            });


            hBox1.getChildren().addAll(btn0,btn1,btn2,btn3,btn4,btn5);
            hBox1.setAlignment(Pos.TOP_LEFT);
            hBox2.getChildren().addAll(btn6,btn7,btn8);
            hBox2.setAlignment(Pos.TOP_LEFT);
            vBox_file.getChildren().addAll(hBox_menu,hBox1,hBox2);
            vBox_file.setStyle("-fx-background-color: #67a5a9;");
            pane_chat.setBottom(vBox_file);
            pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg1.jpg" +");"+"-fx-background-size:cover;");
            BorderPane.setMargin(vBox_file,new Insets(0,0,0,0));
            stage.setScene(scene_chat);
        });

        button_tool.setOnAction(event3 -> {
            VBox vBox_file = new VBox();
            vBox_file.setSpacing(15);
            HBox hBox_fileChoose = new HBox();
            hBox_fileChoose.setSpacing(10);
            ImageView imageView0 = new ImageView(new Image("file:src/main/resources/chat.png",40,40,false,false));
            Button btn0 = new Button("",imageView0);
            btn0.setOnAction(event4 -> {
                final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
                Process process = null;
                final String cmd = "cmd /c start file://E:\\Desktop\\client\\src\\tools\\sparkapi\\chat.bat";
                try {
                    process = runtime.exec(cmd);
                } catch (final Exception e) {
                    System.out.println("Error exec!");
                }
            });
            ImageView imageView1 = new ImageView(new Image("file:src/main/resources/wechat.png",40,40,false,false));
            Button btn1 = new Button("",imageView1);
            btn1.setOnAction(event5 -> {
                final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
                Process process = null;
                final String cmd = "rundll32 url.dll FileProtocolHandler file://A:\\Tencent\\WeChat\\WeChat.exe";
                try {
                    process = runtime.exec(cmd);
                } catch (final Exception e) {
                    System.out.println("Error exec!");
                }
            });
            // "A:\Genshin Impact Cloud Game\Genshin Impact Cloud Game.exe"
            ImageView imageView2 = new ImageView(new Image("file:src/main/resources/op.png",40,40,false,false));
            Button btn2 = new Button("",imageView2);
            btn2.setOnAction(event5 -> {
                final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
                Process process = null;
                final String cmd = "rundll32 url.dll FileProtocolHandler file://A:\\Genshin Impact Cloud Game\\Genshin Impact Cloud Game.exe";
                try {
                    process = runtime.exec(cmd);
                } catch (final Exception e) {
                    System.out.println("Error exec!");
                }
            });
            //"A:\QSanguosha20201008\QSanguosha.exe"
            ImageView imageView3 = new ImageView(new Image("file:src/main/resources/sha.png",40,40,false,false));
            Button btn3 = new Button("",imageView3);
            btn3.setOnAction(event5 -> {
                final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
                Process process = null;
                final String cmd = "rundll32 url.dll FileProtocolHandler file://A:\\sanguosha\\SGSOL\\SGSOL.exe";
                try {
                    process = runtime.exec(cmd);
                } catch (final Exception e) {
                    System.out.println("Error exec!");
                }
            });
            ImageView imageView4 = new ImageView(new Image("file:src/main/resources/bili.png",40,40,false,false));
            Button btn4 = new Button("",imageView4);
            btn4.setOnAction(event5 -> {
                final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
                Process process = null;
                final String cmd = "rundll32 url.dll FileProtocolHandler file://C:\\Program Files\\bilibili\\哔哩哔哩.exe";
                try {
                    process = runtime.exec(cmd);
                } catch (final Exception e) {
                    System.out.println("Error exec!");
                }
            });


            hBox_fileChoose.getChildren().addAll(btn0,btn1,btn2,btn3,btn4);
            hBox_fileChoose.setAlignment(Pos.TOP_LEFT);
            vBox_file.getChildren().addAll(hBox_menu,hBox_fileChoose);
            vBox_file.setStyle("-fx-background-color: #67a5a9;");
            pane_chat.setBottom(vBox_file);
            pane_chat.setStyle("-fx-background-image:url("+"file:src/main/resources/bg1.jpg" +");"+"-fx-background-size:cover;");
            BorderPane.setMargin(vBox_file,new Insets(0,0,10,0));
            stage.setScene(scene_chat);
        });


        stage.setScene(scene);

        stage.setTitle("Client");
        stage.getIcons().add(new Image("file:src/main/resources/R-C.png"));
        stage.setResizable(false);  //窗口大小变
        stage.show();

    }

//    public static void refresh(String cipher,String plain){
//        Platform.runLater(() -> textArea_defaultReceive.setText(cipher));
//        Platform.runLater(() -> textArea_Receive.setText(cipher));
//        Platform.runLater(() -> addMessageToChat_Right(plain));
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
        Platform.runLater(Main::addFileToChat);
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
    private static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;}

    private static void addMessageToChat_Right(String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";
        String chatMessage_message = message;
        Label messageLabel = new Label(chatMessage);
        if(isHttpUrl(message)) {
            Hyperlink hyperlink = new Hyperlink(message);
            hyperlink.setOnAction(event -> {
                String filePath="E:/Desktop/client/src/tools/website.bat";
                try {
                    FileWriter writer = new FileWriter(filePath);
                    writer.write("@echo off\n");
                    writer.write("if not \"%~1\"==\"p\" start /min cmd.exe /c %0 p&exit\n");
                    writer.write("start "+ message);
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String filePath1="E:/Desktop/client/src/tools/1.vbs";
                try {
                    FileWriter writer = new FileWriter(filePath1);
                    writer.write("set ws=WScript.CreateObject(\"WScript.Shell\")\n");
                    writer.write("ws.Run \"E:\\Desktop\\client\\src\\tools\\website.bat\",0");
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Runtime.getRuntime().exec(new String[] { "C:\\Windows\\System32\\wscript.exe", "E:\\Desktop\\client\\src\\tools\\1.vbs" });
                } catch (final Exception e) {
                    System.out.println("Error exec!");
                }
//                final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
//                Process process = null;
//                final String cmd = "cmd /c start file://E:\\Desktop\\client\\src\\tools\\website.bat";
//                try {
//                    process = runtime.exec(cmd);
//                } catch (final Exception e) {
//                    System.out.println("Error exec!");
//                }

            });
            VBox vBox_chatMessage = new VBox();
            vBox_chatMessage.getChildren().addAll(messageLabel,hyperlink);
            vBox_chatMessage.setAlignment(Pos.CENTER_RIGHT);

            HBox hBox_right = new HBox(vBox_chatMessage);
            hBox_right.setAlignment(Pos.BASELINE_RIGHT);
            //chatVBox.setAlignment(Pos.BASELINE_RIGHT);

            chatVBox.getChildren().add(hBox_right);
        }
        else {
            Label messageLabel_message = new Label(chatMessage_message);
            VBox vBox_chatMessage = new VBox();
            vBox_chatMessage.getChildren().addAll(messageLabel,messageLabel_message);
            vBox_chatMessage.setAlignment(Pos.CENTER_RIGHT);

            HBox hBox_right = new HBox(vBox_chatMessage);
            hBox_right.setAlignment(Pos.BASELINE_RIGHT);
            //chatVBox.setAlignment(Pos.BASELINE_RIGHT);

            chatVBox.getChildren().add(hBox_right);
        }


    }

    private static void addFileToChat() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        ImageView fileIcon = new ImageView(new Image("file:src/main/resources/wj.png"));
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);

        // 显示文件图
        //Label fileLabel = new Label(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon);

        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);

        chatVBox.getChildren().add(chatMessageBox);
    }

    private static void addFileToChat_right(File file) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        ImageView fileIcon = new ImageView(new Image("file:src/main/resources/wj.png"));
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);

        // 显示文件图
        Button fileLabel = new Button(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon,fileLabel);
        fileBox.setAlignment(Pos.BASELINE_RIGHT);
        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_RIGHT);

        HBox chatMessageBox_right = new HBox(chatMessageBox);
        chatMessageBox_right.setAlignment(Pos.BASELINE_RIGHT);
        fileLabel.setOnAction(event -> {
            String filePath="E:/Desktop/client/src/tools/image.bat";
            try {
                FileWriter writer = new FileWriter(filePath);
                writer.write("@echo off\n");
                writer.write("if not \"%~1\"==\"p\" start /min cmd.exe /c %0 p&exit\n");
                writer.write("start \"\" "+ file.getPath());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String filePath1="E:/Desktop/client/src/tools/1.vbs";
            try {
                FileWriter writer = new FileWriter(filePath1);
                writer.write("set ws=WScript.CreateObject(\"WScript.Shell\")\n");
                writer.write("ws.Run \"E:\\Desktop\\client\\src\\tools\\image.bat\",0");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
            Process process = null;
//            final String cmd = "cmd /c start file://E:\\Desktop\\client\\src\\tools\\image.bat";
            try {
                Runtime.getRuntime().exec(new String[] { "C:\\Windows\\System32\\wscript.exe", "E:\\Desktop\\client\\src\\tools\\1.vbs" });
            } catch (final Exception e) {
                System.out.println("Error exec!");
            }
        });
        chatVBox.getChildren().add(chatMessageBox_right);
    }

    private static void addEmojiToChat_right(String emojiNum) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        ImageView fileIcon = new ImageView(new Image("file:src/main/resources/"+emojiNum+".png"));
        fileIcon.setFitWidth(50);
        fileIcon.setFitHeight(50);

        // 显示文件图
        HBox fileBox = new HBox(fileIcon);
        fileBox.setAlignment(Pos.BASELINE_RIGHT);
        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_RIGHT);

        HBox chatMessageBox_right = new HBox(chatMessageBox);
        chatMessageBox_right.setAlignment(Pos.BASELINE_RIGHT);

        chatVBox.getChildren().add(chatMessageBox_right);
    }

    private static void addPicToChat_right(File file) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String chatMessage = "[" + formattedDateTime + "] ";

        // 创建文件图标
        System.out.println(file.getPath().replaceAll("\\\\","/"));
        ImageView fileIcon = new ImageView(new Image("file:"+file.getPath().replaceAll("\\\\","/")));
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);

        // 显示文件图
        Button fileLabel = new Button(file.getName(), fileIcon);
        HBox fileBox = new HBox(fileIcon,fileLabel);
        fileBox.setAlignment(Pos.BASELINE_RIGHT);
        Label messageLabel = new Label(chatMessage);
        VBox chatMessageBox = new VBox(messageLabel, fileBox);
        chatMessageBox.setAlignment(Pos.BASELINE_RIGHT);

        HBox chatMessageBox_right = new HBox(chatMessageBox);
        chatMessageBox_right.setAlignment(Pos.BASELINE_RIGHT);
        fileLabel.setOnAction(event -> {
            String filePath="E:/Desktop/client/src/tools/image.bat";
            try {
                FileWriter writer = new FileWriter(filePath);
                writer.write("@echo off\n");
                writer.write("if not \"%~1\"==\"p\" start /min cmd.exe /c %0 p&exit\n");
                writer.write("start \"\" "+ file.getPath());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String filePath1="E:/Desktop/client/src/tools/1.vbs";
            try {
                FileWriter writer = new FileWriter(filePath1);
                writer.write("set ws=WScript.CreateObject(\"WScript.Shell\")\n");
                writer.write("ws.Run \"E:\\Desktop\\client\\src\\tools\\image.bat\",0");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            final Runtime runtime = Runtime.getRuntime();//java用来调用外部程序的类
            Process process = null;
//            final String cmd = "cmd /c start file://E:\\Desktop\\client\\src\\tools\\image.bat";
            try {
                Runtime.getRuntime().exec(new String[] { "C:\\Windows\\System32\\wscript.exe", "E:\\Desktop\\client\\src\\tools\\1.vbs" });
            } catch (final Exception e) {
                System.out.println("Error exec!");
            }
        });
        chatVBox.getChildren().add(chatMessageBox_right);
    }
}
