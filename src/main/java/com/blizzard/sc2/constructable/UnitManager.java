package com.blizzard.sc2.constructable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.buildings.Buildings;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.moves.ConstructUnit;
import com.blizzard.sc2.resources.ResourceManager;
import com.blizzard.sc2.time.Tick;
import com.blizzard.sc2.time.TickListener;
import com.blizzard.sc2.time.TickManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UnitManager implements TickListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------



    @Inject
    private ResourceManager resourceManager;
    private List<UnitInPorgress> unitsInProgress = new ArrayList<>();
    private Map<Units, Integer> ownedUnits = new HashMap<>();
    //this is needed to known, very fast, how many units of what type are in constructions to calculate goal state correctly
    private Map<Units, Integer> numUnitsInProgress = new HashMap<>();
    private  Map<Units, Integer> possibleUnitsConstruction = new HashMap<>();

    public UnitManager(){

    }

    @Inject
    public UnitManager(TickManager tickManager){
        this.possibleUnitsConstruction.put(Units.WORKER, 1);
        tickManager.addTickListerner(this);
        this.ownedUnits.put(Units.WORKER, 7);
    }

    private UnitManager(ResourceManager resourceManager,List<UnitInPorgress> unitsInProgress, Map<Units, Integer> ownedUnits, Map<Units, Integer> numUnitsInProgress, Map<Units, Integer> possibleUnitsConstruction) {
        this.resourceManager=resourceManager;
        this.unitsInProgress = unitsInProgress;
        this.ownedUnits = ownedUnits;
        this.numUnitsInProgress = numUnitsInProgress;
        this.possibleUnitsConstruction = possibleUnitsConstruction;
    }




    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public void applyTick() {
        Iterator<UnitInPorgress> iterator = this.unitsInProgress.iterator();
        while (iterator.hasNext()) {
            UnitInPorgress up = iterator.next();
            up.applyTick();
            if (up.isFinished()) {
                iterator.remove();
                Units unit = up.getConstructUnit().getUnit();
                unitIsFinished(unit);
                Integer integer = this.ownedUnits.get(unit);
                if (integer == null) {
                    integer = 0;
                }
                integer += 1;
                this.ownedUnits.put(unit, integer);

                integer = this.numUnitsInProgress.get(unit);
                integer -= 1;
                this.numUnitsInProgress.put(unit, integer);
            }
        }

    }

    private void unitIsFinished(Units unit) {
        addBuildingUnits(unit.getBuilding());
        if (unit.equals(Units.WORKER)) {
            this.resourceManager.deltaWorkersOnMinerals(1);
        }
    }

    void addBuildingUnits(Buildings building) {
        for (Units unit : building.getUnits()) {
            Integer integer = this.possibleUnitsConstruction.get(unit);
            if (integer == null) {
                integer = 0;
            }
            integer += 1;
            this.possibleUnitsConstruction.put(unit, integer);
        }
    }

    public void build(ConstructUnit constructUnit) {
        this.resourceManager.applyDelta(constructUnit.getUnit().getResource(), -1);
        this.unitsInProgress.add(new UnitInPorgress(constructUnit));
        Units unit = constructUnit.getUnit();
        Integer integer = this.numUnitsInProgress.get(unit);
        if (integer == null) {
            integer = 0;
        }
        integer += 1;
        this.numUnitsInProgress.put(unit, integer);


        for ( Units foo : constructUnit.getUnit().getBuilding().getUnits()) {
            integer = this.possibleUnitsConstruction.get(foo);
            this.possibleUnitsConstruction.put(foo, integer - 1);
        }
    }

    public Map<Units, Integer> getUnits() {
        return this.ownedUnits;
    }

    public Map<Units, Integer> getUnitsInConsturction() {
        return this.numUnitsInProgress;
    }

    public UnitManager copy(ResourceManager resourceManager) {
        List<UnitInPorgress> copyUnitsInProgress = new ArrayList<>();
        for (UnitInPorgress up : this.unitsInProgress) {
            copyUnitsInProgress.add(up.copy());
        }
        return new UnitManager( resourceManager,copyUnitsInProgress, new HashMap<>(this.ownedUnits), new HashMap<>(this.numUnitsInProgress),new HashMap<>(this.possibleUnitsConstruction));
    }

    public List<Move> getPossibleUnitConstruction() {
        List<Move> affordableConstructions = new ArrayList<>();
        for (Map.Entry<Units, Integer> entry : this.possibleUnitsConstruction.entrySet()) {
            if (entry.getValue() <= 0) {
                continue;
            }
            Units unit = entry.getKey();
            if (unit.getResource().compareTo(this.resourceManager.getResources()) <= 0) {
                affordableConstructions.add(new ConstructUnit(unit));
            }
        }
        return affordableConstructions;
    }


    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes 
    //~ ----------------------------------------------------------------------------------------------------------------

    class UnitInPorgress implements TickListener {

        private final ConstructUnit constructUnit;

        private int time;

        private boolean isFinished = false;

        public UnitInPorgress(ConstructUnit constructUnit) {
            this.constructUnit = constructUnit;
            this.time = constructUnit.getUnit().getTime();
        }

        private UnitInPorgress(ConstructUnit constructUnit, int time, boolean isFinished) {
            this.constructUnit = constructUnit;
            this.time = time;
            this.isFinished = isFinished;
        }

        public UnitInPorgress copy() {
            return new UnitInPorgress(constructUnit, time, isFinished);
        }

        /**
         * @return the constructUnit
         */
        public final ConstructUnit getConstructUnit() {
            return this.constructUnit;
        }

        /**
         * @return the isFinished
         */
        public final boolean isFinished() {
            return this.isFinished;
        }

        @Override
        public void applyTick() {
            this.time -= Tick.TICK_TIME;
            if (this.time <= 0) {
                this.isFinished = true;
            }
        }

    }

}
