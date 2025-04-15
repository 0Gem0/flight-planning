package Graph;

import Hibernate.entity.Flight;
import Hibernate.entity.Place;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;

public class Graph {
    public static HashMap<String, Place> graph = new HashMap<>();

    public static Place addOrGetPlace(Place place) {
        if (Objects.equals(place.getName(), "")) {
            return null;
        }
        if (graph.containsKey(place.getName())) {
            return graph.get(place.getName());
        }
        graph.put(place.getName(), place);
        return place;
    }

    public static void generateGraph(Place from, Place to, Flight flight) {
        if (from == null || to == null) {
            System.out.println("Нет вершин");
            return;
        }
        flight.flightDest = to;
        addOrGetPlace(from).flights.add(flight);
        addOrGetPlace(to).parents.put(from, flight);
    }

    public static void printGraph() {
        for (Map.Entry<String, Place> entry : graph.entrySet()) {
            Place temp = entry.getValue();
            for (Map.Entry<Place, Flight> entry1 : temp.parents.entrySet()) {
                Place temp1 = entry1.getKey();
                Flight temp2 = entry1.getValue();
                //System.out.println("[" + temp1.getName() + "]" + "->" + temp2.getPrice() + "[" + entry.getKey() + "]");
            }
        }
    }

    public static ArrayList<String> Deikstra(String begin, String end) {
        int idBegin;
        int idEnd;
        try {
            idBegin = graph.get(begin).getId();
            idEnd = graph.get(end).getId();
        } catch (NullPointerException exception) {
            throw new NullPointerException();
        }

        String nearestStr = "";
        int nearest = 0;
        int[] from = new int[graph.size() + 1];
        Arrays.fill(from, -1);
        int[] dist = new int[graph.size() + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[idBegin] = 0;

        HashMap<Integer, Integer> visited = new HashMap<>();
        visited.put(idBegin, dist[idBegin]);

        while (!visited.isEmpty()) {
            for (int key : visited.keySet()) {
                if (Objects.equals(visited.get(key), Collections.min(visited.values()))) {
                    nearest = key;
                    for (Place place : graph.values()) {
                        if (place.getId() == nearest) {
                            nearestStr = place.getName();
                        }
                    }
                    break;
                }
            }
            visited.remove(nearest);

            for (Flight flight : graph.get(nearestStr).flights) {
                if (flight.getPrice() + dist[nearest] < dist[flight.getFlightDest().getId()]) {
                    visited.remove(flight.getFlightDest().getId(), flight.getPrice());
                    dist[flight.getFlightDest().getId()] = flight.getPrice() + dist[nearest];
                    visited.put(flight.getFlightDest().getId(), dist[flight.getFlightDest().getId()]);
                    from[flight.getFlightDest().getId()] = nearest;
                }
            }
        }

        ArrayList<String> order = new ArrayList<>();
        for (int v = idEnd; v != -1; v = from[v]) {
            if (dist[idEnd] != Integer.MAX_VALUE) {
                for (String key : graph.keySet()) {
                    if (graph.get(key).getId() == v) {
                        order.add(key);
                    }
                }
            } else {
                break;
            }
        }
        Collections.reverse(order);
        return order;
    }

    public static void drawGraph(GraphicsContext graphicsContext, Place place, LinkedHashSet<Place> passed) {
        graphicsContext.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        graphicsContext.setFill(Color.BLACK);
        passed.add(place);

        graphicsContext.setStroke(Paint.valueOf("#ff0000"));
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillText(place.getName(), place.getX(), place.getY() - 8);
        graphicsContext.fillOval(place.getX(), place.getY(), 5, 5);

        for (Flight flight : place.getFlights()) {
            graphicsContext.setStroke(Paint.valueOf("#000000"));
            graphicsContext.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
            graphicsContext.setFill(Color.RED);
            graphicsContext.setLineWidth(3);

            double[] arrows = createArrow(place.getX(), place.getY(),
                    flight.getFlightDest().getX(), flight.getFlightDest().getY(), 10);

            graphicsContext.strokeLine(place.getX(), place.getY(),
                    flight.getFlightDest().getX(), flight.getFlightDest().getY());

            graphicsContext.setStroke(Paint.valueOf("#ff0000"));
            graphicsContext.strokeLine(flight.getFlightDest().getX(), flight.getFlightDest().getY(), arrows[0], arrows[1]);
            graphicsContext.strokeLine(flight.getFlightDest().getX(), flight.getFlightDest().getY(), arrows[2], arrows[3]);

            graphicsContext.fillText(flight.getPrice() + "",
                    (int) ((place.getX() + flight.getFlightDest().getX()) / 2 - 35),
                    (int) ((place.getY() + flight.getFlightDest().getY()) / 2 - 6));

            if (!passed.contains(flight.getFlightDest())) {
                drawGraph(graphicsContext, flight.getFlightDest(), passed);
            }
        }
    }

    public static void drawGraphWrap(HashMap<String, Place> graph, GraphicsContext graphicsContext) {
        LinkedHashSet<Place> passed = new LinkedHashSet<>();
        for (Map.Entry<String, Place> entry : graph.entrySet()) {
            if (!passed.contains(entry.getValue())) {
                drawGraph(graphicsContext, entry.getValue(), passed);
            }
        }
    }

    public static double[] createArrow(double startX, double startY, double endX, double endY, double arrowHeadSize) {
        double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double x1 = (-0.5 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
        double y1 = (-0.5 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

        double x2 = (0.5 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
        double y2 = (0.5 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

        return new double[]{x1, y1, x2, y2};
    }
}
