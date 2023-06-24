package main.utils;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.geojson.Point;
import main.Node;

import java.util.List;

public class PMedianProblem {
    private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiYWJkZXJyYWhtYW5lbGgiLCJhIjoiY2xqYWtpbGRpMGpzNTNsbnRjMW4yNXpxNCJ9.Z5SHUMBpHtr97aaD34H02A";

    public static double[][] createDistanceMatrix(List<Node> demandNodes, List<Node> candidateNodes) {
        double[][] distanceMatrix = new double[demandNodes.size()][candidateNodes.size()];
        for (int i = 0; i < demandNodes.size(); i++) {
            for (int j = 0; j < candidateNodes.size(); j++) {
                try {
                    distanceMatrix[i][j] = calculateDistance(demandNodes.get(i), candidateNodes.get(j));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return distanceMatrix;
    }


    public static double calculateDistance(Node origin, Node destination) throws Exception {
        Point originPoint = Point.fromLngLat(origin.getLongitude(), origin.getLatitude());
        Point destinationPoint = Point.fromLngLat(destination.getLongitude(), destination.getLatitude());

        MapboxDirections client = MapboxDirections.builder()
                .origin(originPoint)
                .destination(destinationPoint)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(MAPBOX_ACCESS_TOKEN)
                .build();
        double distance = client.executeCall().body().routes().get(0).distance();
        System.out.println(distance);
        return distance;
    }

    public static void main(String[] args) {

    }
}
