package games.saboteur;

import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Card;
import core.components.Deck;
import core.components.PartialObservableDeck;
import games.saboteur.actions.*;
import games.saboteur.components.*;
import utilities.Vector2D;

import static core.CoreConstants.VisibilityMode.*;

import java.util.*;

public class SaboteurForwardModel extends StandardForwardModel {
//--------------------------------------------------------------------------------------------------//
//region Setup Functions
    @Override
    protected void _setup(AbstractGameState firstState)
    {
        SaboteurGameState sgs = (SaboteurGameState) firstState;
        SaboteurGameParameters sgp = (SaboteurGameParameters) sgs.getGameParameters();

        sgs.roleDeck = new PartialObservableDeck<>("RoleDeck", sgs.getNPlayers());
        for(int i = 0; i < sgs.roleDeck.getSize(); i++)
        {
            sgs.roleDeck.setVisibilityOfComponent(i, i, true);
        }

        sgs.drawDeck = new Deck<>("DrawDeck", HIDDEN_TO_ALL);
        sgs.goalDeck = new Deck<>("GoalDeck", HIDDEN_TO_ALL);
        sgs.discardDeck = new PartialObservableDeck<>("DiscardDeck", sgs.getNPlayers());
        sgs.gridBoard = new PartialObservableGridBoard<>(sgp.GridSize, sgp.GridSize, sgs.getNPlayers(), true);
        sgs.nuggetDeck = new Deck<>("NuggetDeck", HIDDEN_TO_ALL);

        setupPlayerDecks(sgs);
        ResetDecks(sgs, sgp);
        ResetBoard(sgs, sgp);
        SetupStartingHand(sgs);
    }

    private void SetupRound(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {
        ResetDecks(sgs, sgp);
        ResetBoard(sgs, sgp);
        ResetPathCardOptions(sgs);
        SetupStartingHand(sgs);
    }

    private void setupPlayerDecks(SaboteurGameState sgs)
    {
        //Initialise Player Decks
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            sgs.playerDecks.add(new Deck<SaboteurCard>("Player" + i + "Deck", VISIBLE_TO_OWNER));
            sgs.brokenToolDecks.add(new Deck<SaboteurCard>("Player" + i + "BrokenToolDeck", VISIBLE_TO_OWNER));
            sgs.playerNuggetDecks.add(new PartialObservableDeck<SaboteurCard>("Player" + i + "NuggetDeck", i));
        }
    }

    private void SetupStartingHand(SaboteurGameState sgs)
    {
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            for (int j = 0; j < 5; j++)
            {
                sgs.playerDecks.get(i).add(sgs.drawDeck.draw());
            }
        }
    }
