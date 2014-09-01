package com.blizzard.sc2.goal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.blizzard.sc2.Starcraft2Game;
import com.blizzard.sc2.constructable.ConstructorManager;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.resources.Resource;
import com.blizzard.sc2.resources.ResourceManager;


public class GoalCalculator {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * <p>This is where the algorithm needs to be VERY efficient in guiding the program towards good moves if we want
     * the running time to go down.</p>
     *
     * return 0 if state has been achieved.
     *
     * @param  game
     *
     * @return
     */
    public static int calculateGoalDistance(Starcraft2Game game) {
        Map<Units, Integer> goalUnits = game.getGoal().getUnits();

        ConstructorManager constructorManager = game.getConstructorManager();
        ResourceManager resourceManager = game.getResourceManager();
        Map<Units, Integer> unitRemainingToGet = getUnitsRemainingToGet(goalUnits, constructorManager);
        // check if win!
        if (unitRemainingToGet.isEmpty()) {
            return 0;
        }

        int secondsNeededToGetResources = getSecondsToGetResources(resourceManager, unitRemainingToGet);
        Map<Units, Integer> unitsConstructionRate = constructorManager.getUnitsConstructionRate();
        int secondsNeededToBuildBuildingAndUnit = getSecondsNeededToGetUnits(unitRemainingToGet, unitsConstructionRate);

        return game.getTime() + Math.max(secondsNeededToBuildBuildingAndUnit, secondsNeededToGetResources) ;
    }

    // TODO: this does not include building requirements !!! That is you needed certain buildings to build others etc ..
    private static int getSecondsNeededToGetUnits(Map<Units, Integer> unitRemainingToGet, Map<Units, Integer> unitsConstructionRate) {
        int secondsNeededToBuildBuildingAndUnit = 0;
        for (Entry<Units, Integer> entry : unitRemainingToGet.entrySet()) {
            Units unit = entry.getKey();
            Integer numGoalUnit = entry.getValue();

            Integer unitConstructionRate = unitsConstructionRate.get(unit);
            if (unitConstructionRate == null) {
                unitConstructionRate = 1;
                secondsNeededToBuildBuildingAndUnit += unit.getBuilding().getTime();
            }

            int rate = (int) Math.ceil(numGoalUnit.doubleValue() / unitConstructionRate.doubleValue());
            secondsNeededToBuildBuildingAndUnit += rate * unit.getTime();
        }
        return secondsNeededToBuildBuildingAndUnit;
    }

    private static int getSecondsToGetResources(ResourceManager resourceManager, Map<Units, Integer> unitRemainingToGet) {
        Resource totalResourceNeeded = new Resource(0, 0);
        for (Entry<Units, Integer> entry : unitRemainingToGet.entrySet()) {
            Units unit = entry.getKey();
            Integer numGoalUnit = entry.getValue();
            totalResourceNeeded.applyDelta(unit.getResource(), numGoalUnit);
        }

        double gasRate = resourceManager.getGasRatePerSecond();
        double mineralRate = resourceManager.getMineralRatePerSecond();

        int secondsNeededToGetResources = Math.max((int) Math.ceil(totalResourceNeeded.getGas() / gasRate), (int) Math.ceil(totalResourceNeeded.getMinerals() / mineralRate));
        return secondsNeededToGetResources;
    }

    private static Map<Units, Integer> getUnitsRemainingToGet(Map<Units, Integer> goalUnits, ConstructorManager constructorManager) {
        Map<Units, Integer> unitRemainingToGet = new HashMap<>();
        Map<Units, Integer> unitsOwned = constructorManager.getUnits();
        Map<Units, Integer> unitsInConstruction = constructorManager.getUnitsInConsturction();
        boolean isWin = true;
        for (Entry<Units, Integer> entry : goalUnits.entrySet()) {
            Units unit = entry.getKey();
            Integer numGoalUnit = entry.getValue();

            Integer numUnitsOwned = unitsOwned.get(unit);
            if (numUnitsOwned == null) {
                numUnitsOwned = 0;
            }
            Integer numUnitsInConsturction = unitsInConstruction.get(unit);
            if (numUnitsInConsturction == null) {
                numUnitsInConsturction = 0;
            }

            numUnitsOwned += numUnitsInConsturction;

            int unitsToGet = numGoalUnit - numUnitsOwned;
            if (unitsToGet > 0) {
                isWin = false;
                unitRemainingToGet.put(unit, unitsToGet);
            }
        }
        return unitRemainingToGet;
    }

}
