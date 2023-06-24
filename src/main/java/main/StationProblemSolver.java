package main;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.*;

public class StationProblemSolver {
    static {
        Loader.loadNativeLibraries();
    }

    private final double[][] distances;
    private final int demandNodesCount;
    private final int candidateNodesCount;

    public StationProblemSolver(double[][] distanceMatrix) {
        this.distances = distanceMatrix;
        this.demandNodesCount = distanceMatrix.length;
        this.candidateNodesCount = distanceMatrix[0].length;
    }

    public void solve(int p) {
        // Create the solver.
        MPSolver solver = MPSolver.createSolver("CBC");

        // Variables: x[i][j] = 1 if demand node i is assigned to candidate node j, 0 otherwise.
        MPVariable[][] x = new MPVariable[demandNodesCount][candidateNodesCount];
        for (int i = 0; i < demandNodesCount; i++) {
            for (int j = 0; j < candidateNodesCount; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "][" + j + "]");
            }
        }

        // Variables: y[j] = 1 if candidate node j is a facility, 0 otherwise.
        MPVariable[] y = new MPVariable[candidateNodesCount];
        for (int j = 0; j < candidateNodesCount; j++) {
            y[j] = solver.makeIntVar(0, 1, "y[" + j + "]");
        }

        // Objective function: Minimize the total distance from each demand node to its assigned facility.
        MPObjective objective = solver.objective();
        for (int i = 0; i < demandNodesCount; i++) {
            for (int j = 0; j < candidateNodesCount; j++) {
                objective.setCoefficient(x[i][j], distances[i][j]);
            }
        }
        objective.setMinimization();

        // Constraint 1: Each demand node is assigned to exactly one facility.
        for (int i = 0; i < demandNodesCount; i++) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "demand_assignment[" + i + "]");
            for (int j = 0; j < candidateNodesCount; j++) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }

        // Constraint 2: Assign demand nodes only to candidate nodes that are facilities.
        for (int i = 0; i < demandNodesCount; i++) {
            for (int j = 0; j < candidateNodesCount; j++) {
                MPConstraint constraint = solver.makeConstraint(0, 0, "assign_demand[" + i + "][" + j + "]");
                constraint.setCoefficient(x[i][j], 1);
                constraint.setCoefficient(y[j], -1);
            }
        }

        // Constraint 3: Select the best p facilities.
        for (int j = 0; j < candidateNodesCount; j++) {
            MPConstraint constraint = solver.makeConstraint(0, 1, "select_facility[" + j + "]");
            constraint.setCoefficient(y[j], 1);
        }

        // Constraint 4: Limit the number of selected facilities to p.
        MPConstraint pConstraint = solver.makeConstraint(p, p, "limit_facilities");
        for (int j = 0; j < candidateNodesCount; j++) {
            pConstraint.setCoefficient(y[j], 1);
        }

        // Solve the problem and print the solution.
        MPSolver.ResultStatus status = solver.solve();

        if (status == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found.");
            for (int i = 0; i < demandNodesCount; i++) {
                for (int j = 0; j < candidateNodesCount; j++) {
                    if (x[i][j].solutionValue() == 1) {
                        System.out.println("Demand node " + i + " assigned to facility node " + j);
                    }
                }
            }
        } else {
            System.out.println("No optimal solution found.");
        }
    }
}
