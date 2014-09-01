package com.blizzard.sc2.goal;

import java.util.HashMap;
import java.util.Map;

import com.blizzard.sc2.Starcraft2Game;
import com.blizzard.sc2.constructable.buildings.Buildings;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.moves.ConstructBuilding;
import com.blizzard.sc2.moves.ConstructUnit;
import com.blizzard.sc2.moves.Wait;
import com.blizzard.sc2.time.Tick;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class GoalCalculatorTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------
    @Inject
    private Starcraft2Game game;

    @Deployment
    public static Archive<?> deployVitManagedInterface() {
        //J-
        return ShrinkWrap.create(JavaArchive.class)
                // VIT managed interface
                .addPackages(true, Filters.exclude(".*Test.*"), Starcraft2Game.class.getPackage())
                        .addClass(org.jboss.weld.bootstrap.WeldStartup.class)
                        // Bean archive deployment descriptor
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        //J+
    }

    @Test
    public final void testGoalDistanceReturnsZero() {
        Map<Units, Integer> unitsToOwn = new HashMap<>();
        unitsToOwn.put(Units.MARINE, 1);
        Starcraft2Goal goal = new Starcraft2Goal(unitsToOwn);
        game.setGoal(goal);
        // speed game up a litte
        Tick.TICK_TIME = 100;
        game.applyMove(Wait.WAIT);
        game.applyMove(new ConstructBuilding(Buildings.BARRACKS));
        game.applyMove(Wait.WAIT);
        game.applyMove(new ConstructUnit(Units.MARINE));
        game.applyMove(Wait.WAIT);
        double goalDistance = GoalCalculator.calculateGoalDistance(game);
        assertTrue(goalDistance == 0);

    }

    @Test
    public final void testCalculateGoalDistance() {
        Map<Units, Integer> unitsToOwn = new HashMap<>();
        unitsToOwn.put(Units.MARINE, 1);
        Starcraft2Goal goal = new Starcraft2Goal(unitsToOwn);
        game.setGoal(goal);
        Starcraft2Game otherGame = game.copy();
        // speed game up a litte
        Tick.TICK_TIME = 100;
        game.applyMove(Wait.WAIT);
        game.applyMove(new ConstructBuilding(Buildings.BARRACKS));
        double closerGoaldDistance = GoalCalculator.calculateGoalDistance(game);

        otherGame.applyMove(Wait.WAIT);
        otherGame.applyMove(Wait.WAIT);

        double otherGoalDistance = GoalCalculator.calculateGoalDistance(otherGame);

        assertTrue(closerGoaldDistance < otherGoalDistance);

    }

}
