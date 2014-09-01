package com.blizzard.sc2.constructable;

import java.util.*;

import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.buildings.Buildings;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.moves.ConstructBuilding;
import com.blizzard.sc2.resources.ResourceManager;
import com.blizzard.sc2.time.Tick;
import com.blizzard.sc2.time.TickListener;
import com.blizzard.sc2.time.TickManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BuildingManager implements TickListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------



    @Inject
    private ResourceManager resourceManager;

    @Inject
    private UnitManager unitManager;

    private List<BuildingInPorgress> buildingsInProgress = new ArrayList<>();
    private Map<Buildings, Integer> ownedBuildings = new HashMap<>();

    private Set<Buildings> possibleBuildingsConstruction=  new HashSet<>(Arrays.asList(Buildings.BARRACKS, Buildings.COMMAND_CENTER));

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------
    public BuildingManager(){

    }

    @Inject
    public BuildingManager(TickManager tickManager){
        tickManager.addTickListerner(this);
        this.ownedBuildings.put(Buildings.COMMAND_CENTER, 1);
    }

    private BuildingManager(HashSet<Buildings> possibleBuildingsConstruction, ResourceManager resourceManager, UnitManager unitManager, List<BuildingInPorgress> buildingsInProgress, Map<Buildings, Integer> ownedBuildings) {
        this.possibleBuildingsConstruction=possibleBuildingsConstruction;
        this.resourceManager=resourceManager;
        this.unitManager=unitManager;
        this.buildingsInProgress = buildingsInProgress;
        this.ownedBuildings = ownedBuildings;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public void applyTick() {
        Iterator<BuildingInPorgress> iterator = this.buildingsInProgress.iterator();
        while (iterator.hasNext()) {
            BuildingInPorgress bp = iterator.next();
            bp.applyTick();
            if (bp.isFinished()) {
                iterator.remove();
                Buildings building = bp.getConstructBuilding().getBuilding();
                buildingIsFinished(building);
                Integer integer = this.ownedBuildings.get(building);
                if (integer == null) {
                    integer = 0;
                }
                integer += 1;
                this.ownedBuildings.put(building, integer);
            }
        }
    }


    private void addBuildingUnits(Buildings building, Map<Units, Integer> map) {
        for (Units unit : building.getUnits()) {
            Integer integer = map.get(unit);
            if (integer == null) {
                integer = 0;
            }
            integer += 1;
            map.put(unit, integer);
        }
    }


    private void buildingIsFinished(Buildings building) {
        this.possibleBuildingsConstruction.add(building);
        unitManager.addBuildingUnits(building);
        this.resourceManager.deltaWorkersOnMinerals(1);
    }

    public void build(ConstructBuilding constructBuilding) {
        this.buildingsInProgress.add(new BuildingInPorgress(constructBuilding));
        this.resourceManager.deltaWorkersOnMinerals(-1);
        this.resourceManager.applyDelta(constructBuilding.getBuilding().getResource(), -1);
    }

    public BuildingManager copy(ResourceManager resourceManager,UnitManager unitManager) {
        List<BuildingInPorgress> copyUnitsInProgress = new ArrayList<>();
        for (BuildingInPorgress up : this.buildingsInProgress) {
            copyUnitsInProgress.add(up.copy());
        }
        return new BuildingManager(new HashSet<>(possibleBuildingsConstruction),resourceManager,unitManager, copyUnitsInProgress, new HashMap<>(this.ownedBuildings));
    }

    public Collection<Move> getPossibleBuildingConstruction() {
        List<Move> affordableConstructions = new ArrayList<>();
        for (Buildings building : this.possibleBuildingsConstruction) {
            if (building.getResource().compareTo(this.resourceManager.getResources()) <= 0) {
                affordableConstructions.add(new ConstructBuilding(building));
            }
        }
        return affordableConstructions;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes 
    //~ ----------------------------------------------------------------------------------------------------------------

    class BuildingInPorgress implements TickListener {

        private ConstructBuilding constructBuilding;
        private int time;
        private boolean isFinished = false;

        public BuildingInPorgress(ConstructBuilding constructBuilding) {
            this.constructBuilding = constructBuilding;
            this.time = constructBuilding.getBuilding().getTime();
        }

        private BuildingInPorgress(ConstructBuilding constructBuilding, int time, boolean isFinished) {
            this.constructBuilding = constructBuilding;
            this.time = time;
            this.isFinished = isFinished;
        }

        public BuildingInPorgress copy() {
            return new BuildingInPorgress(constructBuilding, time, isFinished);
        }

        /**
         * @return the constructBuilding
         */
        public final ConstructBuilding getConstructBuilding() {
            return this.constructBuilding;
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
