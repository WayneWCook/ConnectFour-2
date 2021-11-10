/* ConnectFour.java
 * Wayne Cook
 * 5 October 2020
 * Sample interface for Connect Four based on Luke Langius' Tic Tac Toe Board
 * Modified: 6 December 2020
 *      Cleaned up code to better explain how to use files for storing user information.
 * Modified 10 December 2020
 *      Force this to be the copy saved on the cloud.
 * Modified 11 October 2021
 *      Added Rules print section.
 */

import java.io.*;                               // Import needed classes for writing to a file
import java.util.ArrayList;                     // Create a list of variable size
import java.util.Optional;
import java.util.Scanner;                       // Import needed classes for reading files

// JavaFX libraries
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

// Now import the color palette.
import static javafx.scene.paint.Color.BLACK;

public class ConnectFour extends Application {
    private char whoseTurn = 'R';                                                                                           // indicates whose turn it is

    private Cell[][] cell = new Cell[6][7];                                                                                // creates an 2x2 array for the board

    private Label playerStats = new Label("Player's turn Here");                                                             // creates a player label at the bottom of the screen which says whose turn it is
    private Label gameStats = new Label("Game Stats Here");
    private String fileBase = "connectFour",
            fileMain = fileBase + ".txt",
            fileBack = fileBase + ".bak";
    private Players[] players = new Players[2];
    private BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    // File I/O
    private PrintWriter output;
    private Scanner input;

    // Colors for ellipses
    private Paint paint;

