package ptui;


import model.LightsOutModel;
import model.Observer;
import model.Tile;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A plain text user interface for Lights Out
 */
public class LightsOutPTUI implements Observer<LightsOutModel, String> {
   private static char ONSYMBOL = 'o';
    private static char OFFSYMBOL = '.';
    /* Cool encodings. Fonzies only.
    private static char ONSYMBOL = '●';
    private static char OFFSYMBOL = '○';

     */
    private LightsOutModel model;
    private Scanner in;

/**
 * The Text UI for Lights Out
 */
    public LightsOutPTUI() {
        model = new LightsOutModel();
        model.addObserver(this);
        in = new Scanner( System.in );

    }

    /**
     * Gets a filename from the user and attempts to load the file.
     * @return true iff the game was loaded successfully
     */
    public boolean loadFromFile(){
        boolean ready = false;

        while(!ready){
            System.out.println("Enter a valid file name or type Q to go back.");
            String command =  in.next();
            if (command.equals("q") || command.equals("Q")) {
                System.out.println("going back...");
                return false;
            }
            ready = model.loadBoardFromFile(command);


        }
        return true;
    }

    /**
     * Loads a new game or generates a random one.
     * @return True if the user starts a game; False if the user quits
     */
    public boolean gameStart(){

        boolean ready = false;
        while(!ready){
            System.out.println("(R)andom Board. (L)oad a board. (Q)uit");
            String command =  in.next(); // Using next allows you to string together load commands like l boards/1.lob.
            switch (command){
                case "R":
                case "r":
                    model.generateRandomBoard();
                    ready=true;
                    break;
                case "L":
                case "l":
                    ready = loadFromFile();
                    break;
                case "Q":
                case "q":
                    System.out.println("Exiting");
                    ready = true;
                    in = new Scanner(System.in);//get rid of any remaining commands from the start menu
                    return false;

                default:
                    System.out.println("Enter R, L, or Q.");
            }
        }
        in = new Scanner(System.in);//get rid of any remaining commands from the start menu
        return true;
    }

    /**
     * iterates through the tiles and displays them
     */
    public void displayBoard(){
        //formatting for text ui is soooo elegant
        //System.out.print("\033[0;4m"); //turn on underline

        //prints the column number
        System.out.print("  ");
        for(int c =0; c<model.getDimension(); c++){
            System.out.print(c+" ");
        }
        //System.out.print("\033[0;0m"); //turn off underline
        int currentRow = -1;

        //prints the tiles
        for(Tile t : model){
            if (currentRow!=t.getY()){ //newline for new rows.
                currentRow=t.getY();
                System.out.printf("%n%d ",currentRow);

            }
            char symbol = OFFSYMBOL;
            if (t.isOn()) {
                symbol= ONSYMBOL;
            }
            System.out.print(symbol+" ");

        }

        System.out.printf("\nTotal Moves: %d\n",model.getMoves());
      //  System.out.println();
    }

    /**
     * The main program loop. Keeps getting user input until
     */
    public void run() {
        while (true) {
            if (!gameStart()) //loads new games or quits
                break;
            gameLoop(); // gameplay
        }

    }

    /**
     * Handles the actual game play. Gets user input, toggles tiles, and asks for hints. Checks if the game is over and drops back to the main menu if it is.
     */
    private void gameLoop(){
        String msg;

        while(true) {
            msg = "";
            System.out.println("Enter X Y to toggle a tile, (H)int, or (Q)uit to main menu");
            String command = in.nextLine().strip();
            if (command.equals("q") || command.equals("Q")) {
                System.out.println("Quitting to main menu.");
                break;
            } else if(command.equals("h")||command.equals("H")){
                model.getHint();
                
               // model.toggleTile(hint.getX(),hint.getY());

            } else {
                try {
                    Scanner s = new Scanner(command);
                    int x = s.nextInt();
                    int y = s.nextInt();
                    model.toggleTile(x, y);

                } catch (InputMismatchException e) {

                    msg = "X and Y must be integers";
                } catch (NoSuchElementException e) {

                    msg = "Must enter X and Y on one line.";
                } catch (IndexOutOfBoundsException e) {
                    msg = String.format("X and Y should be between 0 and %d", model.getDimension());
                }
            }


            if (!msg.isEmpty())
                System.out.println("Command: "+command+"\n\033[0;1m***"+msg+"***\033[0;0m");

            if (model.gameOver()) {
                break;
            }
        }
    }

    @Override
    public void update(LightsOutModel model, String msg) {
        System.out.println(msg);
        if (!model.gameOver()) {
            displayBoard(); // renders the board
        }
    }
    
    /**
     * Runs the Text UI for Lights Outs
     * @param args cmd line args
     */
    public static void main(String[] args){
        LightsOutPTUI ui = new LightsOutPTUI();
        ui.run();
    }
}
