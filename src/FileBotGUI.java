import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.geometry.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FileBotGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String currentDir = Paths.get("").toAbsolutePath().toString();
        String delimiter = "/";
        if (currentDir.contains("\\"))
            delimiter = "\\";
        currentDir += delimiter;

        primaryStage.setTitle("Run FileBot");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Configure and Run");
        scenetitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label target = new Label("Target Directory:");
        target.setMinWidth(125);
        grid.add(target, 0, 1);

        TextField targetField = new TextField();
        targetField.insertText(0, "C:/Extemp/files");
        targetField.setMinWidth(300);
        grid.add(targetField, 1, 1);
//        TODO: Directory Chooser

        Label feeds = new Label("Feeds File:");
        grid.add(feeds, 0, 2);

        TextField feedsField = new TextField();
        feedsField.insertText(0, currentDir + "feeds.in");
        grid.add(feedsField, 1, 2);

        Label domHelper = new Label("DOM Helper File:");
        grid.add(domHelper, 0, 3);

        TextField domHelperField = new TextField();
        domHelperField.insertText(0, currentDir + "domHelper.in");
        grid.add(domHelperField, 1, 3);

        Label maxArticles = new Label("Maximum Articles:");
        grid.add(maxArticles, 0, 4);

        TextField maxArticlesField = new TextField();
        maxArticlesField.insertText(0, "1000000");
        grid.add(maxArticlesField, 1, 4);

        Label maxArticlesPerFeed = new Label("Maximum Articles Per Feed:");
        maxArticlesPerFeed.setWrapText(true);
        grid.add(maxArticlesPerFeed, 0, 5);

        TextField maxArticlesPerFeedField = new TextField();
        maxArticlesPerFeedField.insertText(0, "100");
        grid.add(maxArticlesPerFeedField, 1, 5);

        Label maxFeeds = new Label("Maximum Feeds:");
        grid.add(maxFeeds, 0, 6);

        TextField maxFeedsField = new TextField();
        maxFeedsField.insertText(0, "1000");
        grid.add(maxFeedsField, 1, 6);

        Label scramble = new Label("Scramble:");
        grid.add(scramble, 0, 7);

        CheckBox scrambleBox = new CheckBox();
        scrambleBox.setSelected(true);
        grid.add(scrambleBox, 1, 8);

        Label loop = new Label("Loop:");
        grid.add(loop, 0, 8);

        CheckBox loopBox = new CheckBox();
        loopBox.setSelected(true);
        grid.add(loopBox, 1, 7);

        Button btn = new Button("Run FileBot");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 9);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                String t = targetField.getText();
                String f = feedsField.getText();
                String dH = domHelperField.getText();
                int mA = Integer.parseInt(maxArticlesField.getText());
                int mAPF = Integer.parseInt(maxArticlesPerFeedField.getText());
                int mF = Integer.parseInt(maxFeedsField.getText());
                Boolean s = scrambleBox.isSelected();
                Boolean l = scrambleBox.isSelected();

                Thread a = new Thread(() -> {
                    try {
                        FileBot.runFiler(t, f, dH, mA, mAPF, mF, s, l);

                    } catch (IOException i) {
                        System.out.println("IO Error");
                    } catch (URISyntaxException u) {
                        System.out.println("URI Error");
                    }
                });
                a.start();
            }
        });

        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}