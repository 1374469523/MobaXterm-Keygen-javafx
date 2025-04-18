package com.yaojing.mobaxtermkeygenjavafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.yaojing.mobaxtermkeygenjavafx.MobaXtermKeygen.generateLicense;

public class MobaXtermKeygenApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        TextArea outputTextArea = new TextArea();
        TextField userNameField = new TextField(System.getProperty("user.name"));
        userNameField.setPromptText("请输入 Windows 用户名");
        userNameField.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 8px; -fx-border-radius: 4px; -fx-pref-width: 360px; -fx-border-color: #ccc; -fx-border-width: 1px;");

        TextField versionField = new TextField();
        versionField.setPromptText("请输入版本号（例如，10.9）");
        versionField.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 8px; -fx-border-radius: 4px; -fx-pref-width: 360px; -fx-border-color: #ccc; -fx-border-width: 1px;");

        Button generateButton = new Button("生成许可证");
        generateButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-pref-width: 360px;");
        generateButton.setOnAction(event -> {
            String userName = userNameField.getText();
            String version = versionField.getText();
            if (userName.isEmpty() || version.isEmpty()) {
                outputTextArea.appendText("[*] 错误：请输入用户名和版本号。\n");
                return;
            }
            String[] versionParts = version.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0]);
            int minorVersion = Integer.parseInt(versionParts[1]);

            try {
                generateLicense(MobaXtermKeygen.LicenseType.Professional, 1, userName, majorVersion, minorVersion);
                outputTextArea.appendText("[*] 成功！\n");
                outputTextArea.appendText("[*] 生成文件：" + new File("Custom.mxtpro").getAbsolutePath() + "\n");
                outputTextArea.appendText("[*] 请将新生成的文件移动或复制到 MobaXterm 的安装路径。\n");
            } catch (Exception e) {
                outputTextArea.appendText("[*] 错误：" + e.getMessage() + "\n");
            }
        });

        Button openFolderButton = new Button("打开生成文件夹");
        openFolderButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-pref-width: 360px;");
        openFolderButton.setOnAction(event -> {
            try {
                File generatedFile = new File(System.getProperty("user.dir") + File.separator + "Custom.mxtpro");
                if (generatedFile.exists()) {
                    Desktop.getDesktop().open(generatedFile.getParentFile());
                }
            } catch (IOException e) {
                outputTextArea.appendText("[*] 错误：无法打开文件夹：" + e.getMessage() + "\n");
            }
        });

        outputTextArea.setEditable(false);
        outputTextArea.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 8px; -fx-border-radius: 4px; -fx-pref-width: 360px; -fx-border-color: #ccc; -fx-border-width: 1px;");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(userNameField, versionField, generateButton, openFolderButton, outputTextArea);

        Scene scene = new Scene(layout);
        primaryStage.setTitle("MobaXterm 许可证生成器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}