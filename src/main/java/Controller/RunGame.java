package Controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import static UI.PrintUI.*;

import java.util.Scanner;

import Model.Mouse;
import Model.Maze;
import Model.Cat;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 * <p>
 * Class provides mainline routine functionality; the maze game itself. Instantiates various player and NPC objects,
 * and manages win/lose/continuation of game cases.
 */

@SpringBootApplication
public class RunGame {

    public static void main(String[] args) {
        SpringApplication.run(RunGame.class, args);
        Maze currentMaze = new Maze();
        int FIRST_ACCESSIBLE_ROW = currentMaze.FIRST_ACCESSIBLE_ROW;
        int LAST_ACCESSIBLE_ROW = currentMaze.LAST_ACCESSIBLE_ROW;
        int FIRST_ACCESSIBLE_COL = currentMaze.FIRST_ACCESSIBLE_COL;
        int LAST_ACCESSIBLE_COL = currentMaze.LAST_ACCESSIBLE_COL;
        Scanner userInput = new Scanner(System.in);
        boolean playerWon = false;
        char inputFromUser = ' ';
        int numberOfCheese = 0;

        Mouse Player = new Mouse(FIRST_ACCESSIBLE_ROW, FIRST_ACCESSIBLE_COL);

        Cat topRight_Cat = new Cat(FIRST_ACCESSIBLE_ROW, LAST_ACCESSIBLE_COL);
        currentMaze.getAll_Cats().add(topRight_Cat);

        Cat bottomLeft_Cat = new Cat(LAST_ACCESSIBLE_ROW, FIRST_ACCESSIBLE_COL);
        currentMaze.getAll_Cats().add(bottomLeft_Cat);

        Cat bottomRight_Cat = new Cat(LAST_ACCESSIBLE_ROW, LAST_ACCESSIBLE_COL);
        currentMaze.getAll_Cats().add(bottomRight_Cat);

        currentMaze.setPlayerandNPC(Player, topRight_Cat, bottomLeft_Cat, bottomRight_Cat);
        currentMaze.updatePlayerVisibility();

        introMenu();
        Legend();

        while (!atCat(Player, topRight_Cat, bottomLeft_Cat, bottomRight_Cat)) {
            printPlayerMaze(currentMaze);
            printCurrentScore(Player.getCheeseCounter(), Player.getWinCondition());
            askUserForInput();
            inputFromUser = Character.toLowerCase(userInput.next().charAt(0));

            while (!Player.moveDirection(inputFromUser, currentMaze)) {
                askUserForInput();
                inputFromUser = Character.toLowerCase(userInput.next().charAt(0));
            }
            if (Player.atCheese(currentMaze.getCheeseLocation())) {
                numberOfCheese = Player.getCheeseCounter();
                Player.setCheeseCounter(numberOfCheese + 1);
                currentMaze.setCheeseLocation();
            }
            if (atCat(Player, topRight_Cat, bottomLeft_Cat, bottomRight_Cat)) {
                break;
            }

            topRight_Cat.moveCatRandomly(currentMaze);
            bottomLeft_Cat.moveCatRandomly(currentMaze);
            bottomRight_Cat.moveCatRandomly(currentMaze);

            if (atCat(Player, topRight_Cat, bottomLeft_Cat, bottomRight_Cat)) {
                break;
            }

            if (Player.getCheeseCounter() >= Player.getWinCondition()) {
                printWin(currentMaze, Player.getCheeseCounter(), Player.getWinCondition());
                playerWon = true;
                currentMaze.setPlayerWins(true);
                currentMaze.setPlayerLoses(false);
                break;
            }
        }

        if (!playerWon) {
            currentMaze.setPlayerDeathCharacter_inMazes(Player.getLocation());
            printLose(currentMaze, Player.getCheeseCounter(), Player.getWinCondition());
            currentMaze.setPlayerLoses(true);
            currentMaze.setPlayerWins(false);
        }
    }

    private static boolean atCat(Mouse Player, Cat topRight_Cat, Cat bottomLeft_Cat, Cat bottomRight_Cat) {
        boolean atCat = false;

        if (Player.atCat(topRight_Cat.getLocation()) || Player.atCat(bottomLeft_Cat.getLocation()) || Player.atCat(bottomRight_Cat.getLocation())) {
            atCat = true;
        }
        return atCat;
    }
}