package main.utils;

import javafx.util.Pair;
import main.Node;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Map;

public class GraphicService {
    public static void displayMap(Map<Pair<Node, Node>, Double> map) {
        int rows = map.size();
        int cols = map.size();

        String[] columnNames = new String[cols];
        for (int col = 0; col < cols; col++) {
            columnNames[col] = String.format("Col %d", col + 1);
        }

        DefaultTableModel model = new DefaultTableModel(columnNames, rows);

        int row = 0;
        int col = 0;
        for (Map.Entry<Pair<Node, Node>, Double> entry : map.entrySet()) {
            Double distance = entry.getValue();
            model.setValueAt(distance, row, col);

            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }

        JTable table = new JTable(model);

        JFrame frame = new JFrame("Distance Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
    }


}
