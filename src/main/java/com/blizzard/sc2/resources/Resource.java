package com.blizzard.sc2.resources;

public class Resource implements Comparable<Resource> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private int gas;

    private int minerals;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @param minerals
     */
    public Resource(int minerals, int gas) {
        this.minerals = minerals;
        this.gas = gas;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the gas
     */
    public final int getGas() {
        return gas;
    }

    /**
     * @param gas the gas to set
     */
    public final void setGas(int gas) {
        this.gas = gas;
    }

    /**
     * @return the minerals
     */
    public final int getMinerals() {
        return this.minerals;
    }

    /**
     * @param minerals the minerals to set
     */
    public final void setMinerals(int minerals) {
        this.minerals = minerals;
    }

    @Override
    public int compareTo(Resource o) {
        if ((o.getGas() == this.getGas()) && (o.getMinerals() == this.getMinerals())) {
            return 0;
        }
        // TODO Auto-generated method stub
        if ((o.getGas() < this.getGas()) || (o.getMinerals() < this.getMinerals())) {
            return -1;
        }
        return 1;
    }

}
