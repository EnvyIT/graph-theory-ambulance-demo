package at.fhooe.ams.util;


import at.fhooe.ams.model.Accident;
import at.fhooe.ams.model.Ambulance;
import at.fhooe.ams.model.AmbulanceStatus;
import at.fhooe.ams.model.Hospital;
import at.fhooe.ams.model.Vertex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser {

  public static Graph<Vertex, DefaultWeightedEdge> parseUndirectedWeightedGraph(Map<String, Vertex> vertices, List<Pair<Vertex, Vertex>> edges,
      List<Double> weights) {
    Graph<Vertex, DefaultWeightedEdge> graph = buildGraph();
    vertices.forEach((key, value) -> graph.addVertex(value));

    for (int i = 0; i < edges.size(); ++i) {
      DefaultWeightedEdge weightedEdge = graph.addEdge(edges.get(i).getFirst(), edges.get(i).getSecond());
      graph.setEdgeWeight(weightedEdge, weights.get(i));
    }
    return graph;
  }

  private static Graph<Vertex, DefaultWeightedEdge> buildGraph() {
    return GraphTypeBuilder
        .<Vertex, DefaultWeightedEdge> undirected()
        .allowingMultipleEdges(false)
        .allowingSelfLoops(false)
        .edgeClass(DefaultWeightedEdge.class)
        .weighted(true)
        .buildGraph();
  }


  public static List<Pair<Vertex, Vertex>> parseEdges(String edges, Map<String, Vertex> vertices) {
    List<Pair<Vertex, Vertex>> parsedEdges = new ArrayList<>();
    String normalized = StringUtil.normalizeSet(edges);
    String[] allEdges = StringUtil.trimAll(normalized.split(","));
    for (int i = 0; i < allEdges.length; i += 2) {
      parsedEdges.add(new Pair<>(vertices.get(allEdges[i]), vertices.get(allEdges[i + 1])));
    }
    return parsedEdges;
  }

  public static List<Double> parseWeights(String weights) {
    String normalized = StringUtil.normalizeSet(weights);
    String[] allWeights = StringUtil.trimAll(normalized.split(","));
    return Arrays.stream(allWeights).map(Double::parseDouble).collect(Collectors.toList());
  }


  public static Map<Vertex, Ambulance> parseAmbulances(String ambulances, String ambulanceStatus , Map<String, Vertex> vertices) {
    Map<Vertex, Ambulance> parsedAmbulances =  new HashMap<>();
    String normalizedAmbulances = StringUtil.normalizeSet(ambulances);
    String[] allAmbulances = StringUtil.trimAll(normalizedAmbulances.split(","));
    String normalizedStatus = StringUtil.normalizeSet(ambulanceStatus);
    String[] allStatus = StringUtil.trimAll(normalizedStatus.split(","));
    for(int i = 0; i < allAmbulances.length; ++i) {
      parsedAmbulances.put(vertices.get(allAmbulances[i]), new Ambulance(allAmbulances[i], getAmbulanceStatus(allStatus[i])));
    }
    return parsedAmbulances;
  }

  private static AmbulanceStatus getAmbulanceStatus(String input ) {
    String status = input.trim();
    switch (status) {
      case "0":
        return AmbulanceStatus.FREE;
      case "1":
        return AmbulanceStatus.BREAK;
      case "2":
        return AmbulanceStatus.OCCUPIED;
      default:
        return AmbulanceStatus.NOT_AVAILABLE;
    }
  }

  public static Map<Vertex, Hospital> parseHospitals(String hospitals , Map<String, Vertex> vertices) {
    Map<Vertex, Hospital> parsedHospitals =  new HashMap<>();
    String normalizedHospitals = StringUtil.normalizeSet(hospitals);
    String[] allHospitals = StringUtil.trimAll(normalizedHospitals.split(","));
    for (String allHospital : allHospitals) {
      parsedHospitals.put(vertices.get(allHospital), new Hospital(allHospital));
    }
    return parsedHospitals;
  }

  public static Map<Vertex, Accident> parseAccidents(String accidents, Map<String, Vertex> vertices) {
    Map<Vertex, Accident> parsedAccidents =  new HashMap<>();
    String normalizedAccidents = StringUtil.normalizeSet(accidents);
    String[] allAccidents = StringUtil.trimAll(normalizedAccidents.split(","));
    for (String allAccident : allAccidents) {
      parsedAccidents.put(vertices.get(allAccident), new Accident(allAccident));
    }
    return parsedAccidents;
  }

  public static Map<String, Vertex> parseVertices(String edges) {
    String normalizedEdges = StringUtil.normalizeSet(edges);
    String[] allEdges = StringUtil.trimAll(normalizedEdges.split(","));
    return Arrays.stream(allEdges).distinct().collect(Collectors.toMap(s -> s, Vertex::new));
  }

  public static AStarAdmissibleHeuristic<Vertex> createHeuristic(Graph<Vertex, DefaultWeightedEdge> graph) {
    return new ALTAdmissibleHeuristic<>(graph, graph.vertexSet());
  }

}
