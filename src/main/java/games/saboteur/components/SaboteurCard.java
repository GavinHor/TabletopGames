package games.saboteur.components;

import core.components.Card;

public class SaboteurCard extends Card
{
    public enum SaboteurCardType
    {
        Path,
        Role,
        GoldNugget,
        Action,
    }

    public SaboteurCard (SaboteurCardType type)
    {
        super(type.toString());
    }
}
