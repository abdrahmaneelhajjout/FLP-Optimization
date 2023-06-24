package main.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.util.Pair;
import main.Node;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

public class Services {
    public static void saveNodesAndDistancesMapToJson(Map<Pair<Node, Node>, Double> map, String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(Services.class.getResource(".").getFile() + "/" + fileName);
        if (file.createNewFile()) {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }
        String filePath = Services.class.getResource(fileName).getPath();
        objectMapper.writeValue(new File(filePath), map);
        objectMapper.writeValue(file, new HashMap<>(map));
        System.out.println("saved ");
    }

    public static Map<Pair<Node, Node>, Double> readNodesAndDistancesMapFromJson(String fileName) throws IOException {
        String filePath = Services.class.getResource(fileName).getPath();
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new CustomMapDeserializer());
        objectMapper.registerModule(module);

        Map<Pair<Node, Node>, Double> map = objectMapper.readValue(file, new TypeReference<Map<Pair<Node, Node>, Double>>() {});

        return map;

    }

    public static void main(String[] args) {

    }
}
