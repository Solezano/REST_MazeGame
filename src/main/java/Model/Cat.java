package Model;

import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 * Class that manages movement and properties of various cat NPC's. Random but thorough paths for cats as they
 * traverse the maze. Has methods that enable proper recognition of player death etc.
 */

public class Cat implements Animal {
    private char currentMapCharacter = ' ';
    private char wall = '#';
    private Coordinate previousLocation;
    private Coordinate currentLocation;

    public Cat(int x_Coordinate, int y_Coordinate) {
        this.currentLocation = new Coordinate(x_Coordinate, y_Coordinate);
        this.previousLocation = currentLocation;
    }

    public void moveCatRandomly(Maze maze) {
        List<Coordinate> previousLocationArr = new ArrayList<>();
        List<Coordinate> validLocations = new ArrayList<>();
        List<Coordinate> tempList = new ArrayList<>();
        Character[][] completeMaze = maze.getCompleteMaze();
        int previousRow = previousLocation.getRow();
        int previousCol = previousLocation.getCol();
        int currentRow = currentLocation.getRow();
        int currentCol = currentLocation.getCol();
        int randomDirection = 0;

        Coordinate up = new Coordinate(currentRow - 1, currentCol);
        Coordinate down = new Coordinate(currentRow + 1, currentCol);
        Coordinate left = new Coordinate(currentRow, currentCol - 1);
        Coordinate right = new Coordinate(currentRow, currentCol + 1);

        validLocations.add(up);
        validLocations.add(down);
        validLocations.add(left);
        validLocations.add(right);

        for (Coordinate s : validLocations) {
            currentRow = s.getRow();
            currentCol = s.getCol();
            if (completeMaze[currentRow][currentCol] == wall) {
                tempList.add(s);
            }
            if (s.getRow() == previousRow && s.getCol() == previousCol) {
                previousLocationArr.add(s);
            }
        }
        validLocations.removeAll(tempList);
        validLocations.removeAll(previousLocationArr);

        if (validLocations.isEmpty()) {
            maze.updateCatLocation_inMazes(currentLocation, previousLocationArr.get(0), this);
            previousLocation = currentLocation;
            currentLocation = previousLocationArr.get(0);
        } else {
            randomDirection = ThreadLocalRandom.current().nextInt(0, validLocations.size());
            previousLocation = currentLocation;
            currentLocation = validLocations.get(randomDirection);
            maze.updateCatLocation_inMazes(previousLocation, currentLocation, this);
        }
    }

    public boolean checkForWalls(Coordinate newCatDirection, Maze maze) {
        int currentRow = newCatDirection.getRow();
        int currentCol = newCatDirection.getCol();
        boolean isWall = false;
        Character[][] completeMaze = maze.getCompleteMaze();

        if (completeMaze[currentRow][currentCol] == wall) {
            isWall = true;
        }
        return isWall;
    }

    public void setCurrentMapCharacter(char currentMapCharacter) {
        this.currentMapCharacter = currentMapCharacter;
    }

    public char getCurrentMapCharacter() {
        return currentMapCharacter;
    }

    public Coordinate getLocation() {
        return currentLocation;
    }
}