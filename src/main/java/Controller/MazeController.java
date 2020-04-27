package Controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.Mouse;
import Model.Maze;
import Model.Cat;

/**
 * Brandon Yip 301294186
 * This class provides functionality between REST API requests (GET/POST) to corresponding functions to provide 'wrapper'
 * classes with correct data to fully populate the maze on localhost. Returns specific exceptions in different cases;
 * check below code.
 */


@RestController
public class MazeController {
    public enum INPUT {BAD_INPUT, PLAYER_MOVED, ONLY_CATS_MOVED, MOVED_INTO_WALL, ONE_CHEESE, SHOW_ALL}

    List<ApiBoardWrapper> boardWrapper_Mazes = new ArrayList<>();
    List<ApiGameWrapper> gameWrapper_Mazes = new ArrayList<>();
    List<Maze> regular_Mazes = new ArrayList<>();
    AtomicInteger gameID = new AtomicInteger(0);


    @ResponseStatus(value = HttpStatus.OK) //200
    @GetMapping("/api/about")
    public String getName() {
        return "Brandon Yip";
    }

    @ResponseStatus(value = HttpStatus.OK) //200
    @GetMapping("/api/games")
    public List<ApiGameWrapper> getGames() {
        return gameWrapper_Mazes;
    }

    @ResponseStatus(value = HttpStatus.CREATED) //201
    @PostMapping("/api/games")
    public ApiGameWrapper createGame() {
        ApiGameWrapper currentGameWrapper;
        ApiBoardWrapper currentBoardWrapper;

        Maze currentMaze = new Maze();
        Mouse player = currentMaze.getMouseObject();

        List<Cat> allCats = currentMaze.getAll_Cats();
        currentMaze.setPlayerandNPC(player, allCats.get(0), allCats.get(1), allCats.get(2));
        currentMaze.updatePlayerVisibility();
        currentMaze.updateVisibleCellsForWrapper(currentMaze.getVisibleCellsForWrapper());
        regular_Mazes.add(currentMaze);

        currentBoardWrapper = ApiBoardWrapper.makeFromGame(currentMaze);
        boardWrapper_Mazes.add(currentBoardWrapper);
        currentGameWrapper = ApiGameWrapper.makeFromGame(currentMaze, gameID.getAndIncrement(), player);
        gameWrapper_Mazes.add(currentGameWrapper);
        return currentGameWrapper;
    }

    @ResponseStatus(value = HttpStatus.OK) //200
    @GetMapping("/api/games/{gameID}")
    public ApiGameWrapper getCurrentGame(@PathVariable("gameID") int gameID) {
        for (ApiGameWrapper currentGameWrapperMaze : gameWrapper_Mazes) {
            if (currentGameWrapperMaze.gameNumber == gameID) {
                return currentGameWrapperMaze;
            }
        }
        throw new IndexOutOfBoundsException(); //404
    }

