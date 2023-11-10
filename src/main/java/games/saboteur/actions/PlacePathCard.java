package games.saboteur.actions;

import core.actions.SetGridValueAction;
import core.components.Component;

public class PlacePathCard extends SetGridValueAction
{
    int rotation;

    public PlacePathCard(int gridBoard, int x, int y, Component value, int rotation) {
        super(gridBoard, x, y, value);
        this.rotation = rotation;
    }
}
