package com.blizzard.sc2.algorithm;

import java.util.List;


/**
 * Template class for typical Game search problems where one has to go through all possible moves to win a game.
 *
 * @author jpoth
 */
public class GameSearch {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the list of {@link Move} to win the {@link Game} or <code>null</code> if given {@link Game} is not
     * possible to win.
     *
     * @param  game {@link Game} to win
     *
     * @return the list of {@link Move} to win the {@link Game} or <code>null</code> if given {@link Game} is not
     *         possible to win.
     */
    public static List<Move> solve(Game game) {
        for (Move move : game.getNextAvailableMoves()) {
            game.applyMove(move);
            if (game.isWin()) {
                return game.getPlayedMoves();
            }
            List<Move> solvedPath = solve(game);
            if (solvedPath != null) {
                return solvedPath;
            }
            game.undoMove(move);
        }
        return null;
    }

}
