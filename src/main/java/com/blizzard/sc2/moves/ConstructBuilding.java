package com.blizzard.sc2.moves;

import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.buildings.Buildings;


public class ConstructBuilding implements Move {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstructBuilding)) return false;

        ConstructBuilding that = (ConstructBuilding) o;

        if (building != that.building) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return building != null ? building.hashCode() : 0;
    }

    // for now moves only consist of constructing something
    private final Buildings building;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public ConstructBuilding(Buildings building) {
        this.building = building;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the constructable
     */
    public final Buildings getBuilding() {
        return building;
    }

    @Override
    public String toString() {
        return this.building.toString();
    }

}
