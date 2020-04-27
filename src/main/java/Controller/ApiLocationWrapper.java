package Controller;

import Model.Coordinate;
import Model.Cat;

import java.util.ArrayList;
import java.util.List;

public class ApiLocationWrapper {
    public int x;
    public int y;

    public static ApiLocationWrapper makeFromCellLocation(Coordinate cell) {
        ApiLocationWrapper location = new ApiLocationWrapper();
        location.y = cell.getRow();
        location.x = cell.getCol();

        return location;
    }

    public static List<ApiLocationWrapper> makeFromCellLocations(List<Cat> allCats) {
        List<ApiLocationWrapper> locations = new ArrayList<>();
        Coordinate newCoordinate;
        int currentRow = 0;
        int currentCol = 0;

        for (Cat currentCat : allCats) {
            Coordinate current_Coordinate = currentCat.getLocation();
            currentRow = current_Coordinate.getRow();
            currentCol = current_Coordinate.getCol();
            newCoordinate = new Coordinate(currentRow, currentCol);
            locations.add(makeFromCellLocation(newCoordinate));
        }
        return locations;
    }
}
