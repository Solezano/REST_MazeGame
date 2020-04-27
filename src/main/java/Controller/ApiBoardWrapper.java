package Controller;

import java.util.List;
import Model.Maze;

public class ApiBoardWrapper {
    public boolean[][] cellsThatHaveWallsForWrapper;
    public boolean[][] visibleCellsForWrapper;
    public List<ApiLocationWrapper> catLocations;
    public ApiLocationWrapper cheeseLocation;
    public ApiLocationWrapper mouseLocation;
    public int boardWidth;
    public int boardHeight;

    public static ApiBoardWrapper makeFromGame(Maze game) {
        ApiBoardWrapper wrapper = new ApiBoardWrapper();
        wrapper.cheeseLocation = ApiLocationWrapper.makeFromCellLocation(game.getCheeseLocation());
        wrapper.mouseLocation = ApiLocationWrapper.makeFromCellLocation(game.getMouseLocation());
        wrapper.catLocations = ApiLocationWrapper.makeFromCellLocations(game.getAll_Cats());
        wrapper.cellsThatHaveWallsForWrapper = game.getCellsThatHaveWallsForWrapper();
        wrapper.visibleCellsForWrapper = game.getVisibleCellsForWrapper();
        wrapper.boardHeight = game.getROWS();
        wrapper.boardWidth = game.getCOLS();

        return wrapper;
    }
}
