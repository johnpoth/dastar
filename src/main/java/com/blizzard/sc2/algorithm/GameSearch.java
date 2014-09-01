package com.blizzard.sc2.algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blizzard.sc2.graph.Edge;
import com.blizzard.sc2.graph.Edges;
import com.blizzard.sc2.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blizzard.sc2.graph.Graph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


/**
 * Template class for typical Game search problems where one has to go through all possible moves to win a game.
 *
 * @author jpoth
 */
public class GameSearch {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(GameSearch.class);

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

    /**
     * Returns the list of {@link Move} to win the {@link Game} or <code>null</code> if given {@link Game} is not
     * possible to win.
     *
     * @param  game {@link Game} to win
     *
     * @return the list of {@link Move} to win the {@link Game} or <code>null</code> if given {@link Game} is not
     *         possible to win.
     */
    public static List<Move> solveAStarWay(Game game) throws Exception {
        Move collapsibleElement = game.getCollapsibleElement();
        int counterState= 1;
        Graph graph = new Graph();
        MinHeap<GameNode> games = new MinHeap<GameNode>();
        Node start = new Node();
        start.setLabel("START");
        start.setMoves("");
        graph.getNodes().add(start);
        GameNode gm = new GameNode(game,start);
        games.add(gm);
        while (true) {
            GameNode gameNode = games.pop();
            Node popedNode = gameNode.getNode();
            Game popGame=gameNode.getGame();
            if (logger.isDebugEnabled()) {
                popGame.printHistory();
            }
            if (popGame == null) {
                throw new IllegalStateException("Could not find path");
            }

            if (popGame.isWin()) {
                writeGraph(graph);
                return popGame.getPlayedMoves();
            }




            for (Move move : popGame.getNextAvailableMoves()) {
                if(gameNode.getForbiddenJustsu().contains(move)){
                    continue;
                }


                Game newGame = popGame.copy();

                newGame.applyMove(move);
                Node newGameNode = new Node();
                String nodeLabel = String.valueOf(counterState);
                if(newGame.isWin()){
                    nodeLabel = "END";
                }
                newGameNode.setLabel(nodeLabel);
                newGameNode.setMoves(newGame.getPlayedMoves().toString());
                Edge newEdge = new Edge();
                newEdge.setLabel(move.toString());
                newEdge.setStartNode(popedNode.getLabel());
                newEdge.setEndNode(newGameNode.getLabel());


                Edges edges = popedNode.getEdges();
                if(edges == null){
                    edges = new Edges();
                    popedNode.setEdges(edges);
                }
                edges.getEdge().add(newEdge);
                graph.getNodes().add(newGameNode);
                if (newGame.isWin()) {
                    writeGraph(graph);
                    return newGame.getPlayedMoves();
                }

                Set<Move> forbiddenJustsu  = new HashSet<>();
                if(move.equals(collapsibleElement)){
                    forbiddenJustsu.addAll(popGame.getNextAvailableMoves());
                    forbiddenJustsu.remove(collapsibleElement);
                }
                games.add(new GameNode(newGame,newGameNode,forbiddenJustsu));
                // add end Node
                addEndEdge(newGame, nodeLabel,newGameNode);
                counterState++;
            }
        }

    }

    private static void addEndEdge(Game newGame, String nodeLabel,Node gameNode) {
        Edge edgeToEnd = new Edge();
        edgeToEnd.setLabel(String.valueOf(newGame.getGoalScore()));
        edgeToEnd.setStartNode(nodeLabel);
        edgeToEnd.setEndNode("END");
        Edges edges = gameNode.getEdges();
        if(edges == null){
            edges = new Edges();
            gameNode.setEdges(edges);
        }
        edges.getEdge().add(edgeToEnd);
    }

    private static void writeGraph(Graph graph) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(Graph.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(graph,new FileOutputStream("src/test/resources/graph-generator/graph.xml"));
    }

    static class GameNode implements Comparable<GameNode>{
        private final Game game;
        private final Node node;
        private final Set<Move> forbiddenJustsu;

        public Set<Move> getForbiddenJustsu() {
            return forbiddenJustsu;
        }


        GameNode(Game game, Node node) {
            this.game = game;
            this.node = node;
            this.forbiddenJustsu = new HashSet<>();
        }

        public GameNode(Game game, Node node, Set<Move> forbiddenJustsu) {
            this.game = game;
            this.node = node;
            this.forbiddenJustsu = forbiddenJustsu;
        }

        @Override
        public int compareTo(GameNode o) {
            return this.game.compareTo(o.getGame());
        }

        public Node getNode() {
            return node;
        }

        public Game getGame() {
            return game;
        }
    }

}
