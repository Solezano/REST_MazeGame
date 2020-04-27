package UI;

import Model.Maze;

import java.util.Arrays;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 *
 * Class that provides all print-to-screen methods. Examples being printing maze, score, win/loss scenarios.
 */


public class PrintUI {

    public static void printCompleteMaze(Maze maze) {
        Character[][] completeMaze = maze.getCompleteMaze();
        System.out.println("Maze:");
        System.out.println(Arrays.deepToString(completeMaze).replace("], ", "\n").replace("[", "").replace(",", "").replace("]", ""));
    }

    public static void printPlayerMaze(Maze maze) {
        Character[][] playerMaze = maze.getPlayerMaze();
        System.out.println("Maze:");
        System.out.println(Arrays.deepToString(playerMaze).replace("], ", "\n").replace("[", "").replace(",", "").replace("]", ""));
    }

    public static void printWin(Maze maze, int numberOfCheese, int scoreToWin) {
        System.out.println("Congratulations, you win!");
        printCompleteMaze(maze);
        printCurrentScore(numberOfCheese, scoreToWin);

    }

    public static void printLose(Maze maze, int numberOfCheese, int scoreToWin) {
        System.out.println("Sorry, you have been eaten!");
        printCompleteMaze(maze);
        printCurrentScore(numberOfCheese, scoreToWin);
        System.out.println("GAME OVER, please try again.");
    }

    public static void introMenu() {
        System.out.println("Welcome to Cat and Mouse Maze Adventure!\nby Brandon Yip & Cole Stankov");
    }

    public static void Legend() {
        System.out.println("DIRECTIONS:");
        System.out.println("\t\tFind 5 cheese before a cat eats you!");
        System.out.println("LEGEND:");
        System.out.println("\t\t#: Wall\n\t\t@: You (a mouse)\n\t\t!: Cat\n\t\t$: Cheese\n\t\t.: Unexplored space");
        System.out.println("MOVES:\n\t\tUse W (up), A (left), S (down), D (right) to move.");
        System.out.println("\t\t(You must press enter after each move).");

    }

    public static void printCurrentScore(int numberOfCheese, int scoreToWin) {
        System.out.println("Cheese collected: " + numberOfCheese + " of " + scoreToWin);
    }

    public static void askUserForInput() {
        System.out.printf("Enter your move [WASD]: ");
    }

    public static void invalidInput() {
        System.out.println("Invalid move. Please enter W (up), S (down), A (left), or D (right).");
    }

    public static void walkedIntoWall() {
        System.out.println("Invalid move: you cannot move through walls!");
    }

    public static void printWinCondition(int numberOfCheese, int scoreToWin) {
        System.out.println("Score needed to win: " + scoreToWin);
        printCurrentScore(numberOfCheese, scoreToWin);
    }
}