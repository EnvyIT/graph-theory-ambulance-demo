package at.fhooe.ams.util;

import at.fhooe.ams.model.Accident;
import at.fhooe.ams.model.Ambulance;
import at.fhooe.ams.model.AmbulanceStatus;
import at.fhooe.ams.model.Hospital;
import at.fhooe.ams.model.Vertex;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParserTest {

  @Test
  void createUndirectedWeightedGraph() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    String inputWeights = "{2, 2, 1, 2, 5, 1, 3, 2, 6, 8, 2, 2, 3, 2, 4, 1}";
    Map<String,Vertex> vertices = Parser.parseVertices(inputEdges);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);
    Graph<Vertex, DefaultWeightedEdge> graph = Parser.parseUndirectedWeightedGraph(vertices, edges, weights);
    Set<Vertex> graphVertices = graph.vertexSet();
    Set<Vertex> expectedVertices = createExpectedVertices();
    expectedVertices.forEach(vertex -> Assertions.assertTrue(graphVertices.contains(vertex)));
    Vertex a = new Vertex("a");
    Vertex b = new Vertex("b");
    Vertex c = new Vertex("c");
    Vertex d = new Vertex("d");
    Vertex e = new Vertex("e");
    Vertex f = new Vertex("f");
    Vertex g = new Vertex("g");
    Vertex h = new Vertex("h");
    Vertex i = new Vertex("i");
    Vertex j = new Vertex("j");

    DefaultWeightedEdge currentWeight = graph.getEdge(a, b);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(b,c);
    Assertions.assertEquals(1.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(c, d);
    Assertions.assertEquals(1.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(d, e);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(e, g);
    Assertions.assertEquals(8.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(g, i);
    Assertions.assertEquals(3.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(i, j);
    Assertions.assertEquals(4.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(j, a);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));

    currentWeight = graph.getEdge(j, h);
    Assertions.assertEquals(1.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(h, i);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(h, g);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(h, b);
    Assertions.assertEquals(5.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(b, f);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(f, g);
    Assertions.assertEquals(2.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(f, e);
    Assertions.assertEquals(6.0, graph.getEdgeWeight(currentWeight));
    currentWeight = graph.getEdge(f, d);
    Assertions.assertEquals(3.0, graph.getEdgeWeight(currentWeight));
  }

  private Set<Vertex> createExpectedVertices() {
    Set<Vertex> vertices = new HashSet<>();
    vertices.add(new Vertex("a"));
    vertices.add(new Vertex("b"));
    vertices.add(new Vertex("c"));
    vertices.add(new Vertex("d"));
    vertices.add(new Vertex("e"));
    vertices.add(new Vertex("f"));
    vertices.add(new Vertex("g"));
    vertices.add(new Vertex("h"));
    vertices.add(new Vertex("i"));
    vertices.add(new Vertex("j"));
    return vertices;
  }

  @Test
  void parseEdges() {
    String edges = "{{A,B}, {A,C}, {B,C}}";
    Map<String, Vertex> vertices = Parser.parseVertices(edges);
    List<Pair<Vertex, Vertex>> parsedEdges = Parser.parseEdges(edges, vertices);
    Assertions.assertEquals(3, parsedEdges.size());
    Assertions.assertEquals("A", parsedEdges.get(0).getFirst().getName());
    Assertions.assertEquals("B", parsedEdges.get(0).getSecond().getName());
    Assertions.assertEquals("A", parsedEdges.get(1).getFirst().getName());
    Assertions.assertEquals("C", parsedEdges.get(1).getSecond().getName());
    Assertions.assertEquals("B", parsedEdges.get(2).getFirst().getName());
    Assertions.assertEquals("C", parsedEdges.get(2).getSecond().getName());
  }

  @Test
  void parseWeights() {
    String weights = "{2, 3 ,5}";
    List<Double> parsedWeights = Parser.parseWeights(weights);
    Assertions.assertEquals(3, parsedWeights.size());
    Assertions.assertEquals(2, parsedWeights.get(0));
    Assertions.assertEquals(3, parsedWeights.get(1));
    Assertions.assertEquals(5, parsedWeights.get(2));
  }

  @Test
  void parseAmbulances() {
    String ambulances = "{A, C, D , E , F}";
    String status = "{0, 2, 1, 3, 0}";
    String inputEdges = "{ {A,B}, {B,F}, {C,D} , {E,F}, {F,A}}";
    Map<String,Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> parsedAmbulances = Parser.parseAmbulances(ambulances, status, vertices);
    Assertions.assertEquals(5, parsedAmbulances.size());
    Ambulance ambulanceA = parsedAmbulances.get(new Vertex("A"));
    Ambulance ambulanceC = parsedAmbulances.get(new Vertex("C"));
    Ambulance ambulanceD = parsedAmbulances.get(new Vertex("D"));
    Ambulance ambulanceE = parsedAmbulances.get(new Vertex("E"));
    Ambulance ambulanceF = parsedAmbulances.get(new Vertex("F"));
    Assertions.assertEquals("A", ambulanceA.getName());
    Assertions.assertEquals(AmbulanceStatus.FREE, ambulanceA.getStatus());
    Assertions.assertEquals("C", ambulanceC.getName());
    Assertions.assertEquals(AmbulanceStatus.OCCUPIED, ambulanceC.getStatus());
    Assertions.assertEquals("D", ambulanceD.getName());
    Assertions.assertEquals(AmbulanceStatus.BREAK, ambulanceD.getStatus());
    Assertions.assertEquals("E", ambulanceE.getName());
    Assertions.assertEquals(AmbulanceStatus.NOT_AVAILABLE, ambulanceE.getStatus());
    Assertions.assertEquals("F", ambulanceF.getName());
    Assertions.assertEquals(AmbulanceStatus.FREE, ambulanceF.getStatus());
  }

  @Test
  void parseHospitals() {
    String hospitals = "{A, B, F, J}";
    String inputEdges = "{ {A,B}, {B,F}, {F,J}, {J,A}}";
    Map<String,Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Hospital> parsedHospitals = Parser.parseHospitals(hospitals, vertices);
    Assertions.assertEquals(4, parsedHospitals.size());
    Hospital ambulanceA = parsedHospitals.get(new Vertex("A"));
    Hospital ambulanceB = parsedHospitals.get(new Vertex("B"));
    Hospital ambulanceF = parsedHospitals.get(new Vertex("F"));
    Hospital ambulanceJ = parsedHospitals.get(new Vertex("J"));
    Assertions.assertEquals("A", ambulanceA.getName());
    Assertions.assertEquals("B", ambulanceB.getName());
    Assertions.assertEquals("F", ambulanceF.getName());
    Assertions.assertEquals("J", ambulanceJ.getName());
  }

  @Test
  void parseAccidents() {
    String accidents = "{I, K}";
    String inputEdges = "{ { I, K} }";
    Map<String,Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Accident> parsedHospitals = Parser.parseAccidents(accidents, vertices);
    Assertions.assertEquals(2, parsedHospitals.size());
    Accident accidentI = parsedHospitals.get(new Vertex("I"));
    Accident accidentK = parsedHospitals.get(new Vertex("K"));
    Assertions.assertEquals("I", accidentI.getName());
    Assertions.assertEquals("K", accidentK.getName());
  }

  @Test
  void parseVertices() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    Map<String,Vertex> vertices = Parser.parseVertices(inputEdges);
    Set<Vertex> expectedVertices = createExpectedVertices();
    vertices.forEach((key, value) -> Assertions.assertTrue(expectedVertices.contains(value)));
  }

}
