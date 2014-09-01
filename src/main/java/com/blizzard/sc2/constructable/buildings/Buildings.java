package com.blizzard.sc2.constructable.buildings;

import com.blizzard.sc2.constructable.Constructable;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.resources.Resource;


public enum Buildings implements Constructable {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Enum constants 
    //~ ----------------------------------------------------------------------------------------------------------------

    BARRACKS(new Resource(150, 0), 65, new Units[] { Units.MARINE }), COMMAND_CENTER(new Resource(400, 0), 100, new Units[] { Units.WORKER });

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Resource resource;

    /**
     * time until idle state is reached. Either time left to construct building, or until unit is finished constructing
     */
    private int time;
    private final Units[] units;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    private Buildings(Resource resource, int time, Units[] units) {
        this.resource = resource;
        this.time = time;
        this.units = units;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the units the building can construct
     */
    public final Units[] getUnits() {
        return this.units.clone();
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    /**
     * @return the time required to construct
     */
    @Override
    public int getTime() {
        return this.time;
    }

}
