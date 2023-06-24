package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;
import main.utils.GraphicService;
import main.utils.PMedianProblem;
import main.utils.Services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.utils.Services.readNodesAndDistancesMapFromJson;

public class NodeDataReader {
    private final ObjectMapper objectMapper;

    public NodeDataReader() {
        this.objectMapper = new ObjectMapper();
    }

    public Pair<Integer,Integer> getNodesCountAsPair() throws IOException {
        return new Pair<>(extractDemandNodes().size(), extractCandidateNodes().size());
    }

    private JsonNode readJsonFile(String filePath) throws IOException {
        File file = new File(filePath);
        return objectMapper.readTree(file);
    }

    private List<Node> extractDemandNodes() throws IOException {
        JsonNode demandPointsJson = readJsonFile(getClass().getClassLoader().getResource("Demand_Points.geojson").getPath());

        return getNodes(demandPointsJson);

    }


    private List<Node> extractCandidateNodes() throws IOException {
        JsonNode candidateCentroidsJson = readJsonFile(getClass().getClassLoader().getResource("Candidate_Centroids.geojson").getPath());

        return getNodes(candidateCentroidsJson);
    }

    public List<Node> getDemandNodes() throws IOException {
        return extractDemandNodes();
    }

    public List<Node> getCandidateNodes() throws IOException {
        return extractCandidateNodes();
    }

    private List<Node> getNodes(JsonNode candidateCentroidsJson) {
        List<Node> candidateNodes = new ArrayList<>();

        for (JsonNode feature : candidateCentroidsJson.get("features")) {
            JsonNode geometry = feature.get("geometry");
            JsonNode coordinates = geometry.get("coordinates");
            double longitude = coordinates.get(0).asDouble();
            double latitude = coordinates.get(1).asDouble();
            Node candidateNode = new Node(latitude, longitude);
            candidateNodes.add(candidateNode);
        }
        return candidateNodes;
    }

    public  void setNodesAndDistancesMap() throws IOException {
        List<Node> demandNodes =  this.extractDemandNodes();
        List<Node> candidateNodes = this.extractCandidateNodes();
        double[][] distanceMatrix = PMedianProblem.createDistanceMatrix(demandNodes, candidateNodes);
        Map<Pair<Node, Node>, Double> distanceMap = new HashMap<>();
        for (int i = 0; i < demandNodes.size(); i++) {
            for (int j = 0; j < candidateNodes.size(); j++) {
                Node demandNode = demandNodes.get(i);
                Node candidateNode = candidateNodes.get(j);
                double distance = distanceMatrix[i][j];

                Pair<Node, Node> nodePair = new Pair<>(demandNode, candidateNode);
                distanceMap.put(nodePair, distance);
            }
        }
        Services.saveNodesAndDistancesMapToJson(distanceMap, "NodesAndDistancesMap.json");
    }

    public Map<Pair<Node, Node>, Double> getNodesAndDistancesMap() throws IOException {
       return readNodesAndDistancesMapFromJson("NodesAndDistancesMap.json");
    }

    public void showMatrixGui() throws IOException {
        GraphicService.displayMap(getNodesAndDistancesMap());
    }






}