//endregion
//--------------------------------------------------------------------------------------------------//
//region Reset Functions
    private void ResetBoard(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {
        //Initialise GridBoard with starting card
        sgs.centerOfGrid = (int) Math.floor(sgp.GridSize / 2.0);
        sgs.gridBoard.setElement(sgs.centerOfGrid, sgs.centerOfGrid, new PathCard(PathCard.PathCardType.Start, new boolean[]{true, true, true, true}));
        ResetGoals(sgs,sgp);
        }

    private void ResetGoals(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {
        int totalLength = sgs.goalDeck.getSize() * (sgp.GoalSpacingY + 1);
        int startingY = (int) Math.floor(totalLength / 2.0) - 1;

        assert sgp.GoalSpacingX > Math.floor(sgs.gridBoard.getWidth() / 2.0): "Placing Goal card out of bounds for X";

        for(SaboteurCard goalCard: sgs.goalDeck.getComponents())
        {
            assert startingY > sgs.gridBoard.getHeight(): "Placing Goal card out of bounds for Y";

            sgs.gridBoard.setElement(sgp.GoalSpacingX + 1, startingY,goalCard);
            startingY -= (sgp.GoalSpacingY + 1);
        }
    }

    private void ResetDecks(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {
        //Clear player decks besides their gold nugget deck
        sgs.drawDeck.clear();
        sgs.discardDeck.clear();
        sgs.goalDeck.clear();
        sgs.roleDeck.clear();
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            sgs.brokenToolDecks.get(i).clear();
            sgs.playerDecks.get(i).clear();
        }

        //Fill in decks
        FillDeckViaMap(sgs.drawDeck, sgp.pathCardDeck);
        FillDeckViaMap(sgs.drawDeck, sgp.actionCardDeck);
        FillDeckViaMap(sgs.roleDeck, sgp.roleCardDeck);

        //Shuffle Necessary decks
        Random r = new Random(sgs.getGameParameters().getRandomSeed() + sgs.getRoundCounter());
        sgs.drawDeck.shuffle(r);
        sgs.goalDeck.shuffle(r);
        sgs.roleDeck.shuffle(r);
        sgs.nOfSaboteurs = 0;
        sgs.nOfMiners = 0;
        for(int i = 0; i < sgs.getNPlayers(); i++)
        {
            sgs.roleDeck.setVisibilityOfComponent(i, i, true);
            RoleCard currentRole = (RoleCard) sgs.roleDeck.get(i);                                                       //does this remove the card?
            if(currentRole.type == RoleCard.RoleCardType.Saboteur)
            {
                sgs.nOfSaboteurs += 1;
            }
            else
            {
                sgs.nOfMiners += 1;
            }
        }
    }

    private void ResetPathCardOptions(SaboteurGameState sgs)
    {
        sgs.pathCardOptions = new ArrayList<>();
        sgs.pathCardOptions.add(new Vector2D(sgs.centerOfGrid + 1, sgs.centerOfGrid));
        sgs.pathCardOptions.add(new Vector2D(sgs.centerOfGrid - 1, sgs.centerOfGrid));
        sgs.pathCardOptions.add(new Vector2D(sgs.centerOfGrid, sgs.centerOfGrid + 1));
        sgs.pathCardOptions.add(new Vector2D(sgs.centerOfGrid, sgs.centerOfGrid - 1));
    }
//endregion
//--------------------------------------------------------------------------------------------------//
//region Compute Action Functions
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState)
    {
        //Initialise ArrayList of Abstract Actions
        ArrayList<AbstractAction> actions = new ArrayList<>();
        SaboteurGameState sgs = (SaboteurGameState) gameState;

        int player = sgs.getCurrentPlayer();
        Deck<SaboteurCard> currentPlayersDeck = sgs.playerDecks.get(player);
        boolean playerHasBrokenTool = sgs.brokenToolDecks.get(player).getSize() > 0;

        //Check Each card in players deck
        //Switch Case for each type of card you would find in hand

        for(SaboteurCard card: currentPlayersDeck.getComponents())
        {
            switch(card.type)
            {
                case Path:
                    if(!playerHasBrokenTool)
                    {
                        actions.addAll(ComputePathAction((PathCard) card, sgs));
                        break;
                    }

                case Action:
                    actions.addAll(ComputeActionAction((ActionCard) card ,sgs));
                    break;
            }
        }

        for(Card card: currentPlayersDeck.getComponents())
        {
            actions.add(new Pass((SaboteurCard) card));
        }
        return actions;
    }

//region GettingPathActions
    //Updates the map of possible path card locations and directions whenever a path card is placed
    private ArrayList<AbstractAction> ComputePathAction(PathCard card, SaboteurGameState sgs)
    {
        //Check if card can fit into key pair value
        //Rotate card, and recheck
        //If it can fit
            //Add new action to place card
        ArrayList<AbstractAction> actions = new ArrayList<>();
        for(Vector2D location: sgs.pathCardOptions)
        {
            if(CheckPathCardPlacement(card, sgs, location))
            {
                actions.add(new PlacePathCard(0, location.getX(), location.getY(), card, false));
            }

            //check when its rotated
            card.Rotate();
            if(CheckPathCardPlacement(card, sgs, location))
            {
                actions.add(new PlacePathCard(0, location.getX(), location.getY(), card, true));
            }
            card.Rotate();
        }

        return actions;
    }

    private boolean CheckPathCardPlacement(PathCard card, SaboteurGameState sgs, Vector2D location)
    {
        boolean canBePlaced = true;
        for(int i = 0 ; i < 4; i++)
        {
            Vector2D offset = getCardOffset(i);
            PathCard offsetCard =  (PathCard) sgs.gridBoard.getElement(location.getX() + offset.getX(), location.getY() + offset.getY());

            if(offsetCard != null && offsetCard.getDirections()[i] == card.getDirections()[card.getOppositeDirection(i)])
            {
                canBePlaced = false;
            }
        }
        return canBePlaced;
    }
    //For when Rockfall card is played
    private void RecalculatePathCardOptions(SaboteurGameState sgs)
    {
        sgs.pathCardOptions.clear();
        RecalculatePathCardOptionsRecursive(new boolean[]{false, false, false, false}, sgs, new Vector2D(sgs.centerOfGrid, sgs.centerOfGrid));
    }

    private void RecalculatePathCardOptionsRecursive(boolean[] directionFrom, SaboteurGameState sgs, Vector2D location)
    {
        PathCard currentCard = (PathCard) sgs.gridBoard.getElement(location.getX(), location.getY());
        if(currentCard == null)
        {
            sgs.pathCardOptions.add(location);
            return;
        }
        boolean[] currentDirections = currentCard.getDirections();
        for(int i = 0; i < 4; i++)
        {
            if(currentDirections[i] && !directionFrom[i])
            {
                boolean[] newDirectionFrom = new boolean[4];
                newDirectionFrom[i] = true;
                RecalculatePathCardOptionsRecursive(newDirectionFrom, sgs, location.add(getCardOffset(i)));
            }
        }
    }

    private Vector2D getCardOffset(int value)
    {
        switch (value % 4)
        {
            case 0:
                return new Vector2D(0,1);
            case 1:
                return new Vector2D(0,-1);
            case 2:
                return new Vector2D(1,0);
            case 3:
                return new Vector2D(-1,0);
            default:
                return new Vector2D(999,999);
        }
    }
//endregion
//region GettingFixToolsActions
    private ArrayList<AbstractAction> ComputeActionFixTools(ActionCard card, SaboteurGameState sgs)
    {
        //for everyone's BrokenToolDeck
        //If that player does have a BrokenTool Matching it
            //new action to remove players
        ArrayList<AbstractAction> actions = new ArrayList<>();
        for(int currentPlayer = 0; currentPlayer < sgs.getNPlayers(); currentPlayer++)
        {
            if(currentPlayer == sgs.getCurrentPlayer())
            {
                continue;
            }
            Deck<SaboteurCard> currentPlayerBrokenToolDeck = sgs.brokenToolDecks.get(currentPlayer);
            for(SaboteurCard brokenToolCard: currentPlayerBrokenToolDeck.getComponents())
            {
                ActionCard currentCard = (ActionCard) brokenToolCard;
                if(HasToolType(card, currentCard))
                {
                    actions.add(new PlayBrokenToolCard(card,currentPlayer));
                }
            }
        }
        return actions;
    }
//endregion

    private ArrayList<AbstractAction> ComputeActionAction (ActionCard card, SaboteurGameState sgs)
    {
        ArrayList<AbstractAction> actions = new ArrayList<>();
        switch(card.actionType)
        {
            case BrokenTools:
                actions.addAll(ComputeActionBrokenTools(card, sgs));

            case FixTools:
                actions.addAll(ComputeActionFixTools(card, sgs));

            case Map:
                actions.addAll(ComputeActionMap(card, sgs));

            case RockFall:
                actions.addAll(ComputeActionRockFall(sgs));
        }
        return actions;
    }

    private ArrayList<AbstractAction> ComputeActionMap(ActionCard card, SaboteurGameState sgs)
    {
        //new action to check either 1 of the 3 goals and make it visible to the player
        //need to make grid board have visibility of some kind
        ArrayList<AbstractAction> actions = new ArrayList<>();
        PartialObservableGridBoard<PathCard> gridBoard = sgs.gridBoard;
        for(int x = 0; x < gridBoard.getWidth(); x++)
        {
            for(int y = 0; y < gridBoard.getHeight(); y++)
            {
                PathCard currentCard = (PathCard) gridBoard.getElement(x, y);
                if(currentCard != null && currentCard.type == PathCard.PathCardType.Goal)
                {
                    actions.add(new PlayMapCard(x, y, card));
                }
            }
        }
        return actions;
    }
    private ArrayList<AbstractAction> ComputeActionRockFall(SaboteurGameState sgs)
    {
        ArrayList<AbstractAction> actions = new ArrayList<>();
        for(int x = 0; x < sgs.gridBoard.getWidth(); x++)
        {
            for(int y = 0; y < sgs.gridBoard.getHeight(); y++)
            {
                PathCard currentCard = (PathCard) sgs.gridBoard.getElement(x, y);
                if(currentCard != null && (currentCard.type == PathCard.PathCardType.Path || currentCard.type == PathCard.PathCardType.Edge))
                {
                    actions.add(new PlayRockFallCard(sgs.gridBoard.getComponentID(), x, y));
                }
            }
        }
        return actions;
    }

    private ArrayList<AbstractAction> ComputeActionBrokenTools(ActionCard card, SaboteurGameState sgs)
    {
        //for everyone's BrokenToolDeck
        //If that player doesn't have a BrokenTool Matching it
            //new action to add card onto their BrokenToolDeck
        ArrayList<AbstractAction> actions = new ArrayList<>();
        for(int currentPlayer = 0; currentPlayer < sgs.getNPlayers(); currentPlayer++)
        {
            if(currentPlayer == sgs.getCurrentPlayer())
            {
                continue;
            }
            Deck<SaboteurCard> currentPlayerBrokenToolDeck = sgs.brokenToolDecks.get(currentPlayer);
            for(SaboteurCard brokenToolCard: currentPlayerBrokenToolDeck.getComponents())
            {
                ActionCard currentCard = (ActionCard) brokenToolCard;
                if(!HasToolType(card, currentCard))
                {
                    actions.add(new PlayBrokenToolCard(card, currentPlayer));
                }
            }
        }
        return actions;
    }
    private boolean HasToolType(ActionCard card, ActionCard currentCard)
    {
        //assume that the card has only 1 tool type
        assert currentCard.toolTypes != null;
        assert card.toolTypes != null;

        for(ActionCard.ToolCardType currentToolType: currentCard.toolTypes)
        {
            if(card.toolTypes[0] == currentToolType)
            {
                return true;
            }
        }
        return false;
    }
//endregion
//--------------------------------------------------------------------------------------------------//
//region OtherFunctions
    private void FillDeckViaMap(Deck<SaboteurCard> deck, Map<SaboteurCard, Integer> map)
    {
        //add all the Path Cards
        for (Map.Entry<SaboteurCard, Integer> entry : map.entrySet())
        {
            for (int i = 0; i < entry.getValue(); i++)
            {
                deck.add(entry.getKey());
            }
        }
    }

//endregion
//--------------------------------------------------------------------------------------------------//
//region afterAction functions
    @Override
    protected void _afterAction(AbstractGameState gameState, AbstractAction action)
    {
        SaboteurGameState sgs = (SaboteurGameState) gameState;
        if (action instanceof PlacePathCard)
        {
            int goalDirection = HasGoalInPossibleDirection(sgs, (PlacePathCard) action);
            if(goalDirection != -1)
            {
                Vector2D offset = getCardOffset(goalDirection);
                PathCard goalCard = (PathCard) sgs.gridBoard.getElement(((PlacePathCard) action).getX() + offset.getX(), ((PlacePathCard) action).getY() + offset.getY());
                for(int i = 0; i < sgs.getNPlayers(); i++)
                {
                    sgs.gridBoard.setElementVisibility(((PlacePathCard) action).getX() + offset.getX(), ((PlacePathCard) action).getY() + offset.getY(), i, true);
                }
                if(goalCard.hasTreasure)
                {
                    DistributeMinerEarnings(sgs);
                }
            }
        }
        else if(action instanceof PlayRockFallCard)
        {
            RecalculatePathCardOptions(sgs);
        }
        endPlayerTurn(sgs);
    }

    //check if path card goes into a goal
    private int HasGoalInPossibleDirection(SaboteurGameState sgs, PlacePathCard placePathCard)
    {
        PathCard pathCard = (PathCard) sgs.gridBoard.getElement(placePathCard.getX(), placePathCard.getY());
        boolean directions[] = pathCard.getDirections();
        for(int i = 0; i < pathCard.getDirections().length; i++)
        {
            if(directions[i])
            {
                getCardOffset(i);
                PathCard currentCard = (PathCard) sgs.gridBoard.getElement(placePathCard.getX() + getCardOffset(i).getX(), placePathCard.getY() + getCardOffset(i).getY());
                if(currentCard != null && currentCard.type == PathCard.PathCardType.Goal)
                {
                    return i;
                }
            }
        }
        return -1;
    }

    //Distribute earnings for all saboteurs (if any exists)
    private void DistributeMinerEarnings(SaboteurGameState sgs)
    {
        Deck<SaboteurCard> winningPlayersNuggetDeck = sgs.playerNuggetDecks.get(sgs.getCurrentPlayer());

        int highestNuggetSizeIndex = 0;
        int highestNuggetSize = 0;
        for(int i = 0; i < sgs.nuggetDeck.getSize(); i++)
        {
            int currentNuggetSize = sgs.nuggetDeck.peek(i).nOfNuggets;
            if(currentNuggetSize > highestNuggetSize)
            {
                highestNuggetSize = currentNuggetSize;
                highestNuggetSizeIndex = i;
            }
        }
        winningPlayersNuggetDeck.add(sgs.nuggetDeck.pick(highestNuggetSizeIndex));

        for(int player = 0; player < sgs.getNPlayers(); player++)
        {
            RoleCard currentPlayersRole = (RoleCard) sgs.roleDeck.get(player);
            if(player == sgs.getCurrentPlayer() || currentPlayersRole.type == RoleCard.RoleCardType.Saboteur)
            {
                continue;
            }
            PartialObservableDeck<SaboteurCard> currentPlayerNuggetDeck = sgs.playerNuggetDecks.get(player);
            currentPlayerNuggetDeck.add(sgs.nuggetDeck.draw());
        }

        if(sgs.getRoundCounter() > 2)
        {
            endGame(sgs);
        }
        SetupRound(sgs, (SaboteurGameParameters) sgs.getGameParameters());
        endRound(sgs);
    }

    //Distribute earnings for all miners
    private void DistributeSaboteurEarnings(SaboteurGameState sgs)
    {
        int targetNuggetValue = 0;
        switch (sgs.nOfSaboteurs)
        {
            case 1:
                targetNuggetValue = 4;
                break;
            case 2:
            case 3:
                targetNuggetValue = 3;
                break;
            case 4:
                targetNuggetValue = 2;
                break;
        }

        for(int player = 0; player < sgs.getNPlayers(); player++)
        {
            Deck<SaboteurCard> currentPlayerNuggetDeck = sgs.playerNuggetDecks.get(player);
            RoleCard currentPlayersRole = (RoleCard) sgs.roleDeck.get(player);
            if(player == sgs.getCurrentPlayer() || currentPlayersRole.type == RoleCard.RoleCardType.GoldMiner)
            {
                continue;
            }
            if(sgs.nuggetDeck.getSize() == 0)
            {
                break;
            }

            int[] indexes = getNuggetValues(sgs, targetNuggetValue);
            int pointer = 0;
            for(int i = 0; i < sgs.nuggetDeck.getSize(); i++)
            {
                if(sgs.nuggetDeck.peek(i).nOfNuggets == indexes[pointer])
                {
                    currentPlayerNuggetDeck.add(sgs.nuggetDeck.pick(i));
                    pointer++;
                    if(pointer == indexes.length)
                    {
                        break;
                    }
                }
            }
        }
        if(sgs.getRoundCounter() > 2)
        {
            endGame(sgs);
        }
        SetupRound(sgs, (SaboteurGameParameters) sgs.getGameParameters());
        endRound(sgs);
    }

    private int getHighestNuggetSize(SaboteurGameState sgs)
    {
        int highestNuggetSize = 0;
        for(int i = 0; i < sgs.nuggetDeck.getSize(); i++)
        {
            if(sgs.nuggetDeck.peek(i).nOfNuggets > highestNuggetSize)
            {
                highestNuggetSize = sgs.nuggetDeck.peek(i).nOfNuggets;
            }
        }
        return highestNuggetSize;
    }

    private int[] getNuggetValues(SaboteurGameState sgs, int targetValue)
    {
        int l = 0;
        int r = sgs.nuggetDeck.getSize() - 1;
        while(l < r)
        {
            int lValue = sgs.nuggetDeck.peek(l).nOfNuggets;
            int rValue = sgs.nuggetDeck.peek(r).nOfNuggets;
            int currentValue = lValue + rValue;
            if(currentValue == targetValue)
            {
                return new int[]{lValue,rValue};
            }
            if(currentValue < targetValue)
            {
                l++;
            }
            else
            {
                r--;
            }
        }
        if(getHighestNuggetSize(sgs) == 1)
        {
            int[] indexes = new int[sgs.nOfSaboteurs];
            for(int i = 0; i < sgs.nOfSaboteurs; i++)
            {
                indexes[i] = 0;
            }
        }
        return new int[]{-1};
    }
//endregion
//--------------------------------------------------------------------------------------------------//

}