    // Create the application.
    @Override
    // overrides the start method that is in the Application class
    public void start(Stage primaryStage) throws IOException {
        GridPane pane = new GridPane();                                 // creating new pane to hold each cell
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                pane.add(cell[i][j] = new Cell(i, j), j, i);            // adding each cell to the GridPane
        BorderPane borderPane = new BorderPane();                                                                           // creating a BorderPane which will hold the "player" label
        borderPane.setCenter(pane);                                                                                         // setting the BorderPane to be in the center of the GridPane
        VBox bottomLabels = new VBox();
        bottomLabels.getChildren().addAll(playerStats, gameStats);
        borderPane.setBottom(bottomLabels);                                                                                       // setting the "player" label to be at the bottom of the screen
        //Creating file menu
        Menu file = new Menu("File");
        //Creating file menu items
        MenuItem item1 = new MenuItem("New Game");
        MenuItem item2 = new MenuItem("Statistics");
        MenuItem item3 = new MenuItem("Rules");
        MenuItem item4 = new MenuItem("New Players");
        MenuItem item5 = new MenuItem("Exit");
        //Adding all the menu items to the file menu
        file.getItems().addAll(item1, item2, item3, item4, item5);
        //Creating Help menu
        Menu help = new Menu("Help");
        //Creating Help menu items
        MenuItem item6 = new MenuItem("About");
        //Adding all the menu items to the Help
        help.getItems().addAll(item6);
        //Creating a menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.setTranslateX(200);
        menuBar.setTranslateY(20);
        //Adding all the menus to the menu bar
        menuBar.getMenus().addAll(file, help);
        // Setting the action for the menu items.
        //Setting action to exit menu item
        item1.setOnAction((ActionEvent t) -> {
            clearBoard();
            //handleAddPlayers(primaryStage);
        });
        item2.setOnAction((ActionEvent t) -> {
            try {
                handlePrintStatistics(primaryStage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        item3.setOnAction((ActionEvent t) -> {
            handlePrintRules(primaryStage);
        });
        item4.setOnAction((ActionEvent t) -> {
            clearBoard();
            handleAddPlayers(primaryStage);
        });
        item5.setOnAction((ActionEvent t) -> {
            handleGameExit(primaryStage);
        });
        item6.setOnAction((ActionEvent t) -> {
            handlePrintRules(primaryStage);
        });
        //Setting the menubar into a group to post across the top of the tool.
        Group menuGroup = new Group(menuBar);
        borderPane.setTop(menuGroup);
        // Setting up the Scene and Stage to display both.
        Scene scene = new Scene(borderPane, 640, 640);    // creating scene and adding BorderPane to it
        primaryStage.setTitle("Connect Four");                 // setting the stage title to "TicTacToe"
        primaryStage.setScene(scene);                          // placing the scene in the stage
        primaryStage.show();
        handleAddPlayers(primaryStage);
    }

    /* The reason I am using a vector here is to allow a variable number of Strings to be returned.
     * A line may have different number of Strings separated by spaces or other delimeters.
     */
    void splitLine(ArrayList<String> splitList, String inString, char delim) {
        int len = inString.length();
        int j = 0, k = 0;
        splitList.clear();
        for (int i = 0; i < len; i++) {
            if (inString.charAt(i) == delim) {
                String elem = inString.substring(j, i);
                System.out.println("Element " + k++ + " is " + elem);
                splitList.add(elem);
                j = i + 1;
            }
            if (i == len - 1) {
                String elem = inString.substring(j);
                System.out.println("Element " + k++ + " is " + elem);
                splitList.add(elem);
            }
        }
    }

    // Final statistics not in dialog box.
    private void printStatistics() {
        System.out.println("Name\tGames\tWins\tLosses\tTies");
        String gLab = "Name\tGames\tWins\tLosses\tTies\n";
        for (int i = 0; i < 2; i++) {
            gLab += players[i].name + " " + players[i].games + " " + players[i].wins + " " +
                    players[i].losses + " " + players[i].ties + " ";
            System.out.println(players[i].name + "\t" + players[i].games + "\t" + players[i].wins + "\t" +
                    players[i].losses + "\t" + players[i].ties);
        }
        gameStats.setText(gLab);
    }

    private void handlePrintRules(Stage mainStage) {
        System.out.println("Connect 4 Rules");
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.initOwner(mainStage);
        dialog.setTitle("Connect 4 Rules");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Set up Text Area
        String output =
                "Rules for Connect Four\n" +
                "Decide who the two players are.\n" +
                "Enter the name of each player\n" +
                "The first player always goes first\n" +
                "Each player drops their checker into a column\n" +
                "The checker falls to the base of the chosen column\n" +
                "The first player with four checkers in a row wins\n" +
                "the row can be vertical, horizontal, or diagonal.";
        TextArea area = new TextArea(output);
        area.setPrefColumnCount(30);
        area.setPrefRowCount(8);
        ButtonType okButton = new ButtonType("OK");
        gridPane.getChildren().add(area);
        dialog.getDialogPane().setContent(gridPane);
        dialog.getButtonTypes().addAll(okButton);
        //dialog.show();

        // Now take care of the buttons
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == okButton) {
            dialog.close();
        }
    }

    //Handle exiting the program and writing new statistics to file.
    public void handleGameExit(Stage mainStage) {
        File fileIn = new File(fileBack);               // Open created backup file for reading
        File fileOut = new File(fileMain);              // Open main file to save all player stats
        boolean fileHere = true;
        try {
            output = new PrintWriter(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            input = new Scanner(fileIn);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();                      // Keep here for debugging
            fileHere = false;
        }
        while (fileHere && input.hasNext()) {           // If backup file exists, read each line
            output.println(input.nextLine());           // Copy lines to main file
        }
        //printStatistics();
        for (int i = 0; i < 2; i++) {                 // Now load the two players stats
            output.println(players[i].name + ',' + players[i].games + ',' + players[i].wins
                    + ',' + players[i].losses + ',' + players[i].ties);
        }
        input.close();
        output.close();
        System.exit(0);
    }

    // Allow the user to show the current statistics
    public void handlePrintStatistics(Stage mainStage) throws FileNotFoundException {
        File fileIn = new File(fileMain);
        Alert dialog = new Alert(Alert.AlertType.NONE);
        ArrayList<String> splitList = new ArrayList<>();
        dialog.initOwner(mainStage);
        dialog.setTitle("Player Statistics");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Set up Text Area
        String output = "Name\tGames\tWins \tLosses\tTies\n";
        if (fileIn.exists()) {
            input = new Scanner(fileIn);
            String inLine;
            while (input.hasNext()) {
                inLine = input.nextLine();
                splitLine(splitList, inLine, ',');
                String name = splitList.get(0);
                int games = Integer.parseInt(splitList.get(1));
                int wins = Integer.parseInt(splitList.get(2));
                int losses = Integer.parseInt(splitList.get(3));
                int ties = Integer.parseInt(splitList.get(4));
                int len = name.length();
                // Statistics are read from the file above, make sure current statistics are shown.
                for (int i = 0; i < 2; i++) {
                    if (name.equals(players[i].name)) {
                        games = players[i].games;
                        wins = players[i].wins;
                        losses = players[i].losses;
                        ties = players[i].ties;
                    }
                }
                output += name + (len < 5 ? "\t\t\t" : len < 9 ? "\t\t" :
                        "\t") + games + "\t\t" + wins + "\t\t"
                        + losses + "\t\t" + ties + "\n";
            }
            input.close();
        }
        TextArea area = new TextArea(output);
        area.setPrefColumnCount(40);
        area.setPrefRowCount(20);
        ButtonType okButton = new ButtonType("OK");
        gridPane.getChildren().add(area);
        dialog.getDialogPane().setContent(gridPane);
        dialog.getButtonTypes().addAll(okButton);
        //dialog.show();

        // Now take care of the buttons
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == okButton) {
            dialog.close();
        }
    }

    // Allow the user to choose the player names
    public void handleAddPlayers(Stage mainStage) {
        Alert dialog = new Alert(Alert.AlertType.NONE);

        dialog.initOwner(mainStage);
        dialog.setTitle("Adding Game Players");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        Label labelA = new Label("Player A: ");
        TextField playerA = new TextField();
        playerA.setPromptText("First Player");
        HBox hBoxA = new HBox();
        hBoxA.setSpacing(10);
        hBoxA.getChildren().addAll(labelA, playerA);
        Label labelB = new Label("Player B: ");
        TextField playerB = new TextField();
        playerB.setPromptText("Second Player");
        HBox hBoxB = new HBox();
        hBoxB.setSpacing(10);
        hBoxB.getChildren().addAll(labelB, playerB);
        HBox hBoxButton = new HBox();
        // Define Buttons
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().addAll(okButton, cancelButton);
        vbox.getChildren().addAll(hBoxA, hBoxB);
        gridPane.add(vbox, 0, 0);
        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> playerA.requestFocus());

        // Now take care of the buttons
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == okButton) {
            boolean hasValues = true;
            players[0] = new Players();
            players[1] = new Players();
            players[0].name = playerA.getText();
            if (players[0].name.length() == 0) hasValues = false;
            players[1].name = playerB.getText();
            if (players[1].name.length() == 0) hasValues = false;
            if (hasValues) {
                try {
                    initPlayers();
                } catch (Exception e) {
                    System.out.println("Cannot initialize players");
                }
            }
            dialog.close();
        } else if (result.get() == cancelButton) {
            dialog.close();
        }
    }

    // initialize players
    private void initPlayers() throws IOException {
        String inLine, name;
        int games = 0, wins = 0, losses = 0, ties = 0;
        ArrayList<String> splitList = new ArrayList<>();
        File fileIn = new File(fileMain);
        File fileOut = new File(fileBack);
        players[0].games = players[1].games = 0;
        players[0].wins = players[1].wins = 0;
        players[0].losses = players[1].losses = 0;
        players[0].ties = players[1].ties = 0;
        if (fileIn.exists()) {
            output = new PrintWriter(fileOut);
            input = new Scanner(fileIn);
            while (input.hasNext()) {
                boolean notFound = true;
                inLine = input.nextLine();
                splitLine(splitList, inLine, ',');
                name = splitList.get(0);
                games = Integer.parseInt(splitList.get(1));
                wins = Integer.parseInt(splitList.get(2));
                losses = Integer.parseInt(splitList.get(3));
                ties = Integer.parseInt(splitList.get(4));
                for (int i = 0; i < 2; i++) {
                    if (name.equals(players[i].name)) {
                        players[i].games = games;
                        players[i].wins = wins;
                        players[i].losses = losses;
                        players[i].ties = ties;
                        notFound = false;
                    }
                }
                if (notFound) output.println(inLine);
            }
            output.close();
            input.close();
        }
        playerStats.setText(players[0].name + "'s Turn");
    }

    // Check if board is full
    public boolean isFull() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)                                                                                     // determining if all of the cells occupied
                if (cell[i][j].getToken() == ' ') {
                    return false;
                }
        return true;
    }


    public boolean isWon(char token) {
        Background background;
        if (token == ' ') return false;             // No winners if checking empty item.
        if (token == 'R') background = new Background(new BackgroundFill(Color.ORCHID, null, null));
        else background = new Background(new BackgroundFill(Color.LIGHTCORAL, null, null));
        // Now Start the tests.
        // Check for four in a row horizontally.
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                if (cell[i][j].getToken() == token
                        && cell[i][j + 1].getToken() == token                                                                       // checking to see if a player has won in the horizontally
                        && cell[i][j + 2].getToken() == token
                        && cell[i][j + 3].getToken() == token) {
                    cell[i][j].setBackground(background);
                    cell[i][j + 1].setBackground(background);           // if a player has won in the horizontally, the background color of the cells that won the game will be turned to blue
                    cell[i][j + 2].setBackground(background);
                    cell[i][j + 3].setBackground(background);
                    return true;
                }
            }
        }
        // Check for four in a row vertically.
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < 3; i++) {
                /* Sometimes printing out values is faster than using the debugger.
                if (j == 3) {
                    System.out.println(i + " " + j + " " + "Full Collumn: " +
                            cell[0][3].getToken() + cell[1][j].getToken()
                            + cell[2][j].getToken() + cell[3][j].getToken()
                            + cell[4][j].getToken() + cell[5][j].getToken()
                    + "\nFour Selected: " + cell[i][j].getToken() + cell[i+1][j].getToken()
                    + cell[i+2][j].getToken() + cell[i+3][j].getToken());
                }*/
                if (cell[i][j].getToken() == token
                        && cell[i + 1][j].getToken() == token                                                                       // checking to see if a player has won in the vertically
                        && cell[i + 2][j].getToken() == token                                                                       // checking to see if a player has won in the vertically
                        && cell[i + 3][j].getToken() == token) {
                    cell[i][j].setBackground(background);
                    cell[i + 1][j].setBackground(background);          // if a player has won in the vertically, the background color
                    cell[i + 2][j].setBackground(background);          //  of the cells that won the game will be turned to green
                    cell[i + 3][j].setBackground(background);
                    return true;
                }
            }
        }
        // Check for four in a row up and to the right.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (cell[i][j].getToken() == token
                        && cell[i + 1][j + 1].getToken() == token                                                                       // checking to see if a player has won in the horizontally
                        && cell[i + 2][j + 2].getToken() == token
                        && cell[i + 3][j + 3].getToken() == token) {
                    cell[i][j].setBackground(background);
                    cell[i + 1][j + 1].setBackground(background);         // if a player has won in the horizontally, the background color of the cells that won the game will be turned to blue
                    cell[i + 2][j + 2].setBackground(background);
                    cell[i + 3][j + 3].setBackground(background);
                    return true;
                }
            }
        }
        // Check for four in a row down and to the right.
        for (int i = 5; i >= 3; i--) {
            for (int j = 0; j <= 3; j++) {
                /* Sometimes printing out values is faster than using the debugger.
                    System.out.println(i + " " + j + " " + "Four Selected: " +cell[i][j].getToken()
                            + cell[i-1][j + 1].getToken()                                                                     // checking to see if a player has won in the horizontally
                            + cell[i-2][j + 2].getToken()
                            + cell[i-3][j + 3].getToken() ); */
                if (cell[i][j].getToken() == token
                        && cell[i - 1][j + 1].getToken() == token                                                                       // checking to see if a player has won in the horizontally
                        && cell[i - 2][j + 2].getToken() == token
                        && cell[i - 3][j + 3].getToken() == token) {
                    cell[i][j].setBackground(background);
                    cell[i - 1][j + 1].setBackground(background);           // if a player has won in the horizontally, the background color of the cells that won the game will be turned to blue
                    cell[i - 2][j + 2].setBackground(background);
                    cell[i - 3][j + 3].setBackground(background);
                    return true;
                }
            }
        }
        // If nothing found then exit with false.
        return false;
    }

    // Clear the board
    public void clearBoard() {
        Background background = new Background(new BackgroundFill(Color.WHEAT, null, null));
        for (int i = 5; i >= 0; i--) {
            for (int j = 6; j >= 0; j--) {
                cell[i][j].setToken(' ', false);
                cell[i][j].setBackground(background);
            }
        }
        whoseTurn = 'R';
    }

    // Define Cells for the board
    public class Cell extends Pane {                                                                                        // creating the inner class for cell
        private char token = ' ';                                                                                           // setting "token"  equal to nothing for this cell because nothing is currently in the cell
        private int xVal;
        private int yVal;
        Ellipse ellipse;

        public Cell(int x, int y) {
            xVal = x;
            yVal = y;
            setStyle("-fx-border-color: black");                                                                            // setting border of cell to be black
            this.setPrefSize(800, 800);                                                                // setting size of cell to 800x800
            ellipse = new Ellipse(this.getWidth() / 2,
                    this.getHeight() / 2, this.getWidth() / 2 - 10,                                     // creating the ellipse that will represent the "O"
                    this.getHeight() / 2 - 10);
            ellipse.centerXProperty().bind(
                    this.widthProperty().divide(2));
            ellipse.centerYProperty().bind(
                    this.heightProperty().divide(2));
            ellipse.radiusXProperty().bind(
                    this.widthProperty().divide(2).subtract(10));
            ellipse.radiusYProperty().bind(
                    this.heightProperty().divide(2).subtract(10));
            ellipse.setStrokeWidth(5);                                                                                  // setting the width of the "O" to "5"
            ellipse.setStroke(BLACK);                                                                                   // setting the outside of the "O" to be black
            ellipse.setFill(Color.TRANSPARENT);                                                                         // setting the inside of the "O" to be transparent
            this.getChildren().add(ellipse);
            this.setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
            this.setOnMouseClicked(e -> handleMouseClick());                                                                // handling what happens to the cell when the mouse is clicked
        }

        public char getToken() {
            return token;                                                                        // returning "token"
        }


        public void setToken(char c, boolean callBack) {
            token = c;                                                                           // setting a new "token"
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(400));
            long start;
            // Go through the process of dropping the checker
            if (token == ' ') {
                paint = Color.TRANSPARENT;
            } else if (token == 'R') {                                                             // creating an "X"
                paint = Color.RED;
            } else if (token == 'B') {
                paint = Color.YELLOW;
            }
            ellipse.setFill(paint);
            if (callBack) cell[xVal - 1][yVal].setToken(' ', false);
            if ((xVal < 5) && (cell[xVal + 1][yVal].getToken() == ' ')) {
                // start = System.currentTimeMillis() + 500;
                //while (System.currentTimeMillis() < start) ;
                if (token != ' ') {
                    pauseTransition.setOnFinished(event -> cell[xVal + 1][yVal].setToken(token, true));
                    pauseTransition.play();
                }
            } else isWon(token);
        }


        private void handleMouseClick() {                                                                                   // creating the method to handle what happens when the mouse gets clicked
            if (token == ' ' && whoseTurn != ' ') {                                                                         // if the cell is empty and game is not over,
                setToken(whoseTurn, false);                                                                                        // set the desired token into the cell that was clicked
                if (isWon(whoseTurn)) {                                                                                     // checking the game status using the "isWon" method
                    playerStats.setText((whoseTurn == 'R' ? players[0].name : players[1].name) +
                            " won the game! Congratulations");                  // if game is won, displays message saying "Congratulations"
                    players[0].games++;
                    players[1].games++;
                    if (whoseTurn == 'R') {
                        players[0].wins++;
                        players[1].losses++;
                    } else {
                        players[1].wins++;
                        players[0].losses++;
                    }
                    whoseTurn = ' ';                                                                                        // sets the "whichTurn" variable to empty making the game stop
                } else if (isFull()) {                                                                                        // checking the game status using the "isFull" method
                    playerStats.setText("Draw! The game is over");                                                               // if the game is full, then displays message saying "Draw"
                    players[0].games++;
                    players[1].games++;
                    players[0].ties++;
                    players[1].ties++;
                    whoseTurn = ' ';                                                                                        // sets the "whichTurn" variable to empty making the game stop
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (cell[i][j].getToken() != ' ') {                                                             // if all cells are not empty, then make the background of all cells turn to teal, thus clarifying that it is a draw
                                cell[i][j].setBackground(new Background(new BackgroundFill(Color.TEAL, null, null)));
                            }
                        }
                    }

                } else {

                    whoseTurn = (whoseTurn == 'R') ? 'B' : 'R';                                                             // changes the turn if the game is still going

                    playerStats.setText((whoseTurn == 'R' ? players[0].name : players[1].name)
                            + " Player's turn");                                                      // displays whose turn it is
                }
                //printStatistics();
            }
        }

    }

    // Put in a place for starting the program.
    public static void main(String[] args) {
        launch(args);                                                                                                       // launching the entire game
    }

}
