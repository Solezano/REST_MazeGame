package Controller;

import Model.Maze;
import Model.Mouse;

public class ApiGameWrapper {
    public int numCheeseFound;
    public int numCheeseGoal;
    public boolean isGameLost;
    public boolean isGameWon;
    public int gameNumber;


    public static ApiGameWrapper makeFromGame(Maze game, int id, Mouse mouse) {
        ApiGameWrapper wrapper = new ApiGameWrapper();
        wrapper.numCheeseFound = mouse.getCheeseCounter();
        wrapper.numCheeseGoal = mouse.getWinCondition();
        wrapper.isGameLost = game.getPlayerLoses();
        wrapper.isGameWon = game.getPlayerWins();
        wrapper.gameNumber = id;
        return wrapper;
    }
}
