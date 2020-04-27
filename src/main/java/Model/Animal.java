package Model;

/**
 * Brandon Yip (301294186) & Cole Stankov (301295209)
 * February 26th, 2020
 * Interface that serves as a template for both mouse and cat classes; provides a clear and concise inheritance between
 * a cat & mouse being an animal
 */


public interface Animal {
    public boolean checkForWalls(Coordinate direction, Maze maze);

    public Model.Coordinate getLocation();
}