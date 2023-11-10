package games.saboteur.components;

import java.util.Arrays;

public class PathCard extends SaboteurCard
{
    final boolean[] directions;
    final PathCardType type;
    static int nOfTreasures = 1;
    final boolean hasTreasure;

    public enum PathCardType
    {
        Edge,
        Path,
        Goal,
        Start,
    }

    public PathCard(PathCardType type, boolean[] direction)
    {
        super(SaboteurCardType.Path);
        this.type = type;
        this.directions = direction;
        if(type == PathCardType.Goal && nOfTreasures < 1)
        {
            hasTreasure = true;
            nOfTreasures -= 1;
        }
        else
        {
            hasTreasure = false;
        }
    }

    @Override
    public String toString()
    {
        switch(type)
        {
            case Edge:
            case Path:
                return type + Arrays.toString(directions);
            case Start:
                return type.toString();
            case Goal:
                return type.toString() + hasTreasure;
        }
        return null;
    }
}
