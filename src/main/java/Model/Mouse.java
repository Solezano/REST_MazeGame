package Model;
import static UI.PrintUI.*;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 *
 * Mouse class provides methods that enables player movement based on inputs given (any valid command in general will
 * provide unique action). Additionally, class will provide functionality for when player has come into contact with
 * dangerous NPC's (cats), or cheese. Based on what cases occur, class will update various aspects of game accordingly
 * (death, score, etc.).
 */


public class Mouse implements Animal {
    private int cheeseCounter = 0;
    private int winCondition = 5;
    private int finishEasily = 1;

    private char up = 'w';
    private char down = 's';
    private char left = 'a';
    private char right = 'd';
    private char wall = '#';
    private char mapHack = 'm';
    private char cheat = 'c';
    private char help = '?';

    Coordinate currentMouseLocation;


    public Mouse(int x_coordinate, int y_coordinate) {
        this.currentMouseLocation = new Coordinate(x_coordinate, y_coordinate);
    }

    public boolean moveDirection(char userInput, Maze maze) {
        boolean validCommand = true;
        int currentRow = 0;
        int currentCol = 0;

        if (userInput == up) {
            currentRow = currentMouseLocation.getRow();
            currentCol = currentMouseLocation.getCol();
            Coordinate newMouseCoordinate = new Coordinate((currentRow - 1), currentCol);

            if (!checkForWalls(newMouseCoordinate, maze)) {
                maze.updateMouseLocation_inMazes(currentMouseLocation, newMouseCoordinate);
                currentMouseLocation = newMouseCoordinate;
            } else {
                walkedIntoWall();
                validCommand = false;
            }

        } else if (userInput == left) {
            currentRow = currentMouseLocation.getRow();
            currentCol = currentMouseLocation.getCol();
            Coordinate newMouseCoordinate = new Coordinate(currentRow, (currentCol - 1));

            if (!checkForWalls(newMouseCoordinate, maze)) {
                maze.updateMouseLocation_inMazes(currentMouseLocation, newMouseCoordinate);
                currentMouseLocation = newMouseCoordinate;
            } else {
                walkedIntoWall();
                validCommand = false;
            }

        } else if (userInput == down) {
            currentRow = currentMouseLocation.getRow();
            currentCol = currentMouseLocation.getCol();
            Coordinate newMouseCoordinate = new Coordinate((currentRow + 1), currentCol);

            if (!checkForWalls(newMouseCoordinate, maze)) {
                maze.updateMouseLocation_inMazes(currentMouseLocation, newMouseCoordinate);
                currentMouseLocation = newMouseCoordinate;
            } else {
                walkedIntoWall();
                validCommand = false;
            }

        } else if (userInput == right) {
            currentRow = currentMouseLocation.getRow();
            currentCol = currentMouseLocation.getCol();
            Coordinate newMouseCoordinate = new Coordinate(currentRow, (currentCol + 1));

            if (!checkForWalls(newMouseCoordinate, maze)) {
                maze.updateMouseLocation_inMazes(currentMouseLocation, newMouseCoordinate);
                currentMouseLocation = newMouseCoordinate;
            } else {
                walkedIntoWall();
                validCommand = false;
            }

        } else {
            if (userInput == mapHack) {
                printCompleteMaze(maze);
            } else if (userInput == cheat) {
                setWinCondition(finishEasily);
                printWinCondition(this.getCheeseCounter(), this.getWinCondition());
            } else if (userInput == help) {
                Legend();
            } else {
                invalidInput();
            }
            validCommand = false;
        }
        return validCommand;
    }

    public boolean atCat(Coordinate currentCatCoordinate) {
        boolean playerDies = false;
        int currentCatRow = currentCatCoordinate.getRow();
        int currentCatCol = currentCatCoordinate.getCol();
        int previousCatRow = currentCatCoordinate.getRow();
        int previousCatCol = currentCatCoordinate.getCol();

        int mouseRow = currentMouseLocation.getRow();
        int mouseCol = currentMouseLocation.getCol();

        if (mouseRow == currentCatRow && mouseCol == currentCatCol) {
            playerDies = true;
        } else if (mouseRow == previousCatRow && mouseCol == previousCatCol) {
            playerDies = true;
        }
        return playerDies;
    }

    public boolean atCheese(Coordinate cheesePos) {
        boolean atCheese = false;
        int cheeseRow = cheesePos.getRow();
        int cheeseCol = cheesePos.getCol();

        int mouseRow = currentMouseLocation.getRow();
        int mouseCol = currentMouseLocation.getCol();

        if (mouseRow == cheeseRow && mouseCol == cheeseCol) {
            atCheese = true;
        }
        return atCheese;
    }

    public boolean checkForWalls(Coordinate newMouseDirection, Maze maze) {
        int currentRow = newMouseDirection.getRow();
        int currentCol = newMouseDirection.getCol();
        boolean isWall = false;
        Character[][] completeMaze = maze.getCompleteMaze();

        if (completeMaze[currentRow][currentCol] == wall) {
            isWall = true;
        }
        return isWall;
    }

    public Coordinate getLocation() {
        return currentMouseLocation;
    }

    public void setWinCondition(int scoreToWin) {
        this.winCondition = scoreToWin;
    }

    public int getWinCondition() {
        return winCondition;
    }

    public void setCheeseCounter(int numberOfCheese) {
        this.cheeseCounter = numberOfCheese;
    }

    public int getCheeseCounter() {
        return cheeseCounter;
    }
}