package games.saboteur;

import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Component;
import core.components.Deck;
import core.components.PartialObservableDeck;
import games.saboteur.actions.PlacePathCard;
import games.saboteur.components.ActionCard;
import games.saboteur.components.PathCard;
import games.saboteur.components.SaboteurCard;
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

        sgs.playerScore = new int[firstState.getCurrentPlayer()];
        setupPlayerDecks(sgs);

        ResetDecks(sgs, sgp);
        ResetBoard(sgs, sgp);
    }

    private void SetupRound(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {

        ResetDecks(sgs, sgp);
        ResetBoard(sgs, sgp);
        ResetPathCardOptions(sgs);
    }

    private void setupPlayerDecks(SaboteurGameState sgs)
    {
        //Initialise Player Decks
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            sgs.playerDecks.add(new Deck<>("Player" + i + "Deck", VISIBLE_TO_OWNER));
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

        //Initialise Decks
        sgs.drawDeck = new Deck<>("DrawDeck", HIDDEN_TO_ALL);
        sgs.discardDeck = new PartialObservableDeck<SaboteurCard>("DiscardDeck", 0,HIDDEN_TO_ALL); //OWNER ID ARBITRARY RN, DUNNO WHAT TO DO
        sgs.goalDeck = new Deck<>("GoalDeck", HIDDEN_TO_ALL);
        sgs.roleDeck = new PartialObservableDeck<>("RoleDeck", 0, HIDDEN_TO_ALL);
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            sgs.playerDecks.add(new Deck<>("Player" + i + "Deck", VISIBLE_TO_OWNER));
            sgs.brokenToolDecks.add(new Deck<>("Player" + i + "BrokenToolDeck", VISIBLE_TO_OWNER));
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
        for(int i = 0; i < sgs.roleDeck.getSize(); i++)
        {
            sgs.roleDeck.setVisibilityOfComponent(i, i, true);
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

        Vector2D center = new Vector2D(sgs.centerOfGrid,sgs.centerOfGrid);

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
                        ComputePathAction((PathCard) card, sgs);
                        break;
                    }

                case Action:
                    actions.addAll(ComputeActionAction((ActionCard) card ,sgs));
                    break;
            }
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
    private void ComputeFixToolsAction(SaboteurGameState sgs, ActionCard card)
    {
        //for everyone's BrokenToolDeck
        //If that player does have a BrokenTool Matching it
            //new action to remove players
    }
//endregion

    private ArrayList<AbstractAction> ComputeActionAction (ActionCard card, SaboteurGameState sgs)
    {
        ArrayList<AbstractAction> actions = new ArrayList<>();
        switch(card.actionType)
        {
            case BrokenTools:
                //for everyone's BrokenToolDeck
                //If that player doesn't have a BrokenTool Matching it
                    //new action to add card onto their BrokenToolDeck
            case FixTools:
                //for everyone's BrokenToolDeck
                //If that player does have a BrokenTool Matching it
                    //new action to remove players

            case Map:
                //new action to check either 1 of the 3 goals and make it visible to the player
                //need to make grid board have visibility of some kind

            case RockFall:
                //for entire grid
                //if cell is null
                    //continue
                //if cell has PathCard and is PathCardType "path" or "edge"
                    //new action SetGridValueAction coordinates and then move card to discard deck
                    //may need new action class for this
        }
        return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction actionTaken) {
        if(actionTaken.equals(PlacePathCard.class))
        {
            //update pathCardOptions
            PlacePathCard placePathCard = (PlacePathCard) actionTaken;
        }
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
}