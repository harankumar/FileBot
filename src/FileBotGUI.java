import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.geometry.*;

import java.util.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.jfoenix.controls.*;

public class FileBotGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static void alertError(ArrayList<String> messages) {
        String errorText = "FileBot encountered the following errors with your input: ";
        for (String message : messages)
            errorText += "\n - " + message;
        errorText += "\nPlease correct these issues then try again.";
        output.appendText("\n" + errorText);
    }

    public static TextArea output;
    public static Button run;
    public static Label runLabel;
    public static PrintStream out = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            Platform.runLater(() -> output.appendText(String.valueOf((char) b)));
        }
    }, true);

    public static Boolean isRunning = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String curr = Paths.get("").toAbsolutePath().toString();
        final String delimiter = curr.contains("\\") ? "\\" : "/";
        final String currentDir = curr + delimiter;

        primaryStage.setTitle("Run FileBot");

        GridPane grid1 = new GridPane();
        grid1.setAlignment(Pos.CENTER);
        grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.setPadding(new Insets(25, 25, 25, 25));

        Label target = new Label("Output Directory:");
        target.setMinWidth(125);
        grid1.add(target, 0, 0);
        TextField targetField = new JFXTextField();
        targetField.setText("C:/Extemp/files".replace("/", delimiter));
        targetField.setMinWidth(300);
        grid1.add(targetField, 1, 0, 2, 1);
        Button chooseTargetDir = new JFXButton("Open Directory Picker");
        chooseTargetDir.getStyleClass().add("button-raised");
        chooseTargetDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory =
                        directoryChooser.showDialog(primaryStage);

                if (selectedDirectory == null) {
                } else {
                    targetField.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });
        grid1.add(chooseTargetDir, 1, 1);

        GridPane grid2 = new GridPane();
        grid2.setAlignment(Pos.CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(25, 25, 25, 25));

        grid2.add(new Label("Feeds File:"), 0, 0);
        TextField feeds = new JFXTextField();
        feeds.setText("feeds.in");
        feeds.setMinWidth(300);
        grid2.add(feeds, 1, 0, 2, 1);
        Button chooseFeedsFile = new JFXButton("Open File Picker");
        chooseFeedsFile.getStyleClass().add("button-raised");
        grid2.add(chooseFeedsFile, 1, 1);

        grid1.add(new Label("Feeds to file:"), 0, 2);
        ToggleGroup feedChoices = new ToggleGroup();
        feedChoices.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null)
                    return;
                String text = "feeds.in";
                switch (feedChoices.getSelectedToggle().getUserData().toString()) {
                    case "RSS":
                        text = "feeds.in";
                        break;
                    case "GN":
                        text = "gn_searches.in";
                        break;
                    case "BN":
                        text = "bing_searches.in";
                        break;
                    default:
                        System.err.println("Unexpected userData");
                }
                feeds.setText(text);
            }
        });
        RadioButton rb1 = new JFXRadioButton();
        rb1.setToggleGroup(feedChoices);
        rb1.setUserData("RSS");
        rb1.setSelected(true);
        grid1.add(rb1, 2, 2);
        grid1.add(new Label("RSS Feeds"), 1, 2);
        ToggleButton rb2 = new JFXRadioButton();
        rb2.setToggleGroup(feedChoices);
        rb2.setUserData("GN");
        grid1.add(rb2, 2, 3);
        grid1.add(new Label("Google News Feeds"), 1, 3);
        ToggleButton rb3 = new JFXRadioButton();
        rb3.setToggleGroup(feedChoices);
        rb3.setUserData("BN");
        grid1.add(rb3, 2, 4);
        grid1.add(new Label("Bing News Feeds"), 1, 4);
        chooseFeedsFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile =
                        fileChooser.showOpenDialog(primaryStage);

                if (selectedFile == null) {
                } else {
                    feeds.setText(selectedFile.getAbsolutePath());
                    rb1.setSelected(false);
                    rb2.setSelected(false);
                    rb3.setSelected(false);
                }
            }
        });
        feeds.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                rb1.setSelected(false);
                rb2.setSelected(false);
                rb3.setSelected(false);
                if (feeds.getText().equals("feeds.in"))
                    rb1.setSelected(true);
                else if (feeds.getText().equals("gn_searches.in"))
                    rb2.setSelected(true);
                else if (feeds.getText().equals("bing_searches.in"))
                    rb3.setSelected(true);
            }
        });

        grid2.add(new Label("DOM Helper:"), 0, 2);
        TextField domHelper = new JFXTextField();
        domHelper.setText("domHelper.in");
        domHelper.setMinWidth(300);
        grid2.add(domHelper, 1, 2);
        Button chooseDOMHelperFile = new JFXButton("Open File Picker");
        chooseDOMHelperFile.getStyleClass().add("button-raised");
        chooseDOMHelperFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile =
                        fileChooser.showOpenDialog(primaryStage);

                if (selectedFile == null) {
                } else {
                    domHelper.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        grid2.add(chooseDOMHelperFile, 1, 3);

        grid2.add(new Label("Maximum Articles:"), 0, 4);
        TextField maxArticles = new JFXTextField();
        maxArticles.setText("1000000");
        grid2.add(maxArticles, 1, 4);

        Label mapf = new Label("Maximum Articles Per Feed:");
        mapf.setWrapText(true);
        grid2.add(mapf, 0, 5);
        TextField maxArticlesPerFeed = new JFXTextField();
        maxArticlesPerFeed.setText("100");
        grid2.add(maxArticlesPerFeed, 1, 5);

        grid2.add(new Label("Maximum Feeds:"), 0, 6);
        TextField maxFeeds = new JFXTextField();
        maxFeeds.setText("1000");
        grid2.add(maxFeeds, 1, 6);

        FlowPane f1 = new FlowPane();
        f1.setVgap(8);
        f1.setHgap(15);
        HBox h1 = new HBox();
        h1.getChildren().add(new Label("Scramble:"));
        CheckBox scramble = new JFXCheckBox();
        scramble.setSelected(true);
        h1.getChildren().add(scramble);
        f1.getChildren().add(h1);
        HBox h2 = new HBox();
        h2.getChildren().add(new Label("Loop:"));
        CheckBox loop = new JFXCheckBox();
        loop.setSelected(true);
        h2.getChildren().add(loop);
        f1.getChildren().add(h2);
        grid2.add(f1, 0, 7, 3, 1);

        Button restoreDefaults = new JFXButton("Restore Defaults");
        restoreDefaults.getStyleClass().add("button-raised");
        restoreDefaults.setStyle("-fx-background-color: #9E9E9E");
        restoreDefaults.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                targetField.setText("C:/Extemp/files".replace("/", delimiter));
                rb1.setSelected(true);
                feeds.setText("feeds.in");
                domHelper.setText("domHelper.in");
                maxArticles.setText("1000000");
                maxArticlesPerFeed.setText("100");
                maxFeeds.setText("1000");
                scramble.setSelected(true);
                loop.setSelected(true);
            }
        });
        HBox h3 = new HBox(10);
        h3.setAlignment(Pos.BOTTOM_RIGHT);
        h3.getChildren().add(restoreDefaults);
        grid2.add(h3, 1, 8);

        GridPane grid3 = new GridPane();
        grid3.setAlignment(Pos.CENTER);
        grid3.setHgap(10);
        grid3.setVgap(10);

        run = new Button("Run FileBot");
        run.setStyle("-fx-background-color: #8BC34A;");
        runLabel = new Label();
        HBox h4 = new HBox(10);
        h4.getChildren().addAll(run, runLabel);
        grid3.add(h4, 0, 0);

        output = new TextArea();
        System.setOut(out);
        System.setErr(out);
        output.setWrapText(true);
        output.setEditable(false);
        output.getStyleClass().add("console-output");
        output.setMinSize(400, 400);
        grid3.add(output, 0, 1);


        run.setOnAction(new EventHandler<ActionEvent>() {
            boolean validInputs() {
                boolean ret = true;
                ArrayList<String> errors = new ArrayList<String>();
                String t = targetField.getText();
                if (Files.notExists(Paths.get(t)) || !Files.isDirectory(Paths.get(t))) {
                    errors.add("Target Field '" + t + "' is not a valid directory.");
                    ret = false;
                }
                String f = feeds.getText();
                if (f.equals("feeds.in") || f.equals("bing_searches.in") || f.equals("gn_searches.in")) {
                } else if (Files.notExists(Paths.get(f)) || !Files.isRegularFile(Paths.get(f))) {
                    errors.add("Feeds File '" + f + "' is invalid.");
                    ret = false;
                }
                String dH = domHelper.getText();
                if (dH.equals("domHelper.in")) {
                } else if (Files.notExists(Paths.get(dH)) || !Files.isRegularFile(Paths.get(dH))) {
                    errors.add("DOM Helper File '" + dH + "' is invalid.");
                    ret = false;
                }
                try {
                    int mA = Integer.parseInt(maxArticles.getText());
                } catch (NumberFormatException n) {
                    errors.add("Maximum Articles '" + maxArticles.getText() + "' is not a valid integer.");
                    ret = false;
                }
                try {
                    int mAPF = Integer.parseInt(maxArticlesPerFeed.getText());
                } catch (NumberFormatException n) {
                    errors.add("Maximum Articles per Feed '" + maxArticlesPerFeed.getText() + "' is not a valid integer.");
                    ret = false;
                }
                try {
                    int mF = Integer.parseInt(maxFeeds.getText());
                } catch (NumberFormatException n) {
                    errors.add("Maximum Feeds '" + maxFeeds.getText() + "' is not a valid integer.");
                    ret = false;
                }
                if (!ret)
                    alertError(errors);
                return ret;
            }

            @Override
            public void handle(ActionEvent evt) {
                Thread a = new Thread(() -> {
                    if (isRunning) {
                        output.appendText("\nCan't run FileBot as it is already filing." +
                                "\nPlease wait, or open a new instance of the program.");
                        return;
                    }
                    if (validInputs()) {
                        String t = targetField.getText();
                        String f = feeds.getText();
                        String dH = domHelper.getText();
                        int mA = Integer.parseInt(maxArticles.getText());
                        int mAPF = Integer.parseInt(maxArticlesPerFeed.getText());
                        int mF = Integer.parseInt(maxFeeds.getText());
                        Boolean s = scramble.isSelected();
                        Boolean l = scramble.isSelected();
                        try {
//                          TODO: safe way to kill this
                            running(true);
                            FileBot.runFiler(t, f, dH, mA, mAPF, mF, s, l);
                            running(false);
                            System.out.println("Done Filing!");
                            isRunning = false;
                        } catch (IOException i) {
                            System.out.println("IO Error");
                        } catch (URISyntaxException u) {
                            System.out.println("URI Error");
                        }
                    } else {
                        runLabel.setText("Error. Fix issues and rerun.");
                    }
                });
                a.start();
            }
        });

        TitledPane tp1 = new TitledPane();
        tp1.setText("Step 1: Configure Basic Settings");
        tp1.setContent(grid1);

        TitledPane tp2 = new TitledPane();
        tp2.setText("Step 2: Configure Advanced Settings (or don't)");
        tp2.setContent(grid2);

        TitledPane tp3 = new TitledPane();
        tp3.setText("Step 3: Run FileBot");
        tp3.setContent(grid3);

        Accordion accordion = new Accordion(tp1, tp2, tp3);
        accordion.setExpandedPane(tp1);

        Scene scene = new Scene(accordion, 500, 600);
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void running(boolean x) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                run.setDisable(x);
                runLabel.setText(x ? "Filer running." : "Operation completed.");
            }
        });
    }

}