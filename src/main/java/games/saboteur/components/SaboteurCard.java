package games.saboteur.components;

import core.components.Card;

public class SaboteurCard extends Card
{
    public SaboteurCardType type;
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
        this.type = type;
    }
}
