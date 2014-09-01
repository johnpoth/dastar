package com.blizzard.sc2.moves;

import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.units.Units;


public class ConstructUnit implements Move {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstructUnit)) return false;

        ConstructUnit that = (ConstructUnit) o;

        if (unit != that.unit) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return unit.hashCode();
    }

    // for now moves only consist of constructing something
    private final Units unit;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public ConstructUnit(Units unit) {
        this.unit = unit;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the unit
     */
    public final Units getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return "Constructing:" + this.unit;
    }

}
