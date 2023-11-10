package games.saboteur;

import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;

import java.util.List;

public class SaboteurForwardModel extends StandardForwardModel {
    @Override
    protected void _setup(AbstractGameState firstState) {
        //
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        return null;
    }
}
