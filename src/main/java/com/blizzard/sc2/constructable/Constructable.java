package com.blizzard.sc2.constructable;

import com.blizzard.sc2.resources.Resource;


public interface Constructable {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the cost of the building
     */
    Resource getResource();

    /**
     * @return the time required to construct
     */
    int getTime();

}
