package com.blizzard.sc2;

import java.util.*;

import com.blizzard.sc2.algorithm.Game;
import com.blizzard.sc2.algorithm.Goal;
import com.blizzard.sc2.algorithm.Move;
import com.blizzard.sc2.constructable.ConstructorManager;
import com.blizzard.sc2.goal.GoalCalculator;
import com.blizzard.sc2.goal.Starcraft2Goal;
import com.blizzard.sc2.moves.ConstructBuilding;
import com.blizzard.sc2.moves.ConstructUnit;
import com.blizzard.sc2.moves.Wait;
import com.blizzard.sc2.resources.ResourceManager;
import com.blizzard.sc2.time.Tick;
import com.blizzard.sc2.time.TickListener;
import com.blizzard.sc2.time.TickManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class Starcraft2Game implements Game, TickListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(Starcraft2Game.class);

    private ResourceManager resourceManager;
    private ConstructorManager constructorManager;
    private int time = 0;
    private TickManager tickManager;
    private List<Move> moves= new ArrayList<>();
    private Starcraft2Goal starcraft2Goal;
    private int goalScore;



    /*required by cdi*/
    public Starcraft2Game(){

    }
    @Inject
    public Starcraft2Game(TickManager tickManager,ConstructorManager constructorManager,ResourceManager resourceManager){
        this.tickManager=tickManager;
        this.constructorManager=constructorManager;
        this.resourceManager=resourceManager;
        this.tickManager.addTickListerner(this);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------


    private Starcraft2Game(ResourceManager resourceManager, ConstructorManager constructorManager, int time, TickManager tickManager, List<Move> moves, Starcraft2Goal starcraft2Goal, int goalScore) {
        this.resourceManager = resourceManager;
        this.constructorManager = constructorManager;
        this.time = time;
        this.tickManager = tickManager;
        this.starcraft2Goal = starcraft2Goal;
        this.goalScore = goalScore;
        this.moves=moves;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Starcraft2Game copy() {
        TickManager copyTickManager = new TickManager();
        ResourceManager copyResourceManager = this.resourceManager.copy();
        copyTickManager.addTickListerner(copyResourceManager);
        Starcraft2Game copyStarcrat2Game = new Starcraft2Game(copyResourceManager, this.constructorManager.copy(copyResourceManager, copyTickManager), this.time, copyTickManager, new ArrayList<>(this.moves),
            this.starcraft2Goal, this.goalScore);
        copyTickManager.addTickListerner(copyStarcrat2Game);
        return copyStarcrat2Game;
    }

    /**
     * @return the goalScore
     */
    @Override
    public  int getGoalScore() {
        return this.goalScore;
    }

    @Override
    public Move getCollapsibleElement() {
        return Wait.WAIT;
    }

    /**
     * @return the resourceManager
     */
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    /**
     * @return the constructorManager
     */
    public ConstructorManager getConstructorManager() {
        return this.constructorManager;
    }

    @Override
    public List<Move> getNextAvailableMoves() {
        List<Move> possibleConstructions = this.constructorManager.getPossibleConstructions();
        possibleConstructions.add(Wait.WAIT);
        return possibleConstructions;
    }

    @Override
    public void undoMove(Move move) {
        throw new IllegalStateException("Undo move is part of a bruce force algorithm, not this!!");
    }

    @Override
    public List<Move> getPlayedMoves() {
        return this.moves;
    }

    @Override
    public boolean isWin() {
        return this.goalScore == 0;
    }

    @Override
    public void applyMove(Move move) {
        this.moves.add(move);
        if (move instanceof ConstructBuilding) {
            this.constructorManager.build((ConstructBuilding) move);
        }
        else if (move instanceof ConstructUnit) {
            this.constructorManager.build((ConstructUnit) move);
        }
        else if (move instanceof Wait) {
            this.tickManager.applyTick();
        }else{
            throw new IllegalStateException("Should be called in overloaded methods");
        }
        this.goalScore = GoalCalculator.calculateGoalDistance(this);
        return;
    }
    /*used for testing*/
    public void setGoal(Goal starcraft2Goal) {
        this.starcraft2Goal = (Starcraft2Goal) starcraft2Goal;
        this.goalScore = GoalCalculator.calculateGoalDistance(this);
    }

    @Override
    public void applyTick() {
        this.time += Tick.TICK_TIME;
    }

    @Override
    public int compareTo(Game game) {
        Starcraft2Game sc2 = (Starcraft2Game) game;
        int i = Integer.valueOf(this.goalScore).compareTo(Integer.valueOf(sc2.getGoalScore()));
        if(i == 0){
            double myR = this.getResourceManager().getResources().getGas() + this.getResourceManager().getResources().getMinerals();
            double hisR = sc2.getResourceManager().getResources().getGas() + sc2.getResourceManager().getResources().getMinerals();
            // The more the better
            return Double.valueOf(hisR).compareTo(Double.valueOf(myR));
        }
        return i;
    }

    @Override
    public void printHistory() {
        logger.info("GAME HISTORY");
        for (Move move : this.moves) {
            logger.info(move.toString());
        }

    }

    public Starcraft2Goal getGoal() {
        return this.starcraft2Goal;
    }

    public int getTime() {
        return this.time;
    }



}
