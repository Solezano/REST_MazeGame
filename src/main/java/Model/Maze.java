package Model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 * The maze class generates the maze (both as a fully walled grid and utilizing depth-first search algorithm).
 * Additionally updates both player and NPC (non-playable character; cheese, cats) locations in maze.
 */

public class Maze {
    public final int FIRST_ACCESSIBLE_ROW = 1;
    public final int LAST_ACCESSIBLE_ROW = 13;
    public final int FIRST_ACCESSIBLE_COL = 1;
    public final int LAST_ACCESSIBLE_COL = 18;
    private final int ROWS = 15;
    private final int COLS = 20;
    private final int zeroIndexedROW = ROWS - 1;
    private final int zeroIndexedCOL = COLS - 1;
    private boolean playerLoses = false;
    private boolean playerWins = false;
    private Coordinate cheeseLocation;
    private char availableSpace = ' ';
    private char deadPlayer = 'X';
    private char hiddenCell = '.';
    private char cheese = '$';
    private char player = '@';
    private char catNPC = '!';
    private char wall = '#';

    private Mouse mouseObject;
    private Cat topRight_Cat;
    private Cat bottomLeft_Cat;
    private Cat bottomRight_Cat;

    private Map<Coordinate, Character> mapForCompleteMaze = new HashMap<>();
    private Map<Coordinate, Boolean> visibleCellsToPlayer = new HashMap<>();
    private Map<Coordinate, Boolean> checkIfVisited = new HashMap<>();

    private boolean[][] cellsThatHaveWallsForWrapper = new boolean[ROWS][COLS];
    private boolean[][] visibleCellsForWrapper = new boolean[ROWS][COLS];
    private Character[][] completeMaze = new Character[ROWS][COLS];
    private Character[][] playerMaze = new Character[ROWS][COLS];

    private List<Coordinate> listOfAllCoordinates = new ArrayList<>();
    private List<Cat> all_Cats = new ArrayList<>();


    public Maze() {
        this.mazeGenerate();
        depthFirstSearch();
        checkLastColumn();
        initializeCellsThatHaveWallsForWrapper(cellsThatHaveWallsForWrapper);
        updateVisibleCellsForWrapper(visibleCellsForWrapper);
        mouseObject = new Mouse(1, 1);
        createCats();
    }

