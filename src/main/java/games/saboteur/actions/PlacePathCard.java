package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.actions.SetGridValueAction;
import games.saboteur.SaboteurGameParameters;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.PathCard;
import utilities.Vector2D;

public class PlacePathCard extends SetGridValueAction
{
    boolean rotated;
    PathCard pathCard;
    public PlacePathCard(int gridBoard, int x, int y, PathCard pathCard, boolean rotated) {
        super(gridBoard, x, y, pathCard);
        this.rotated = rotated;
        this.pathCard = pathCard;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        if(rotated)
        {
            pathCard.Rotate();
        }
        return true;
    }

    @Override
    public AbstractAction copy() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return null;
    }
}
