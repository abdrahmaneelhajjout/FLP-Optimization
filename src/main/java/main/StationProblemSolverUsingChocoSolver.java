package main;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationProblemSolverUsingChocoSolver {
    private final double[][] distances;
    private final int demandNodesCount;
    private final int candidateNodesCount;
    Map<Pair<Node, Node>, Double> distancesMap;
    List<Node> demandNodes;
    Map<Node, Node> nodeMapping;
    List<Node> candidateNodes;
    public  StationProblemSolverUsingChocoSolver(Map<Pair<Node, Node>, Double> distancesMap, List<Node> demandNodes, List<Node> candidateNodes) {
        this.distancesMap = distancesMap;
        this.demandNodes = demandNodes;
        this.candidateNodes = candidateNodes;

        this.distances = createDistanceMatrix(distancesMap, demandNodes, candidateNodes);
        this.demandNodesCount = demandNodes.size();
        this.candidateNodesCount = candidateNodes.size();
    }

    public  Map<Node, Node>  solve(int p) {
        // Create the model.
        Model model = new Model("Facility Location Problem");

        // Variables: x[i][j] = 1 if demand node i is assigned to candidate node j, 0 otherwise.
        IntVar[][] x = model.intVarMatrix(demandNodesCount, candidateNodesCount, 0, 1);

        // Variables: y[j] = 1 if candidate node j is a facility, 0 otherwise.
        IntVar[] y = model.intVarArray(candidateNodesCount, 0, 1);

        // Convert distances to integers
        int[][] distancesInt = new int[demandNodesCount][candidateNodesCount];
        for (int i = 0; i < demandNodesCount; i++) {
            for (int j = 0; j < candidateNodesCount; j++) {
                distancesInt[i][j] = (int) distances[i][j];
            }
        }

        // Objective function: Minimize the total distance from each demand node to its assigned facility.
        IntVar[] distancesVar = new IntVar[demandNodesCount];
        for (int i = 0; i < demandNodesCount; i++) {
            distancesVar[i] = model.intVar(distancesInt[i]);
        }

        IntVar objective = model.intVar("objective", 0, IntVar.MAX_INT_BOUND);
        model.sum(distancesVar, "=", objective).post();
        model.setObjective(Model.MINIMIZE, objective);

        // Constraint 1: Each demand node is assigned to exactly one facility.
        for (int i = 0; i < demandNodesCount; i++) {
            model.sum(x[i], "=", 1).post();
        }

        // Constraint 2: Assign demand nodes only to candidate nodes that are facilities.
        for (int i = 0; i < demandNodesCount; i++) {
            for (int j = 0; j < candidateNodesCount; j++) {
                model.ifThen(
                        model.arithm(y[j], "=", 0),
                        model.arithm(x[i][j], "=", 0)
                );
            }
        }

        // Constraint 3: Select the best p facilities.
        model.sum(y, "=", p).post();

        // Solve the problem and print the solution.
        Solution solution = model.getSolver().findOptimalSolution(objective, Model.MINIMIZE);
        if (solution != null) {
            System.out.println("Optimal solution found.");
            nodeMapping = new HashMap<>();

            for (int i = 0; i < demandNodesCount; i++) {
                for (int j = 0; j < candidateNodesCount; j++) {
                    if (solution.getIntVal(x[i][j]) == 1) {
                        System.out.println("Demand node " + i + " assigned to facility node " + j);

                        Node demandNode = demandNodes.get(i);
                        Node candidateNode = candidateNodes.get(j);
                        nodeMapping.put(demandNode, candidateNode);
                    }
                }
            }
        } else {
            System.out.println("No optimal solution found.");
        }

        return nodeMapping;
    }


    private double[][] createDistanceMatrix(Map<Pair<Node, Node>, Double> distances, List<Node> demandNodes, List<Node> candidateNodes) {
        int demandNodesCount = demandNodes.size();
        int candidateNodesCount = candidateNodes.size();
        double[][] distanceMatrix = new double[demandNodesCount][candidateNodesCount];

        for (int i = 0; i < demandNodesCount; i++) {
            for (int j = 0; j < candidateNodesCount; j++) {
                Pair<Node, Node> nodePair = new Pair<>(demandNodes.get(i), candidateNodes.get(j));
                distanceMatrix[i][j] = distances.get(nodePair);
            }
        }

        return distanceMatrix;
    }
    public void showMapOnConsole(Map<Pair<Node, Node>, Double> distances) {
        for (Map.Entry<Pair<Node, Node>, Double> entry : distances.entrySet()) {
            Pair<Node, Node> nodes = entry.getKey();
            Double distance = entry.getValue();
            System.out.println(nodes.getKey() + " -> " + nodes.getValue() + " : " + distance);
        }
    }

}
