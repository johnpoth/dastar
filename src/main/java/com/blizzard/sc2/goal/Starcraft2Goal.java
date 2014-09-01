package com.blizzard.sc2.goal;

import java.util.Map;

import com.blizzard.sc2.algorithm.Goal;
import com.blizzard.sc2.constructable.units.Units;


public class Starcraft2Goal implements Goal {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Map<Units, Integer> unitsToOwn;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @param unitsToOwn
     */
    public Starcraft2Goal(Map<Units, Integer> unitsToOwn) {
        this.unitsToOwn = unitsToOwn;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Map<Units, Integer> getUnits() {
        return unitsToOwn;

    }
}