    public void mazeGenerate() {
        Coordinate newCoordinate;
        int currentRow = 0;
        int currentCol = 0;

        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                cellsThatHaveWallsForWrapper[currentRow][currentCol] = false;
                visibleCellsForWrapper[currentRow][currentCol] = false;
                completeMaze[currentRow][currentCol] = wall;

                newCoordinate = new Coordinate(currentRow, currentCol);
                visibleCellsToPlayer.put(newCoordinate, false);
                mapForCompleteMaze.put(newCoordinate, wall);
                checkIfVisited.put(newCoordinate, false);
                listOfAllCoordinates.add(newCoordinate);
                currentCol++;
            }
            currentRow++;
            currentCol = 0;
        }
    }

    private void playerMazeGenerate() {
        int currentRow = 0;
        int currentCol = 0;
        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                char characterAtIndex = completeMaze[currentRow][currentCol];
                if (completeMaze[currentRow][currentCol] == player || completeMaze[currentRow][currentCol] == catNPC) {
                    playerMaze[currentRow][currentCol] = characterAtIndex;
                } else if (currentRow == 0 || currentCol == 0) {
                    playerMaze[currentRow][currentCol] = wall;
                } else if (currentRow == zeroIndexedROW || currentCol == zeroIndexedCOL) {
                    playerMaze[currentRow][currentCol] = wall;
                } else {
                    playerMaze[currentRow][currentCol] = hiddenCell;
                }
                currentCol++;

            }
            currentRow++;
            currentCol = 0;
        }
    }

    public void depthFirstSearch() {
        List<Coordinate> listOfCoordinates = new ArrayList<>();
        Coordinate initialCell = new Coordinate(1, 1);
        Stack<Coordinate> DFS_Stack = new Stack();
        char directionFromCurrentCell = ' ';
        int deleteWallAtXCoordinate = 0;
        int deleteWallAtYCoordinate = 0;
        int currentXCoordinate = 0;
        int currentYCoordinate = 0;
        int adjacentCellX = 0;
        int adjacentCellY = 0;
        int directionToGo = 0;

        DFS_Stack.push(initialCell);

        while (!DFS_Stack.empty()) {
            Coordinate currentCoordinate = DFS_Stack.pop();
            currentXCoordinate = currentCoordinate.getRow();
            currentYCoordinate = currentCoordinate.getCol();

            if ((currentXCoordinate - 2) >= 1) {
                Coordinate up = findCoordinate((currentXCoordinate - 2), currentYCoordinate);
                if (checkIfVisited.get(up) != true) {
                    up.setDirection('N');
                    listOfCoordinates.add(up);
                }
            }
            if ((currentXCoordinate + 2) < zeroIndexedROW) {
                Coordinate down = findCoordinate((currentXCoordinate + 2), currentYCoordinate);
                if (checkIfVisited.get(down) != true) {
                    down.setDirection('S');
                    listOfCoordinates.add(down);
                }
            }
            if ((currentYCoordinate - 2) >= 1) {
                Coordinate left = findCoordinate(currentXCoordinate, (currentYCoordinate - 2));
                if (checkIfVisited.get(left) != true) {
                    left.setDirection('W');
                    listOfCoordinates.add(left);
                }
            }
            if ((currentYCoordinate + 2) < zeroIndexedCOL) {
                Coordinate right = findCoordinate(currentXCoordinate, (currentYCoordinate + 2));
                if (checkIfVisited.get(right) != true) {
                    right.setDirection('E');
                    listOfCoordinates.add(right);
                }
            }

            if (!listOfCoordinates.isEmpty()) {
                DFS_Stack.push(currentCoordinate);
                directionToGo = ThreadLocalRandom.current().nextInt(0, listOfCoordinates.size());
                Coordinate coordinateOfDirection = listOfCoordinates.get(directionToGo);
                directionFromCurrentCell = coordinateOfDirection.getDirection();
                Coordinate wallToDelete;

                if (directionFromCurrentCell == 'N') {
                    wallToDelete = findCoordinate((currentXCoordinate - 1), currentYCoordinate);
                } else if (directionFromCurrentCell == 'S') {
                    wallToDelete = findCoordinate((currentXCoordinate + 1), currentYCoordinate);
                } else if (directionFromCurrentCell == 'W') {
                    wallToDelete = findCoordinate(currentXCoordinate, (currentYCoordinate - 1));
                } else {
                    wallToDelete = findCoordinate(currentXCoordinate, (currentYCoordinate + 1));
                }

                adjacentCellX = wallToDelete.getRow();
                adjacentCellY = wallToDelete.getCol();
                completeMaze[adjacentCellX][adjacentCellY] = availableSpace;

                deleteWallAtXCoordinate = coordinateOfDirection.getRow();
                deleteWallAtYCoordinate = coordinateOfDirection.getCol();
                completeMaze[deleteWallAtXCoordinate][deleteWallAtYCoordinate] = availableSpace;

                checkIfVisited.put(coordinateOfDirection, true);
                checkIfVisited.put(wallToDelete, true);
                listOfCoordinates.clear();
                DFS_Stack.push(coordinateOfDirection);
            }
        }
    }

    private void checkLastColumn() {
        int colWithCats = zeroIndexedROW - 1;
        int currentCol = zeroIndexedCOL - 1;
        int currentRow = 2;
        int removeIf = 1;
        while (currentRow < colWithCats) {
            if (removeIf % 2 == 0) {
                completeMaze[currentRow][currentCol] = availableSpace;
            }
            removeIf = removeIf + 1;
            currentRow = currentRow + 1;
        }
    }

    private Coordinate findCoordinate(int row, int col) {
        Coordinate cellToBeSearched = new Coordinate(row, col);
        for (Coordinate currentCoordinate : listOfAllCoordinates) {
            if (currentCoordinate.equals(cellToBeSearched)) {
                return currentCoordinate;
            }
        }
        return null;
    }

    public void setPlayerandNPC(Mouse mouse, Cat firstCat, Cat secondCat, Cat thirdCat) {
        List<Coordinate> allCatCoordinates = new ArrayList<>();

        Coordinate currentCoordinate = mouse.getLocation();
        int currentRow = currentCoordinate.getRow();
        int currentCol = currentCoordinate.getCol();
        mapForCompleteMaze.put(findCoordinate(currentRow, currentCol), player);
        completeMaze[currentRow][currentCol] = player;

        allCatCoordinates.add(firstCat.getLocation());
        allCatCoordinates.add(secondCat.getLocation());
        allCatCoordinates.add(thirdCat.getLocation());

        for (Coordinate currentCatCoordinate : allCatCoordinates) {
            setPlayerandNPC_Helper(currentCatCoordinate);
        }
        playerMazeGenerate();
        setCheeseLocation();
    }

    public void setPlayerandNPC_Helper(Coordinate currentCatCoordinate) {
        Coordinate currentCoordinate = currentCatCoordinate;
        int currentRow = currentCoordinate.getRow();
        int currentCol = currentCoordinate.getCol();
        mapForCompleteMaze.put(findCoordinate(currentRow, currentCol), catNPC);
        completeMaze[currentRow][currentCol] = catNPC;
    }

    public void updatePlayerVisibility() {
        int currentRow = 0;
        int currentCol = 0;
        int mouseRow = 0;
        int mouseCol = 0;
        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                if (completeMaze[currentRow][currentCol] == player) {
                    mouseRow = currentRow;
                    mouseCol = currentCol;
                    break;
                }
                currentCol++;
            }
            currentRow++;
            currentCol = 0;
        }

        for (currentRow = mouseRow - 1; currentRow <= mouseRow + 1; currentRow++) {
            for (currentCol = mouseCol - 1; currentCol <= mouseCol + 1; currentCol++) {
                if (currentRow == mouseRow && currentCol == mouseCol) {
                    continue;
                }
                playerMaze[currentRow][currentCol] = completeMaze[currentRow][currentCol];
                Coordinate playerRadius = findCoordinate(currentRow, currentCol);
                visibleCellsToPlayer.put(playerRadius, true);
            }
        }
    }

    public Character[][] getCompleteMaze() {
        return completeMaze;
    }

    public Character[][] getPlayerMaze() {
        return playerMaze;
    }

    public void setPlayerDeathCharacter_inMazes(Coordinate currentMouseCoordinate) {
        int currentRow = currentMouseCoordinate.getRow();
        int col = currentMouseCoordinate.getCol();
        completeMaze[currentRow][col] = deadPlayer;
    }

    public Mouse getMouseObject() {
        return this.mouseObject;
    }

    public void updateMouseLocation_inMazes(Coordinate oldMouseLoc, Coordinate newMouseLoc) {
        int oldMouseRow = oldMouseLoc.getRow();
        int oldMouseCol = oldMouseLoc.getCol();
        int newMouseRow = newMouseLoc.getRow();
        int newMouseCol = newMouseLoc.getCol();
        int currentRow = 0;
        int currentCol = 0;

        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                if (currentRow == newMouseRow && currentCol == newMouseCol) {
                    completeMaze[currentRow][currentCol] = player;
                    playerMaze[currentRow][currentCol] = player;
                }
                if (currentRow == oldMouseRow && currentCol == oldMouseCol) {
                    completeMaze[currentRow][currentCol] = availableSpace;
                    playerMaze[currentRow][currentCol] = availableSpace;
                }
                currentCol++;
            }
            currentRow++;
            currentCol = 0;
        }
        updatePlayerVisibility();
        setMouseLocation(newMouseLoc);
    }

    public void setMouseLocation(Coordinate mouseLocation) {
        this.mouseObject.currentMouseLocation = mouseLocation;
    }

    public Coordinate getMouseLocation() {
        return this.mouseObject.currentMouseLocation;
    }

    public void setPlayerWins(boolean playerWins) {
        this.playerWins = playerWins;
    }

    public boolean getPlayerWins() {
        return playerWins;
    }

    public void setPlayerLoses(boolean playerLoses) {
        this.playerLoses = playerLoses;
    }

    public boolean getPlayerLoses() {
        return playerLoses;
    }

    public void updateCatLocation_inMazes(Coordinate oldCatLoc, Coordinate newCatLoc, Cat cat) {
        int cheeseRow = this.cheeseLocation.getRow();
        int cheeseCol = this.cheeseLocation.getCol();
        int newCurrentCatRow = newCatLoc.getRow();
        int newCurrentCatCol = newCatLoc.getCol();
        int oldCurrentCatRow = oldCatLoc.getRow();
        int oldCurrentCatCol = oldCatLoc.getCol();
        Coordinate oldCatCoordinate;
        int currentRow = 0;
        int currentCol = 0;

        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                if (currentRow == newCurrentCatRow && currentCol == newCurrentCatCol) {
                    cat.setCurrentMapCharacter(completeMaze[currentRow][currentCol]);
                    completeMaze[currentRow][currentCol] = catNPC;
                    playerMaze[currentRow][currentCol] = catNPC;
                }
                if (currentRow == oldCurrentCatRow && currentCol == oldCurrentCatCol) {
                    oldCatCoordinate = findCoordinate(currentRow, currentCol);
                    if (visibleCellsToPlayer.get(oldCatCoordinate) == true) {
                        playerMaze[currentRow][currentCol] = cat.getCurrentMapCharacter();
                        completeMaze[currentRow][currentCol] = cat.getCurrentMapCharacter();
                    } else {
                        playerMaze[currentRow][currentCol] = hiddenCell;
                        completeMaze[currentRow][currentCol] = availableSpace;
                    }
                    if (oldCurrentCatRow == cheeseRow && oldCurrentCatCol == cheeseCol) {
                        playerMaze[currentRow][currentCol] = cheese;
                        completeMaze[currentRow][currentCol] = cheese;
                    }
                }
                currentCol++;
            }
            currentRow++;
            currentCol = 0;
        }
    }

    public List<Cat> getAll_Cats() {
        return all_Cats;
    }

    public void setCheeseLocation() {
        int cheeseRow = 0;
        int cheeseCol = 0;
        while (true) {
            cheeseCol = ThreadLocalRandom.current().nextInt(1, COLS);
            cheeseRow = ThreadLocalRandom.current().nextInt(1, ROWS);
            if (completeMaze[cheeseRow][cheeseCol] == availableSpace) {
                cheeseLocation = findCoordinate(cheeseRow, cheeseCol);
                completeMaze[cheeseRow][cheeseCol] = cheese;
                playerMaze[cheeseRow][cheeseCol] = cheese;
                break;
            }
        }
    }

    public Coordinate getCheeseLocation() {
        return cheeseLocation;
    }

    public int getROWS() {
        return ROWS;
    }

    public int getCOLS() {
        return COLS;
    }


    /*-------------------------------CREATED FOR ASSIGNMENT 4-------------------------------*/

    public void initializeCellsThatHaveWallsForWrapper(boolean[][] cellsThatHaveWallsForWrapper) {
        for (int currentRow = 0; currentRow < ROWS; currentRow++) {
            for (int currentCol = 0; currentCol < COLS; currentCol++) {
                if (completeMaze[currentRow][currentCol] == wall) {
                    cellsThatHaveWallsForWrapper[currentRow][currentCol] = true;
                }
            }
        }
        initializeCellsThatHaveWallsForWrapper_Helper();
    }

    public void initializeCellsThatHaveWallsForWrapper_Helper() {
        cellsThatHaveWallsForWrapper[FIRST_ACCESSIBLE_ROW][LAST_ACCESSIBLE_COL] = false;
        cellsThatHaveWallsForWrapper[LAST_ACCESSIBLE_ROW][FIRST_ACCESSIBLE_COL] = false;
        cellsThatHaveWallsForWrapper[LAST_ACCESSIBLE_ROW][LAST_ACCESSIBLE_COL] = false;
    }

    public boolean[][] getCellsThatHaveWallsForWrapper() {
        return cellsThatHaveWallsForWrapper;
    }

    public void updateVisibleCellsForWrapper(boolean[][] visibleCellsForWrapper) {
        int currentRow = 0;
        int currentCol = 0;
        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                Coordinate currentCoordinate = findCoordinate(currentRow, currentCol);
                if (visibleCellsToPlayer.get(currentCoordinate)) {
                    visibleCellsForWrapper[currentRow][currentCol] = true;
                }
                currentCol++;
            }
            currentRow++;
            currentCol = 0;
        }
    }

    public void updateVisibleCellsForWrapper_ShowAll(boolean[][] visibleCellsForWrapper) {
        int currentRow = 0;
        int currentCol = 0;
        while (currentRow < ROWS) {
            while (currentCol < COLS) {
                visibleCellsForWrapper[currentRow][currentCol] = true;
                currentCol++;
            }
            currentRow++;
            currentCol = 0;
        }
    }

    public boolean[][] getVisibleCellsForWrapper() {
        return visibleCellsForWrapper;
    }

    public void createCats() {
        int TOP_RIGHT_ROW = 1;
        int TOP_RIGHT_COL = 18;
        topRight_Cat = new Cat(TOP_RIGHT_ROW, TOP_RIGHT_COL);
        all_Cats.add(topRight_Cat);

        int BOTTOM_LEFT_ROW = 13;
        int BOTTOM_LEFT_COL = 1;
        bottomLeft_Cat = new Cat(BOTTOM_LEFT_ROW, BOTTOM_LEFT_COL);
        all_Cats.add(bottomLeft_Cat);

        int BOTTOM_RIGHT_ROW = 13;
        int BOTTOM_RIGHT_COL = 18;
        bottomRight_Cat = new Cat(BOTTOM_RIGHT_ROW, BOTTOM_RIGHT_COL);
        all_Cats.add(bottomRight_Cat);
    }

    public boolean isAtCat(Cat topRight_Cat, Cat bottomLeft_Cat, Cat bottomRight_Cat) {
        boolean playerAtCat = false;
        if (this.mouseObject.currentMouseLocation == topRight_Cat.getLocation() ||
                this.mouseObject.currentMouseLocation == bottomLeft_Cat.getLocation() ||
                this.mouseObject.currentMouseLocation == bottomRight_Cat.getLocation()) {
            playerAtCat = true;
        }
        return playerAtCat;
    }

    /*-------------------------------CREATED FOR ASSIGNMENT 4-------------------------------*/
}