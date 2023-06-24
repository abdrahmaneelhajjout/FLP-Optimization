package main.utils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import main.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomMapDeserializer extends JsonDeserializer<Map<Pair<Node, Node>, Double>> {
    @Override
    public Map<Pair<Node, Node>, Double> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        Map<Pair<Node, Node>, Double> map = new HashMap<>();

        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode rootNode = objectMapper.readTree(jsonParser);
        Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.fields();

        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            String keyString = entry.getKey();

            double value = entry.getValue().asDouble();

            Pair<Node, Node> pair = parsePair(keyString);
            map.put(pair, value);
        }

        return map;
    }


    private Pair<Node, Node> parsePair(String keyString) {
        Pattern pattern = Pattern.compile("Node\\{longitude=(.+?), latitude=(.+?)\\}");
        Matcher matcher = pattern.matcher(keyString);
        if (matcher.find()) {
            double firstLongitude = Double.parseDouble(matcher.group(1).trim());
            double firstLatitude = Double.parseDouble(matcher.group(2).trim());

            matcher.find();
            double secondLongitude = Double.parseDouble(matcher.group(1).trim());
            double secondLatitude = Double.parseDouble(matcher.group(2).trim());

            Node first = new Node(firstLongitude, firstLatitude);
            Node second = new Node(secondLongitude, secondLatitude);

            return new Pair<>(first, second);
        }
        return null;

    }
    
}
