package games.saboteur.actions;

import core.actions.SetGridValueAction;
import games.saboteur.components.PathCard;

public class PlacePathCard extends SetGridValueAction
{
    boolean rotated;

    public PlacePathCard(int gridBoard, int x, int y, PathCard value, boolean rotation) {
        super(gridBoard, x, y, value);
        this.rotated = rotation;
    }
}
