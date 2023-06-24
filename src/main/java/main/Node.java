package main;


import org.locationtech.jts.geom.Coordinate;

import java.util.Objects;

public class Node {
    private String id;
    private final double longitude;
    private final double latitude;
    public Node(String id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public Node(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    @Override
    public String toString() {
        return "Node{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Double.compare(node.longitude, longitude) == 0 && Double.compare(node.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }

    public Coordinate getCoordinate() {
        return new Coordinate(longitude, latitude);
    }
}
