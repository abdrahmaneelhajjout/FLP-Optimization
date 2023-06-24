package main.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Node;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class GeoJSONCreator  {


    public static List<Node> convertConnectionsMapToList(Map<Node, Node> connections){
        List<Node> chosenCandidateNodes = new ArrayList<>();
        chosenCandidateNodes.addAll(connections.values().stream().collect(Collectors.toSet()));
        return chosenCandidateNodes;
    }
    public static String createLineStringGeoJSON(Map<Node, Node> connections) {

        // Create a GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory();


        // Create a GeoJSON object
        GeoJSONObject geoJson = new GeoJSONObject();
        geoJson.setType("FeatureCollection");



        // Iterate over the connections map
        for (Map.Entry<Node, Node> entry : connections.entrySet()) {
            // Get the coordinates of the nodes
            Node node1 = entry.getKey();
            Node node2 = entry.getValue();

            // Create a LineString geometry between the two nodes
            LineString lineString = geometryFactory.createLineString(new Coordinate[]{node1.getCoordinate(), node2.getCoordinate()});

            // Create a Feature object for the LineString
            Feature feature = new Feature();
            feature.setType("Feature");
            Map<String, String> properties = new HashMap<>();
            properties.put("fill", "red");
            properties.put("stroke-width", "5");
            feature.setProperties(properties);
            // Create the geometry object
            LineStringGeometry geometry = new LineStringGeometry();
            geometry.setType("LineString");

            // Create the coordinates array for the LineString
            double[][] coordinates = new double[][]{{node1.getLatitude(),node1.getLongitude()}, { node2.getLatitude(), node2.getLongitude()}};

            // Add the coordinates array to the geometry object
            geometry.setCoordinates(coordinates);


            // Add the geometry object to the Feature object
            feature.setGeometry(geometry);

            // Add the Feature object to the GeoJSON object
            geoJson.addFeature(feature);
        }

        // Convert GeoJSON object to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(geoJson);

            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
    private static Point createPoint(Node node) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(node.getLongitude(), node.getLatitude()));
    }

    public static void saveGeoJSONToFile(String geoJson, String filePath) {
        // Write the GeoJSON to a file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(geoJson);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classes for GeoJSON structure using Jackson annotations
    public static class GeoJSONObject {
        private String type;
        private Feature[] features;



        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Feature[] getFeatures() {
            return features;
        }

        public void setFeatures(Feature[] features) {
            this.features = features;
        }

        public void addFeature(Feature feature) {
            if (features == null) {
                features = new Feature[1];
                features[0] = feature;
            } else {
                Feature[] updatedFeatures = new Feature[features.length + 1];
                System.arraycopy(features, 0, updatedFeatures, 0, features.length);
                updatedFeatures[features.length] = feature;
                features = updatedFeatures;
            }
        }
    }

    public static class Feature {
        private String type;
        private Geometry geometry;

        private Map<String, String> properties;

        public  Map<String, String> getProperties(){
            return this.properties;
        }

        public void setProperties( Map<String, String> properties){
            this.properties = properties;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }
    public static class Geometry {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }




    }
    public static class LineStringGeometry extends Geometry {
        private double[][] coordinates;




        public double[][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class PointGeometry extends  Geometry{
        private double[] coordinates;



        public double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[] coordinates) {
            this.coordinates = coordinates;
        }
    }
    public static String createPointsGeoJSON(List<Node> nodes) {
        GeoJSONObject geoJson = new GeoJSONObject();
        geoJson.setType("FeatureCollection");


        for (Node node : nodes) {
            Point point = createPoint(node);

            Feature feature;
            feature = new Feature();
            feature.setType("Feature");
            // Set the style properties for the LineString
            Map<String, String> properties = new HashMap<>();
            properties.put("fill", "red");
            properties.put("stroke-width", "5");
            feature.setProperties(properties);

            PointGeometry geometry = new PointGeometry();
            geometry.setType("Point");
            geometry.setCoordinates(new double[]{node.getLatitude(), node.getLongitude()});

            feature.setGeometry(geometry);

            geoJson.addFeature(feature);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(geoJson);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
