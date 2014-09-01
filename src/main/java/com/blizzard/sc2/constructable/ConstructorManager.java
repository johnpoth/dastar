package com.blizzard.sc2.constructable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.buildings.Buildings;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.moves.ConstructBuilding;
import com.blizzard.sc2.moves.ConstructUnit;
import com.blizzard.sc2.resources.ResourceManager;
import com.blizzard.sc2.time.TickManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConstructorManager {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Inject
    private  ResourceManager resourceManager;


    @Inject
    private  UnitManager unitManager;
    @Inject
    private  BuildingManager buildingManager;

    // these are used to calculate the starcraft2 goal distance
    private  Map<Units, Integer> unitsConstructionRate= new HashMap<>();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------
    public ConstructorManager(){
        this.unitsConstructionRate.put(Units.WORKER, 1);
    }



    private ConstructorManager(ResourceManager resourceManager, TickManager tickManager, UnitManager unitManagerToCopy,
        BuildingManager buildingManagerToCopy, Map<Units, Integer> unitsConstructionRate) {
        this.resourceManager = resourceManager;
        this.unitsConstructionRate = unitsConstructionRate;
        this.unitManager = unitManagerToCopy.copy(resourceManager);
        this.buildingManager = buildingManagerToCopy.copy(resourceManager,this.unitManager);
        tickManager.addTickListerner(this.unitManager);
        tickManager.addTickListerner(this.buildingManager);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @return the unitsConstructionRate
     */
    public Map<Units, Integer> getUnitsConstructionRate() {
        return this.unitsConstructionRate;
    }

    public List<Move> getPossibleConstructions() {
        List<Move> affordableConstructions = new ArrayList<>();

        affordableConstructions.addAll(this.buildingManager.getPossibleBuildingConstruction());
        affordableConstructions.addAll(this.unitManager.getPossibleUnitConstruction());

        return affordableConstructions;
    }

    public void build(ConstructBuilding constructBuilding) {

        this.buildingManager.build(constructBuilding);
        addBuildingUnits(constructBuilding.getBuilding());
    }

    void addBuildingUnits(Buildings building) {
        for (Units unit : building.getUnits()) {
            Integer integer = this.unitsConstructionRate.get(unit);
            if (integer == null) {
                integer = 0;
            }
            integer += 1;
            this.unitsConstructionRate.put(unit, integer);
        }
    }

    public void build(ConstructUnit constructUnit) {
        this.unitManager.build(constructUnit);
    }




    public Map<Units, Integer> getUnits() {
        return this.unitManager.getUnits();
    }

    public Map<Units, Integer> getUnitsInConsturction() {
        return this.unitManager.getUnitsInConsturction();
    }

    public ConstructorManager copy(ResourceManager copyResourceManager, TickManager copyTickManager) {
        ConstructorManager copyConstructorManager = new ConstructorManager(copyResourceManager,  copyTickManager,
            this.unitManager, this.buildingManager, new HashMap<>(this.unitsConstructionRate));
        return copyConstructorManager;
    }


}
