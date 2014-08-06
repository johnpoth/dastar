package com.blizzard.sc2.algorithm;

import java.util.List;


public interface Game {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    List<Move> getNextAvailableMoves();

    void undoMove(Move move);

    List<Move> getPlayedMoves();

    boolean isWin();

    void applyMove(Move move);
}