    @ResponseStatus(value = HttpStatus.OK) //200
    @GetMapping("/api/games/{gameID}/board")
    public ApiBoardWrapper getBoardOfGame(@PathVariable("gameID") int gameID) {
        for (ApiGameWrapper currentGameWrapperMaze : gameWrapper_Mazes) {
            if (currentGameWrapperMaze.gameNumber == gameID) {
                return boardWrapper_Mazes.get(gameID);
            }
        }
        throw new IndexOutOfBoundsException(); //404
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED) //202
    @PostMapping("/api/games/{gameID}/moves")
    public void performMove(@PathVariable("gameID") int gameID, @RequestBody String move) {
        String[] validInputs = new String[]{"MOVE_UP", "MOVE_DOWN", "MOVE_LEFT", "MOVE_RIGHT", "MOVE_CATS"};
        boolean moveCatsWasEntered = false;
        boolean movedSuccessfully = false;
        boolean gameID_Exists = false;
        boolean playerDied = false;
        ApiBoardWrapper currentBoardWrapper;
        ApiGameWrapper currentGameWrapper;
        Mouse currentMouse;
        List<Cat> allCats;
        Maze currentMaze;
        int cheeseRow = 0;
        int cheeseCol = 0;
        int mouseRow = 0;
        int mouseCol = 0;

        for (ApiGameWrapper currentGameWrapperMaze : gameWrapper_Mazes) {
            if (currentGameWrapperMaze.gameNumber == gameID) {
                gameID_Exists = true;
                break;
            }
        }
        if (!gameID_Exists) {
            throw new IndexOutOfBoundsException();
        }
        currentMaze = regular_Mazes.get(gameID);
        currentBoardWrapper = boardWrapper_Mazes.get(gameID);
        currentGameWrapper = gameWrapper_Mazes.get(gameID);
        allCats = currentMaze.getAll_Cats();
        currentMouse = currentMaze.getMouseObject();
        mouseRow = currentMouse.getLocation().getRow();
        mouseCol = currentMouse.getLocation().getCol();
        cheeseRow = currentMaze.getCheeseLocation().getRow();
        cheeseCol = currentMaze.getCheeseLocation().getCol();
        INPUT result = INPUT.BAD_INPUT;

        if (Arrays.asList(validInputs).contains(move)) {
            result = performMoveHelper(currentBoardWrapper, currentMaze, currentMouse, move, allCats);
            if (result == INPUT.PLAYER_MOVED || result == INPUT.ONLY_CATS_MOVED) {
                movedSuccessfully = true;
                if (result == INPUT.ONLY_CATS_MOVED) {
                    moveCatsWasEntered = true;
                } else if (result == INPUT.PLAYER_MOVED) {
                    currentMaze.updateVisibleCellsForWrapper(currentMaze.getVisibleCellsForWrapper());
                }
            }
            if (moveCatsWasEntered) {
                for (Cat currentCat : allCats) {
                    if (currentMouse.atCat(currentCat.getLocation())) {
                        playerDied = true;
                        setPlayerGameStatus(currentGameWrapper, currentMaze, false, true);
                    }
                }
                if (!playerDied &&
                        mouseRow == cheeseRow &&
                        mouseCol == cheeseCol) {
                    currentMaze.setCheeseLocation();
                    currentMouse.setCheeseCounter(currentMouse.getCheeseCounter() + 1);
                    currentGameWrapper.numCheeseFound++;
                    setCheeseLocationInBoardWrapper(currentBoardWrapper, currentMaze);
                    if (currentMouse.getCheeseCounter() >= currentMouse.getWinCondition()) {
                        setPlayerGameStatus(currentGameWrapper, currentMaze, true, false);
                    }
                }
            }
        }
        if (result == INPUT.BAD_INPUT || !movedSuccessfully) {
            throw new IllegalArgumentException(); //400
        }
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED) //202
    @PostMapping("/api/games/{gameID}/cheatstate")
    public void checkForCheats(@PathVariable("gameID") int gameID, @RequestBody String cheatCode) {
        String[] validInputs = new String[]{"1_CHEESE", "SHOW_ALL"};
        ApiGameWrapper currentGameWrapper;
        boolean gameID_Exists = false;
        Maze currentMaze;
        Mouse currentMouse;
        INPUT result;

        for (ApiGameWrapper currentGameWrapperMaze : gameWrapper_Mazes) {
            if (currentGameWrapperMaze.gameNumber == gameID) {
                gameID_Exists = true;
                break;
            }
        }
        if (!gameID_Exists) {
            throw new IndexOutOfBoundsException(); //404
        }
        if (Arrays.asList(validInputs).contains(cheatCode)) {
            currentGameWrapper = gameWrapper_Mazes.get(gameID);
            currentMaze = regular_Mazes.get(gameID);
            currentMouse = currentMaze.getMouseObject();
            result = performCheat(currentGameWrapper, currentMaze, currentMouse, cheatCode);
            if (result == INPUT.BAD_INPUT) {
                throw new IllegalArgumentException(); //400
            }
        }
    }

    public void setPlayerGameStatus(ApiGameWrapper currentGameWrapper, Maze maze, boolean winStatus, boolean lossStatus) {
        maze.setPlayerWins(winStatus);
        maze.setPlayerLoses(lossStatus);
        currentGameWrapper.isGameWon = winStatus;
        currentGameWrapper.isGameLost = lossStatus;
    }

