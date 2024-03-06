package games.saboteur.components;

import org.apache.poi.ss.formula.atp.Switch;

import java.util.Arrays;
public class PathCard extends SaboteurCard
{
    final boolean[] directions;
    final public PathCardType type;
    static int nOfTreasures = 1;
    public final boolean hasTreasure;

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
        if(type == PathCardType.Goal && nOfTreasures > 0)
        {
            hasTreasure = true;
            nOfTreasures -= 1;
        }
        else
        {
            hasTreasure = false;
        }
    }

    public void Rotate()
    {
        //up down
        Swap(0,1);
        //left right
        Swap(2,3);
    }

    private void Swap(int indexA, int indexB)
    {
        boolean temp = directions[indexA];
        directions[indexA] = directions[indexB];
        directions[indexB] = temp;
    }

    public boolean[] getDirections() {return directions;}
    public int getOppositeDirection(int direction)
    {
        switch(direction)
        {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
        }
        return -1;
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
