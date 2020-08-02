package at.fhooe.ams.algorithm;

import at.fhooe.ams.model.Accident;
import at.fhooe.ams.model.Ambulance;
import at.fhooe.ams.model.AmbulanceStatus;
import at.fhooe.ams.model.Hospital;
import at.fhooe.ams.model.Vertex;
import at.fhooe.ams.util.GraphUtil;
import at.fhooe.ams.util.Parser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphController {


  public static Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> getAmbulancePaths(Map<String, Vertex> vertices,
      List<Pair<Vertex, Vertex>> edges,
      Map<Vertex, Ambulance> ambulances, Map<Vertex, Hospital> hospitals, Map<Vertex, Accident> accidents,
      List<Double> weights) {

    Graph<Vertex, DefaultWeightedEdge> graph = Parser.parseUndirectedWeightedGraph(vertices, edges, weights);
    GraphUtil.setParams(graph, ambulances, hospitals, accidents);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> occupiedPaths = new HashMap<>();
    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> accidentPaths = new HashMap<>();
    AStarAdmissibleHeuristic<Vertex> heuristic = Parser.createHeuristic(graph);

    calculatePathsToHospital(ambulances, hospitals, graph, occupiedPaths, heuristic);
    calculatePathsToAccident(ambulances, accidents, graph, occupiedPaths, accidentPaths, heuristic);

    return accidentPaths;
  }

  private static void calculatePathsToAccident(Map<Vertex, Ambulance> ambulances, Map<Vertex, Accident> accidents,
      Graph<Vertex, DefaultWeightedEdge> graph, Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> occupiedPaths,
      Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths, AStarAdmissibleHeuristic<Vertex> heuristic) {

    //calculate shortest path for each ambulance to accident place
    ambulances.forEach((ambulanceKey, ambulanceValue) -> accidents.forEach((accidentKey, accidentValue) -> {
      if (!AmbulanceStatus.NOT_AVAILABLE.equals(ambulanceValue.getStatus())) {
        GraphPath<Vertex, DefaultWeightedEdge> path = GraphController.getShortestPath(graph, ambulanceKey, accidentKey, heuristic);
        if (AmbulanceStatus.BREAK.equals(ambulanceValue.getStatus())) {
          GraphUtil.addBreakTime(path);
        } else if (AmbulanceStatus.OCCUPIED.equals(ambulanceValue.getStatus())) {
          Vertex hospital = occupiedPaths.get(ambulanceKey).getEndVertex();
          GraphPath<Vertex, DefaultWeightedEdge> pathToAccident = GraphController.getShortestPath(graph, hospital, accidentKey, heuristic);
          path = GraphUtil.mergeHospitalPath(pathToAccident, occupiedPaths.get(ambulanceKey));
        }
        addPathIfShorter(ambulancePaths, path);
      }
    }));
  }

  private static void calculatePathsToHospital(Map<Vertex, Ambulance> ambulances, Map<Vertex, Hospital> hospitals,
      Graph<Vertex, DefaultWeightedEdge> graph, Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> occupiedPaths, AStarAdmissibleHeuristic<Vertex> heuristic) {
    //calculate shortest path to hospital for each occupied ambulance
    ambulances.forEach((ambulanceKey, ambulanceValue) -> hospitals.forEach((hospitalKey, hospitalValue) -> {
      if (AmbulanceStatus.OCCUPIED.equals(ambulanceValue.getStatus())) {
        GraphPath<Vertex, DefaultWeightedEdge> path = GraphController.getShortestPath(graph, ambulanceKey, hospitalKey, heuristic);
        addPathIfShorter(occupiedPaths, path, true);
      }
    }));
  }

  private static GraphPath<Vertex, DefaultWeightedEdge> getShortestPath(Graph<Vertex, DefaultWeightedEdge> graph, Vertex source,
      Vertex sink, AStarAdmissibleHeuristic<Vertex> heuristic) {
    AStarShortestPath<Vertex, DefaultWeightedEdge> shortestPathAlgorithm = new AStarShortestPath<>(graph, heuristic);
    SingleSourcePaths<Vertex, DefaultWeightedEdge> path = shortestPathAlgorithm.getPaths(source);
    return path.getPath(sink);
  }

  private static void addPathIfShorter(Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> accidentPaths,
      GraphPath<Vertex, DefaultWeightedEdge> path) {
    addPathIfShorter(accidentPaths, path, false);
  }

  private static void addPathIfShorter(Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> accidentPaths,
      GraphPath<Vertex, DefaultWeightedEdge> path, boolean addStart) {
    Vertex vertex = addStart ? path.getStartVertex() : path.getEndVertex();
    if ((!accidentPaths.containsKey(vertex) && !isAccidentPathAlreadyCalculated(accidentPaths, path)) ||
        (accidentPaths.containsKey(vertex) && accidentPaths.get(vertex).getWeight() > path.getWeight())) {
      accidentPaths.put(vertex, path);
    }
  }

  private static boolean isAccidentPathAlreadyCalculated(Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> accidentPaths,
      GraphPath<Vertex, DefaultWeightedEdge> otherPath) {
    for(Vertex accident: accidentPaths.keySet()) {
      if (isAmbulancePlanned(accidentPaths, otherPath, accident)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isAmbulancePlanned(Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> accidentPaths,
      GraphPath<Vertex, DefaultWeightedEdge> otherPath, Vertex accident) {
    return accidentPaths.get(accident).getStartVertex().equals(otherPath.getStartVertex()) && accidentPaths.get(accident).getWeight() <= otherPath.getWeight();
  }

  public static void printAccidentPaths(Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> accidentPaths) {
    System.out.println("\n====== Accidents & Ambulances ========");
    accidentPaths.forEach((accident, path) -> System.out.printf("Accident: %s  Ambulance: %s %n", accident.getName(), path.getStartVertex().getAmbulance().getName()));
    System.out.println("\n============ Paths for all ambulances ===============");
    accidentPaths.forEach((accident, path) -> System.out.printf("Accident: %s - Path: %s - Pathlength: %f%n", accident, path.getVertexList().toString(), path.getWeight()));
  }
}