    public INPUT performMoveHelper(ApiBoardWrapper currentBoardWrapper, Maze currentMaze, Mouse currentMouse, String move, List<Cat> allCats) {
        INPUT result = INPUT.MOVED_INTO_WALL;
        if (move.equals("MOVE_UP")) {
            if (currentMouse.moveDirection('w', currentMaze)) {
                result = INPUT.PLAYER_MOVED;
                setMouseLocationInBoardWrapper(currentBoardWrapper, currentMaze);
            }
        } else if (move.equals("MOVE_DOWN")) {
            if (currentMouse.moveDirection('s', currentMaze)) {
                result = INPUT.PLAYER_MOVED;
                setMouseLocationInBoardWrapper(currentBoardWrapper, currentMaze);
            }
        } else if (move.equals("MOVE_LEFT")) {
            if (currentMouse.moveDirection('a', currentMaze)) {
                result = INPUT.PLAYER_MOVED;
                setMouseLocationInBoardWrapper(currentBoardWrapper, currentMaze);
            }
        } else if (move.equals("MOVE_RIGHT")) {
            if (currentMouse.moveDirection('d', currentMaze)) {
                result = INPUT.PLAYER_MOVED;
                setMouseLocationInBoardWrapper(currentBoardWrapper, currentMaze);
            }
        } else if (move.equals("MOVE_CATS")) {
            result = INPUT.ONLY_CATS_MOVED;
            moveCatsRandomly(allCats, currentBoardWrapper, currentMaze);
        }
        return result;
    }

    public INPUT performCheat(ApiGameWrapper currentGameWrapper, Maze currentMaze, Mouse currentMouse, String cheatCode) {
        INPUT result = INPUT.BAD_INPUT;
        if (cheatCode.equals("1_CHEESE")) {
            result = INPUT.ONE_CHEESE;
            currentMouse.setWinCondition(1);
            currentGameWrapper.numCheeseGoal = 1;
        } else if (cheatCode.equals("SHOW_ALL")) {
            result = INPUT.SHOW_ALL;
            currentMaze.updateVisibleCellsForWrapper_ShowAll(currentMaze.getVisibleCellsForWrapper());
        }
        return result;
    }

    public void moveCatsRandomly(List<Cat> allCats, ApiBoardWrapper currentBoardWrapper, Maze currentMaze) {
        int catArray_Index = 0;
        for (Cat currentCat : allCats) {
            currentCat.moveCatRandomly(currentMaze);
            setCatLocationsInBoardWrapper(currentBoardWrapper, currentCat, catArray_Index);
            catArray_Index++;
        }
    }

    public void setMouseLocationInBoardWrapper(ApiBoardWrapper currentBoardWrapper, Maze currentMaze) {
        currentBoardWrapper.mouseLocation =
                ApiLocationWrapper.makeFromCellLocation(currentMaze.getMouseObject().getLocation());
    }

    public void setCheeseLocationInBoardWrapper(ApiBoardWrapper currentBoardWrapper, Maze currentMaze) {
        currentBoardWrapper.cheeseLocation =
                ApiLocationWrapper.makeFromCellLocation(currentMaze.getCheeseLocation());
    }

    public void setCatLocationsInBoardWrapper(ApiBoardWrapper currentBoardWrapper, Cat currentCat, int catArray_Index) {
        int currentCatRow = currentCat.getLocation().getRow();
        int currentCatCol = currentCat.getLocation().getCol();
        currentBoardWrapper.catLocations.get(catArray_Index).y = currentCatRow;
        currentBoardWrapper.catLocations.get(catArray_Index).x = currentCatCol;
    }

    public void printMouse(Mouse currentMouse) {
        int currentMouseRow = currentMouse.getLocation().getRow();
        int currentMouseCol = currentMouse.getLocation().getCol();
        System.out.println("Mouse Coordinate: {" + currentMouseRow +
                ", " + currentMouseCol + "}");
    }

    public void printCats(List<Cat> allCats) {

        int currentCatRow = 0;
        int currentCatCol = 0;
        int index = 0;
        for (Cat currentCat : allCats) {
            currentCatRow = currentCat.getLocation().getRow();
            currentCatCol = currentCat.getLocation().getCol();
            System.out.println("Cat Coordinate " + index + ": {" + currentCatRow +
                    ", " + currentCatCol + "}");
            index++;
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND) //404
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public void IndexOutOfBoundsException() {
        //nothing to do here
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST) //400
    @ExceptionHandler(IllegalArgumentException.class)
    public void IllegalArgumentException() {
        //nothing to do here
    }
}
