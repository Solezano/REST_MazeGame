package Model;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 *
 * Class that creates objects that store various locations on the maze grid.
 */


public class Coordinate {
    private char direction;
    private int row;
    private int col;


    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public char getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object coordinate) {
        boolean result = false;
        final Coordinate newCoordinate = (Coordinate) coordinate;
        if (this.getRow() == newCoordinate.getRow() && this.getCol() == newCoordinate.getCol()) {
            result = true;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
