package com.blizzard.sc2;

import java.util.*;

import com.blizzard.sc2.algorithm.Game;
import com.blizzard.sc2.algorithm.GameSearch;
import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.buildings.Buildings;
import com.blizzard.sc2.constructable.units.Units;
import com.blizzard.sc2.goal.Starcraft2Goal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.blizzard.sc2.moves.ConstructBuilding;
import com.blizzard.sc2.moves.ConstructUnit;
import com.blizzard.sc2.moves.Wait;
import com.blizzard.sc2.time.Tick;
import org.fest.assertions.Assert;
import org.jboss.arquillian.container.impl.ContainerImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class Starcrat2GameTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(Starcrat2GameTest.class);

    @Inject
    private Starcraft2Game game;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Deployment
    public static Archive<?> deployVitManagedInterface() {
        //J-
        return ShrinkWrap.create(JavaArchive.class)
                // VIT managed interface
                .addPackages(true, Filters.exclude(".*Test.*"), Starcraft2Game.class.getPackage())
                .addPackages(true, Filters.exclude(".*Test.*"), ContainerImpl.class.getPackage())
                        // Bean archive deployment descriptor
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        //J+
    }

    @Test
    public final void testNextAvailableMoves() throws Exception {
        Map<Units, Integer> unitsToOwn = new HashMap<>();
        Starcraft2Goal goal = new Starcraft2Goal(unitsToOwn);

        game.setGoal(goal);

        /** test worker and wait*/
        List<Move> availableMoves = game.getNextAvailableMoves();
        List<Move> expectedAvailableMoves = new ArrayList<>();
        expectedAvailableMoves.add(Wait.WAIT);
        expectedAvailableMoves.add(new ConstructUnit(Units.WORKER));
        org.junit.Assert.assertTrue(availableMoves.containsAll(expectedAvailableMoves) && expectedAvailableMoves.containsAll(availableMoves));

        /** test barracks and command center*/
        Tick.TICK_TIME =400;
        game.applyMove(Wait.WAIT);
        availableMoves = game.getNextAvailableMoves();
        expectedAvailableMoves.add(new ConstructBuilding(Buildings.BARRACKS));
        expectedAvailableMoves.add(new ConstructBuilding(Buildings.COMMAND_CENTER));
        org.junit.Assert.assertTrue(availableMoves.containsAll(expectedAvailableMoves) && expectedAvailableMoves.containsAll(availableMoves));


    }

    @Test
    @InSequence(1)
    public final void testEmptyGoalStarcrat2Game() throws Exception {
        Map<Units, Integer> unitsToOwn = new HashMap<>();
        Starcraft2Goal goal = new Starcraft2Goal(unitsToOwn);

        game.setGoal(goal);
        List<Move> solveAStarWay = GameSearch.solveAStarWay(game);

        assertTrue(solveAStarWay.isEmpty());
        for (Move move : solveAStarWay) {
            System.out.println(move);
        }
    }

    @Test
    @InSequence(2)
    public final void testOnlyWorkers() throws Exception {

        Map<Units, Integer> unitsToOwn = new HashMap<>();

        unitsToOwn.put(Units.WORKER, 9);

        Starcraft2Goal goal = new Starcraft2Goal(unitsToOwn);

        game.setGoal(goal);
        List<Move> solveAStarWay = GameSearch.solveAStarWay(game);
        List<Move> expectedMoves = Arrays.asList(new ConstructUnit(Units.WORKER),Wait.WAIT,Wait.WAIT,Wait.WAIT,new ConstructUnit(Units.WORKER));
        assertArrayEquals(expectedMoves.toArray(), solveAStarWay.toArray());
        printHistory(solveAStarWay);

    }

    @Test
    @InSequence(3)
    public final void testOnlyWorkersMax() throws Exception {

        Map<Units, Integer> unitsToOwn = new HashMap<>();

        unitsToOwn.put(Units.WORKER, 10);
//        unitsToOwn.put(Units.MARINE, 1);
        Starcraft2Goal goal = new Starcraft2Goal(unitsToOwn);

        game.setGoal(goal);
        List<Move> solveAStarWay = GameSearch.solveAStarWay(game);
        printHistory(solveAStarWay);

    }

    public void printHistory(List<Move> moves) {
        logger.info("GAME HISTORY");
        moves.removeAll(Arrays.asList(Wait.WAIT));
        for (Move move : moves) {
            logger.info(move.toString());
        }

    }

}
