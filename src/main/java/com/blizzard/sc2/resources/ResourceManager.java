package com.blizzard.sc2.resources;

import com.blizzard.sc2.time.Tick;
import com.blizzard.sc2.time.TickListener;
import com.blizzard.sc2.time.TickManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Measuring resource income: http://www.teamliquid.net/forum/sc2-strategy/140055-scientifically-measuring-mining-speed
 * 3) Calculating income rate Using the equations (i)-(iv) and the empirically measured variables x and y, one can
 * calculate expected income for SCVs on a single mineral patch or gas geyser: - One SCV on one mineral patch harvests
 * 39-45 minerals per game minute, depending on distance. - Two SCVs on one mineral patch harvest 78-90 minerals per
 * game minute, depending on distance. - Three SCVs on one mineral patch harvest ~102 minerals per game minute. This is
 * fully saturated and does not depend on distance. - One SCV on gas harvests 33-42 gas per game minute, depending on
 * distance. - Two SCVs on gas harvest 67-84 gas per game minute, depending on distance. - Three SCVs on gas harvest
 * 101-114 gas per game minute, depending on distance. In the case of far-diagonal gas placement you will need 4 SCVs
 * for full saturation. - Four SCVs on gas harvest ~114 gas per game minute. This is fully saturated and does not depend
 * on distance. - A fully saturated base with 8 minerals and 2 gas will harvest ~816 minerals and ~228 gas per game
 * minute.
 *
 */
@ApplicationScoped
public class ResourceManager implements TickListener{

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private Resource resource = new Resource(100, 0);
    private int numWorkersMinerals = 7;
    private int numWorkersGas = 0;
    private int mineralPatches= 8;

    private int gasPatches = 0;

    private double workerMineralRatePerSecond = 39.0 / 60.0;
    private double workerGasRatePerSecond = 33.0 / 60.0;


    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public ResourceManager(){

    }

    @Inject
    public ResourceManager(TickManager tickManager) {

        tickManager.addTickListerner(this);
    }


    public ResourceManager(Resource resource, int numWorkersMinerals, int numWorkersGas, int mineralPatches, int gasPatches) {
        this.resource = resource;
        this.numWorkersMinerals = numWorkersMinerals;
        this.numWorkersGas = numWorkersGas;
        this.mineralPatches = mineralPatches;
        this.gasPatches = gasPatches;
    }


    private ResourceManager(Resource resource, int numWorkersMinerals, int numWorkersGas, int mineralPatches, int gasPatches, double workerMineralRatePerSecond, double workerGasRatePerSecond) {
        this.resource = resource;
        this.numWorkersMinerals = numWorkersMinerals;
        this.numWorkersGas = numWorkersGas;
        this.mineralPatches = mineralPatches;
        this.gasPatches = gasPatches;
        this.workerMineralRatePerSecond = workerMineralRatePerSecond;
        this.workerGasRatePerSecond = workerGasRatePerSecond;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public void applyTick() {
        double mineralIncrease = getMineralRatePerSecond() * Tick.TICK_TIME;
        double gasIncrease = getGasRatePerSecond()* Tick.TICK_TIME;
        this.applyDelta(new Resource(mineralIncrease, gasIncrease), 1);
    }

    public Resource getResources() {
        return this.resource;
    }

    public void deltaWorkersOnMinerals(int i) {
        this.numWorkersMinerals += i;
        if (this.numWorkersMinerals < 0) {
            throw new IllegalStateException("Negative number of workers!");
        }

    }

    public void applyDelta(Resource resource, int i) {

        this.resource.applyDelta(resource, i);

    }

    public double getGasRatePerSecond() {
        double effectiveWorkers;
        effectiveWorkers = Math.max(this.gasPatches * 3, this.numWorkersGas);
        double gasIncrease = effectiveWorkers * this.workerGasRatePerSecond;
        return gasIncrease;
    }

    public double getMineralRatePerSecond() {
        double effectiveWorkers = Math.min(this.mineralPatches * 3, this.numWorkersMinerals);
        double mineralIncrease = effectiveWorkers * this.workerMineralRatePerSecond;
        return mineralIncrease;
    }

    public ResourceManager copy() {
        return new ResourceManager(this.resource.copy(), this.numWorkersMinerals, this.numWorkersGas, this.mineralPatches, this.gasPatches);
    }


}
