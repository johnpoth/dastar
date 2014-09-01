package com.blizzard.sc2.constructable.units;

import com.blizzard.sc2.constructable.Constructable;
import com.blizzard.sc2.constructable.buildings.Buildings;
import com.blizzard.sc2.resources.Resource;


public enum Units implements Constructable {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Enum constants 
    //~ ----------------------------------------------------------------------------------------------------------------

    MARINE(new Resource(50, 0), 25), WORKER(new Resource(50, 0), 17);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Resource resource;
    private final int time;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    private Units(Resource resource, int time) {
        this.resource = resource;
        this.time = time;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see com.blizzard.sc2.units.Constructable#getResource()
     */
    @Override
    public Resource getResource() {
        return this.resource;
    }

    /* (non-Javadoc)
     * @see com.blizzard.sc2.units.Constructable#getTime()
     */
    @Override
    public int getTime() {
        return this.time;
    }

    //
    public Buildings getBuilding() {
        if (this.equals(Units.MARINE)) {
            return Buildings.BARRACKS;
        }
        if (this.equals(Units.WORKER)) {
            return Buildings.COMMAND_CENTER;
        }
        throw new IllegalStateException("Fucked up error");
    }

}
