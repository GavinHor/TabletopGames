package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;

public class PlayMapCard extends AbstractAction {
    //need to make partial observable grid class
    @Override
    public boolean execute(AbstractGameState gs) {
        return false;
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
