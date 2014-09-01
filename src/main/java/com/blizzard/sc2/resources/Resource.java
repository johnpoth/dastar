package com.blizzard.sc2.resources;

public class Resource implements Comparable<Resource> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private double gas;

    private double minerals;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @param minerals
     */
    public Resource(double minerals, double gas) {
        this.minerals = minerals;
        this.gas = gas;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the gas
     */
    public final double getGas() {
        return this.gas;
    }

    /**
     * @return the minerals
     */
    public final double getMinerals() {
        return this.minerals;
    }

    @Override
    public int compareTo(Resource o) {
        if ((o.getGas() == this.getGas()) && (o.getMinerals() == this.getMinerals())) {
            return 0;
        }
        if (( this.getGas() < o.getGas() ) || ( this.getMinerals() < o.getMinerals() )) {
            return -1;
        }
        return 1;
    }

    public void applyDelta(Resource resource, int sign) {
        this.minerals += resource.getMinerals() * sign;
        this.gas += resource.getGas() * sign;
        if ((this.minerals < 0) || (this.gas < 0)) {
            throw new IllegalStateException("Negative resources!");
        }
    }

    public Resource copy() {
        return new Resource(minerals, gas);
    }

}
