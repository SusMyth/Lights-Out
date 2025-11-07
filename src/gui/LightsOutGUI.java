package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LightsOutModel;
import model.Observer;
import model.Tile;
import java.io.File;

/**
 * LightsOutGUI implements a graphical interface for the Lights Out game.
 * It provides a 5x5 grid where users can toggle lights, request hints,
 * start new games, and load saved games from files.
 *
 * @author SASMIT MISHRA
 */
public class LightsOutGUI extends Application implements Observer<LightsOutModel, String> {
    private LightsOutModel model;
    private Label movesLabel;
    private Label messageLabel;
    private Button[][] buttons = new Button[5][5];
    private Stage primaryStage;

    /**
     * Starts the JavaFX application and initializes the GUI components.
     *
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        model = new LightsOutModel();
        model.addObserver(this);

        movesLabel = new Label("Moves: 0");
        movesLabel.setStyle("-fx-font-weight: bold");
        messageLabel = new Label("Message:");
        messageLabel.setStyle("-fx-font-weight: bold");
        BorderPane borderpane = new BorderPane();

        FlowPane topPane = new FlowPane();
        topPane.setHgap(90);
        topPane.getChildren().addAll(movesLabel, messageLabel);
        borderpane.setTop(topPane);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(80);
        Button hintButton = new Button("Hint");
        Button newGameButton = new Button("New Game");
        Button loadGameButton = new Button("Load Game");

        hintButton.setOnAction(e -> model.getHint());
        newGameButton.setOnAction(e -> {
            model.generateRandomBoard();
            updateBoard();
        });
        loadGameButton.setOnAction(e -> loadGame());

        bottomPane.getChildren().add(newGameButton);
        bottomPane.getChildren().add(loadGameButton);
        bottomPane.getChildren().add(hintButton);
        borderpane.setBottom(bottomPane);

        GridPane gridPane = new GridPane();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Button lightButton = new Button(" ");
                lightButton.setMinSize(100, 100);
                lightButton.setStyle("-fx-border-color: grey; -fx-border-width: 2px;");
                final int x = j, y = i;
                lightButton.setOnAction(e -> {
                    model.toggleTile(y, x);
                    updateBoard();
                });
                buttons[i][j] = lightButton;
                gridPane.add(lightButton, i, j);
            }
        }
        borderpane.setCenter(gridPane);

        updateBoard();
        Scene scene = new Scene(borderpane);
        stage.setScene(scene);
        stage.setTitle("Lights Out");
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Updates the board UI based on the current game state.
     * Sets button colors for ON, OFF, and increase the number of moves.
     */
    private void updateBoard() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Tile tile = model.getTile(i, j);
                Button button = buttons[i][j];
                if (tile.isOn()) {
                    button.setStyle("-fx-background-color: default; -fx-border-color: grey; -fx-border-width: 2px;");
                } else {
                    button.setStyle("-fx-background-color: black; -fx-border-color: grey; -fx-border-width: 2px;");
                }
            }
        }
        movesLabel.setText("Moves: " + model.getMoves());
    }

    /**
     * Gets a filename from the user and attempts to load the file.
     *
     * @return true iff the game was loaded successfully
     */
    private void loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a game board.");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/boards"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.lob"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null && model.loadBoardFromFile(selectedFile)) {
            updateBoard();
        } else {
            messageLabel.setText("Failed to load file.");
        }
    }

    /**
     * Updates the UI when the model state changes.
     * Displays hints by highlighting a suggested move in yellow.
     *
     * @param model The LightsOutModel instance.
     * @param message The update message.
     */
    @Override
    public void update(LightsOutModel model, String message) {
        messageLabel.setText(message);
        if (message.startsWith(LightsOutModel.HINT_PREFIX)) {
            String[] parts = message.split(" ");
            int hintX = Integer.parseInt(parts[1].replace(",", ""));
            int hintY = Integer.parseInt(parts[2]);
            buttons[hintX][hintY].setStyle("-fx-background-color: yellow; -fx-border-color: grey; -fx-border-width: 2px;");
        } else {
            updateBoard();
        }
    }

    /**
     * Runs the GUI for Lights Outs
     * @param args cmd line args
     */
    public static void main(String[] args) {
        launch(args);
    }
}