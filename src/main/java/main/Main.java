package main;

import javafx.util.Pair;
import main.utils.GeoJSONCreator;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            NodeDataReader nodesReader = new NodeDataReader();
            //this line is used one time to get the nodes from the json files and then calculate distances
            // and save them to a json file, in order to preserve computation resources
            //nodesReader.setNodesAndDistancesMap();
            //nodesReader.showMatrixGui();

            Map<Pair<Node, Node>, Double> nodesAndDistancesMap = nodesReader.getNodesAndDistancesMap();
            StationProblemSolverUsingChocoSolver solver = new StationProblemSolverUsingChocoSolver(nodesAndDistancesMap, nodesReader.getDemandNodes(), nodesReader.getCandidateNodes());
            Map<Node, Node> demandNodeToStationMap = solver.solve(3);


            String stringLinesFilePath = "D:\\Educations\\Master\\M2\\Stage\\Recherche\\Maps\\StringLines.json";
            String pointsFilePath = "D:\\Educations\\Master\\M2\\Stage\\Recherche\\Maps\\ChosenPoints.json";

            // Create the GeoJSON string for LineString
            String lineStringGeoJson = GeoJSONCreator.createLineStringGeoJSON(demandNodeToStationMap);
            GeoJSONCreator.saveGeoJSONToFile(lineStringGeoJson, stringLinesFilePath);

            // get the chosen List of candidate station from the Map
            List<Node> chosenStationNodes = GeoJSONCreator.convertConnectionsMapToList(demandNodeToStationMap);
            // Create the GeoJSON string for chosen candidate points
            String pointGeoJson = GeoJSONCreator.createPointsGeoJSON(chosenStationNodes);
            GeoJSONCreator.saveGeoJSONToFile(pointGeoJson, pointsFilePath);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
