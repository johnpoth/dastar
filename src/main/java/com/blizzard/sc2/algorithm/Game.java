package com.blizzard.sc2.algorithm;

import java.util.List;


public interface Game extends Comparable<Game> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    List<Move> getNextAvailableMoves();

    void undoMove(Move move);

    List<Move> getPlayedMoves();

    boolean isWin();

    void applyMove(Move move);

    Game copy();

    void printHistory();

    int getGoalScore();

    Move getCollapsibleElement();

}
