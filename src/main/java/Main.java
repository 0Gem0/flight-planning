import Graph.Graph;
import Hibernate.FlightHelper;
import Hibernate.HibernateUtil;
import Hibernate.PlaceHelper;
import Hibernate.entity.Flight;
import Hibernate.entity.Place;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    private static final Pane root = new Pane();
    private static final Label resultLabel = new Label();
    private static final Label errorLabel = new Label("Нет такого пути");

    private static final TextField fromField = new TextField();
    private static final TextField toField = new TextField();

    private static final Canvas canvas = new Canvas(1920, 1080);
    private static final GraphicsContext gc = canvas.getGraphicsContext2D();

    private static List<Flight> flights = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Поиск маршрутов");

        initUI();
        loadAndDrawGraph();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void initUI() {
        fromField.setLayoutX(300);
        fromField.setLayoutY(550);

        toField.setLayoutX(300);
        toField.setLayoutY(650);

        Button startButton = new Button("Пуск");
        startButton.setLayoutX(300);
        startButton.setLayoutY(800);
        startButton.setMinSize(80, 30);
        startButton.setOnAction(e -> showShortestPath());

        Label fromLabel = createLabel("Откуда:", 300, 500);
        Label toLabel = createLabel("Куда:", 300, 600);
        Label headerLabel = createLabel("Полёт", 300, 430);

        root.getChildren().addAll(canvas, fromLabel, toLabel, headerLabel, fromField, toField, startButton);
    }

    private Label createLabel(String text, double x, double y) {
        Label label = new Label(text);
        label.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        label.setLayoutX(x);
        label.setLayoutY(y);
        return label;
    }

    private void loadAndDrawGraph() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Place> places = new PlaceHelper(session).getPlaceList();
            flights = new FlightHelper(session).getFlightList();

            for (Flight flight : flights) {
                Place from = places.stream().filter(p -> Objects.equals(flight.getFrom(), p.getName())).findFirst().orElse(null);
                Place to = places.stream().filter(p -> Objects.equals(flight.getTo(), p.getName())).findFirst().orElse(null);

                if (from != null && to != null) {
                    Graph.generateGraph(from, to, flight);
                }
            }

            Graph.printGraph();
            Graph.drawGraphWrap(Graph.graph, gc);
        }
    }

    private void showShortestPath() {
        root.getChildren().remove(resultLabel);
        root.getChildren().remove(errorLabel);
        gc.clearRect(650, 200, 1100, 1100);

        Graph.drawGraphWrap(Graph.graph, gc);

        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        ArrayList<String> route;

        try {
            route = Graph.Deikstra(from, to);
        } catch (NullPointerException e) {
            showError("Нет пути");
            return;
        }

        drawRoute(route);
        showTotalCost(route);
    }

    private void drawRoute(List<String> route) {
        gc.setStroke(Paint.valueOf("#ff0000"));

        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            gc.strokeLine(Graph.graph.get(from).getX(), Graph.graph.get(from).getY(),
                    Graph.graph.get(to).getX(), Graph.graph.get(to).getY());
        }
    }

    private void showTotalCost(List<String> route) {
        int total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            for (Flight flight : flights) {
                total += flight.returnPrice(from, to);
            }
        }

        resultLabel.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        resultLabel.setText("Вся сумма: " + total);
        resultLabel.setLayoutX(300);
        resultLabel.setLayoutY(700);
        root.getChildren().add(resultLabel);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        errorLabel.setLayoutX(300);
        errorLabel.setLayoutY(700);
        root.getChildren().add(errorLabel);
    }
}
