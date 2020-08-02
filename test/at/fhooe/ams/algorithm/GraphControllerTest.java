package at.fhooe.ams.algorithm;

import at.fhooe.ams.model.Accident;
import at.fhooe.ams.model.Ambulance;
import at.fhooe.ams.model.Hospital;
import at.fhooe.ams.model.Vertex;
import at.fhooe.ams.util.Parser;
import java.util.List;
import java.util.Map;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GraphControllerTest {

  private final static double DELTA = 0.0001;

  @Test
  void getPathsInputGraphFromSheetTest() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    String inputWeights = "{2, 2, 1, 2, 5, 1, 3, 2, 6, 8, 2, 2, 3, 2, 4, 1}";
    String inputAmbulances = "{b, e}";
    String inputStates = "{2, 0}";
    String inputHospitals = "{d}";
    String inputAccidents = "{i}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(1, ambulancePaths.size());
    Vertex i = vertices.get("i");
    Assertions.assertTrue(ambulancePaths.containsKey(i));
    AssertAmbulanceAndPath(ambulancePaths, i, "[{ e }, { d }, { f }, { g }, { i }]", 10.0);
  }

  @Test
  void getShortestPathSumForAllAccidents() {
    String inputEdges = "{ {a,c}, {c,d}, {d,f}, {f,e}, {e,b}, {b,d}, {a,f}, {a,b}, {e,g} }";
    String inputWeights = "{1, 2, 5, 1, 3, 7, 6, 2, 10}";
    String inputAmbulances = "{b, a, e}";
    String inputStates = "{0, 0 ,0}";
    String inputHospitals = "{d}";
    String inputAccidents = "{d, f, c}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(3, ambulancePaths.size());
    Vertex c = vertices.get("c");
    Vertex d = vertices.get("d");
    Vertex f = vertices.get("f");
    Assertions.assertTrue(ambulancePaths.containsKey(c));
    Assertions.assertTrue(ambulancePaths.containsKey(d));
    Assertions.assertTrue(ambulancePaths.containsKey(f));
    AssertAmbulanceAndPath(ambulancePaths, c, "[{ a }, { c }]", 1.0);
    AssertAmbulanceAndPath(ambulancePaths, d, "[{ b }, { a }, { c }, { d }]", 5.0);
    AssertAmbulanceAndPath(ambulancePaths, f, "[{ e }, { f }]", 1.0);
  }

  @Test
  void threePathsWith7() {
    String inputEdges = "{{a,b}, {b,c}, {c, d},{d, e}, {e,g}, {g,i}, {i, j}, {j,a}, {j,h}, {b,h}, {g,h}, {i,h}, {b,f}, {d,f}, {e,f}, {g,f}}";
    String inputWeights = "{2, 1, 1, 2, 8, 3, 4, 2, 1, 5, 2, 2, 2, 3, 6, 2}";
    String inputAmbulances = "{b, e}";
    String inputStates = "{0 , 2}";
    String inputHospitals = "{d}";
    String inputAccidents = "{i}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(1, ambulancePaths.size());
    Vertex i = vertices.get("i");
    Assertions.assertTrue(ambulancePaths.containsKey(i));
    AssertAmbulanceAndPath(ambulancePaths, i, "[{ b }, { h }, { i }]", 7.0);
  }

  @Test
  void getFastestAmbulanceIfAmbulanceIsOnBreak() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    String inputWeights = "{2, 2, 1, 2, 5, 1, 3, 2, 6, 8, 2, 2, 3, 2, 4, 1}";
    String inputAmbulances = "{b, e}";
    String inputStates = "{1, 2}";
    String inputHospitals = "{d}";
    String inputAccidents = "{i}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(1, ambulancePaths.size());
    Vertex i = vertices.get("i");
    Assertions.assertTrue(ambulancePaths.containsKey(i));
    AssertAmbulanceAndPath(ambulancePaths, i, "[{ b }, { h }, { i }]", 8.0);
  }


  @Test
  void multipleHospitalsAndAmbulances() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    String inputWeights = "{2, 2, 1, 2, 5, 1, 3, 2, 6, 8, 2, 2, 3, 2, 4, 1}";
    String inputAmbulances = "{a, b, g}";
    String inputStates = "{0, 2, 1}";
    String inputHospitals = "{d, j}";
    String inputAccidents = "{h, f}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(2, ambulancePaths.size());
    Vertex h = vertices.get("h");
    Vertex f = vertices.get("f");
    Assertions.assertTrue(ambulancePaths.containsKey(h));
    Assertions.assertTrue(ambulancePaths.containsKey(f));
    AssertAmbulanceAndPath(ambulancePaths, h, "[{ a }, { j }, { h }]", 3.0); //R0 mit weight 3 und Kreuzungen 3
    AssertAmbulanceAndPath(ambulancePaths, f, "[{ g }, { f }]", 3.0); //R1 mit weight 2  + 1 Break und Kreuzungen 2
  }

  @Test
  void moreAccidentsThanAmbulancesWithSamePathsAndCrossroads() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    String inputWeights = "{2, 2, 1, 2, 5, 1, 3, 2, 6, 8, 2, 2, 3, 2, 4, 1}";
    String inputAmbulances = "{a, b}";
    String inputStates = "{0, 3}";
    String inputHospitals = "{j, e}";
    String inputAccidents = "{c , h}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(1, ambulancePaths.size());
    Vertex c = vertices.get("c");
    Assertions.assertTrue(ambulancePaths.containsKey(c));
    AssertAmbulanceAndPath(ambulancePaths, c, "[{ a }, { b }, { c }]", 3.0); //R0 mit weight 3 und Kreuzungen 3
  }

  @Test
  void onlyNotAvailableAmbulancesShouldReturnNoResult() {
    String inputEdges = "{ {a,b}, {a,j}, {b,c}, {b,f}, {b,h}, {c,d}, {d,f}, {d,e}, {e,f}, {e,g},{g,f}, {g,h}, {g,i}, {i,h}, {i,j}, {j,h} }";
    String inputWeights = "{2, 2, 1, 2, 5, 1, 3, 2, 6, 8, 2, 2, 3, 2, 4, 1}";
    String inputAmbulances = "{a, b}";
    String inputStates = "{3, 3}";
    String inputHospitals = "{ f }";
    String inputAccidents = "{c , h}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(0, ambulancePaths.size()); //No ambulance is available
  }


  @Test
  void proofTest1() {
    String inputEdges = "{{1, 2}, {1, 6}, {2, 3}, {2, 7}, {3, 8}, {4, 5}, {4, 9}, {5, 10}, {6, 7}, {6, 11}, {7, 8}, {8, 9}, {8, 13}, {9, 10}, {9, 14}, {10, 15}, {11, 12}, {12, 13}, {12, 17}, {13, 14}, {13, 18}, {14, 15}, {14, 19}, {15, 20}, {16, 17}, {16, 21}, {17, 18}, {17, 22}, {18, 19}, {18, 23}, {19, 20}, {19, 24}, {20, 25}, {21, 26}, {22, 23}, {22, 27}, {23, 24}, {23, 28}, {24, 25}, {24, 29}, {25, 30}, {26, 27}, {27, 28}, {27, 32}, {28, 29}, {29, 30}, {29, 34}, {30, 35}, {31, 32}, {31, 36}, {32, 33}, {33, 34}, {33, 38}, {34, 35}, {34, 39}, {35, 40}, {36, 37}, {36, 41}, {37, 38}, {37, 42}, {38, 39}, {38, 43}, {39, 44}, {40, 45}, {41, 42}, {41, 46}, {42, 43}, {43, 44}, {44, 45}, {44, 49}, {45, 50}, {46, 47}, {47, 48}, {48, 49}, {49, 50}}";
    String inputWeights = "{0.623319, 0.782928, 0.724415, 0.663301, 0.980314, 0.534384, 0.857525, 0.802711, 0.690864, 1.01628, 1.01964, 1.07021, 1.27433, 0.786696, 1.09202, 1.039, 1.17553, 0.893182, 1.30296, 0.90763, 1.23687, 0.747427, 1.21788, 1.19712, 0.929205, 0.824945, 0.843394, 1.27442, 0.825103, 1.16764, 0.677995, 1.30186, 1.31337, 0.904508, 0.632366, 1.35271, 0.715957, 1.02362, 0.559852, 1.38604, 1.37545, 1.05876, 0.660787, 1.69842, 0.843769, 0.319187, 1.56455, 1.36498, 1.41519, 1.30702, 0.942232, 0.708194, 1.10823, 0.409737, 1.35155, 1.27066, 0.444337, 1.17571, 0.858115, 0.609474, 0.671463, 0.841348, 1.18433, 1.15872, 0.527241, 1.01031, 0.765726, 0.746467, 0.715457, 1.08177, 0.917307, 0.814914, 0.812392, 1.01424, 0.734067}";
    String inputAmbulances = "{27, 1, 46, 31, 27}";
    String inputStates = "{2, 0, 0, 1, 3}";
    String inputHospitals = "{28}";
    String inputAccidents = "{18, 21, 44}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(3, ambulancePaths.size());
    Vertex a_18 = vertices.get("18");
    Vertex a_21 = vertices.get("21");
    Vertex a_44 = vertices.get("44");
    Assertions.assertTrue(ambulancePaths.containsKey(a_18));
    Assertions.assertTrue(ambulancePaths.containsKey(a_21));
    Assertions.assertTrue(ambulancePaths.containsKey(a_44));
    AssertAmbulanceAndPath(ambulancePaths, a_18, "[{ 1 }, { 2 }, { 7 }, { 8 }, { 13 }, { 18 }]", 4.81746);
    AssertAmbulanceAndPath(ambulancePaths, a_21, "[{ 31 }, { 32 }, { 27 }, { 26 }, { 21 }]", 6.076878);
    AssertAmbulanceAndPath(ambulancePaths, a_44, "[{ 46 }, { 41 }, { 42 }, { 43 }, { 44 }]", 3.049744);
  }

  @Test
  void proofFewerAmbulancesThanAccidents() {
    String inputEdges = "{{1, 2}, {1, 6}, {2, 3}, {2, 7}, {3, 8}, {4, 5}, {4, 9}, {5, 10}, {6, 7}, {6, 11}, {7, 8}, {8, 9}, {8, 13}, {9, 10}, {9, 14}, {10, 15}, {11, 12}, {12, 13}, {12, 17}, {13, 14}, {13, 18}, {14, 15}, {14, 19}, {15, 20}, {16, 17}, {16, 21}, {17, 18}, {17, 22}, {18, 19}, {18, 23}, {19, 20}, {19, 24}, {20, 25}, {21, 26}, {22, 23}, {22, 27}, {23, 24}, {23, 28}, {24, 25}, {24, 29}, {25, 30}, {26, 27}, {27, 28}, {27, 32}, {28, 29}, {29, 30}, {29, 34}, {30, 35}, {31, 32}, {31, 36}, {32, 33}, {33, 34}, {33, 38}, {34, 35}, {34, 39}, {35, 40}, {36, 37}, {36, 41}, {37, 38}, {37, 42}, {38, 39}, {38, 43}, {39, 44}, {40, 45}, {41, 42}, {41, 46}, {42, 43}, {43, 44}, {44, 45}, {44, 49}, {45, 50}, {46, 47}, {47, 48}, {48, 49}, {49, 50}}";
    String inputWeights = "{0.623319, 0.782928, 0.724415, 0.663301, 0.980314, 0.534384, 0.857525, 0.802711, 0.690864, 1.01628, 1.01964, 1.07021, 1.27433, 0.786696, 1.09202, 1.039, 1.17553, 0.893182, 1.30296, 0.90763, 1.23687, 0.747427, 1.21788, 1.19712, 0.929205, 0.824945, 0.843394, 1.27442, 0.825103, 1.16764, 0.677995, 1.30186, 1.31337, 0.904508, 0.632366, 1.35271, 0.715957, 1.02362, 0.559852, 1.38604, 1.37545, 1.05876, 0.660787, 1.69842, 0.843769, 0.319187, 1.56455, 1.36498, 1.41519, 1.30702, 0.942232, 0.708194, 1.10823, 0.409737, 1.35155, 1.27066, 0.444337, 1.17571, 0.858115, 0.609474, 0.671463, 0.841348, 1.18433, 1.15872, 0.527241, 1.01031, 0.765726, 0.746467, 0.715457, 1.08177, 0.917307, 0.814914, 0.812392, 1.01424, 0.734067}";
    String inputAmbulances = "{1}";
    String inputStates = "{0}";
    String inputHospitals = "{28}";
    String inputAccidents = "{18, 21, 44}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(1, ambulancePaths.size());
    Vertex a_18 = vertices.get("18");
    Assertions.assertTrue(ambulancePaths.containsKey(a_18));
    AssertAmbulanceAndPath(ambulancePaths, a_18, "[{ 1 }, { 2 }, { 7 }, { 8 }, { 13 }, { 18 }]", 4.81746);
  }

  @Test
  void proofTest2() {
    String inputEdges = "{{1, 2}, {1, 11}, {2, 3}, {3, 4}, {3, 13}, {4, 5}, {4, 14}, {5, 15}, {6, 7}, {6, 16}, {7, 8}, {7, 17}, {8, 9}, {8, 18}, {9, 10}, {9, 19}, {10, 20}, {11, 12}, {11, 21}, {12, 13}, {13, 14}, {13, 23}, {14, 24}, {15, 25}, {16, 17}, {16, 26}, {17, 18}, {17, 27}, {18, 19}, {18, 28}, {19, 20}, {19, 29}, {20, 30}, {21, 22}, {21, 31}, {22, 32}, {23, 24}, {23, 33}, {24, 25}, {24, 34}, {25, 26}, {25, 35}, {26, 27}, {26, 36}, {27, 28}, {27, 37}, {28, 29}, {28, 38}, {29, 30}, {29, 39}, {30, 40}, {31, 32}, {31, 41}, {32, 33}, {32, 42}, {33, 34}, {33, 43}, {35, 36}, {36, 37}, {36, 46}, {37, 38}, {37, 47}, {38, 39}, {38, 48}, {39, 40}, {39, 49}, {40, 50}, {41, 42}, {41, 51}, {42, 43}, {42, 52}, {43, 44}, {43, 53}, {44, 54}, {45, 46}, {45, 55}, {46, 56}, {47, 48}, {47, 57}, {48, 49}, {48, 58}, {49, 50}, {49, 59}, {51, 52}, {51, 61}, {52, 53}, {52, 62}, {53, 54}, {53, 63}, {54, 55}, {54, 64}, {55, 56}, {55, 65}, {56, 57}, {56, 66}, {57, 58}, {57, 67}, {58, 59}, {59, 60}, {59, 68}, {61, 70}, {62, 71}, {63, 64}, {63, 72}, {64, 65}, {65, 66}, {65, 74}, {66, 75}, {67, 76}, {68, 69}, {68, 78}, {69, 79}, {70, 71}, {71, 72}, {71, 81}, {72, 82}, {73, 83}, {74, 84}, {75, 76}, {75, 85}, {76, 77}, {76, 86}, {77, 78}, {77, 87}, {78, 79}, {78, 88}, {79, 89}, {80, 81}, {80, 90}, {81, 82}, {81, 91}, {82, 83}, {83, 84}, {84, 85}, {84, 94}, {85, 86}, {85, 95}, {86, 87}, {86, 96}, {87, 88}, {87, 97}, {88, 89}, {88, 98}, {90, 91}, {91, 92}, {92, 93}, {94, 95}, {95, 96}, {97, 98}, {98, 99}}";
    String inputWeights = "{0.703375, 0.784261, 0.829004, 0.869329, 0.748562, 0.893425, 0.653708, 0.957589, 0.667363, 0.805526, 0.774444, 0.73563, 0.755074, 0.69531, 0.614959, 0.672444, 0.624784, 0.760929, 1.03738, 0.89427, 0.795905, 0.999363, 1.00218, 1.0546, 0.837626, 1.16429, 0.901573, 0.944696, 0.837826, 0.891127, 0.664876, 0.862421, 0.781678, 0.556984, 1.00128, 0.81561, 1.1012, 1.2091, 1.65717, 1.02598, 1.66182, 1.00957, 1.29639, 1.10049, 1.1061, 1.04713, 0.937068, 0.988113, 0.712034, 0.973212, 0.816868, 0.696852, 0.99837, 1.08486, 1.01099, 1.02945, 1.34983, 1.09116, 1.24841, 1.44935, 1.07811, 1.1665, 0.929216, 1.02973, 0.726059, 1.08606, 0.737615, 0.728661, 0.975, 0.915643, 1.0634, 0.936564, 1.15438, 0.985634, 0.964043, 0.967595, 1.19505, 0.888192, 1.248, 0.884907, 0.964052, 0.852116, 1.30489, 0.828469, 0.905975, 1.12908, 1.09785, 1.22939, 1.08866, 1.49718, 0.805292, 1.36009, 0.995144, 1.48768, 1.20279, 1.22501, 1.23988, 1.07607, 0.666634, 1.41805, 0.84532, 1.02544, 1.12996, 1.20816, 1.22105, 1.15477, 1.06479, 1.22923, 1.19593, 0.801484, 1.19898, 0.668349, 0.882984, 0.953381, 1.16892, 1.00683, 0.663063, 0.954983, 1.09698, 1.00384, 1.13421, 0.957261, 1.0309, 0.735248, 0.681852, 0.944594, 0.619566, 0.807938, 0.618474, 1.26809, 1.15403, 1.42848, 1.49422, 1.37849, 0.803128, 1.23825, 0.642581, 1.27841, 0.767574, 0.968954, 0.860625, 0.669729, 0.845074, 0.328445, 0.937256, 0.672901, 0.737365, 0.713149, 0.628288, 0.712777}";
    String inputAmbulances = "{13, 72, 65, 44, 59, 49, 41, 42, 30, 67, 28, 86}";
    String inputStates = "{1, 0, 0, 2, 0, 3, 0, 3, 2, 0, 0, 2}";
    String inputHospitals = "{28, 85}";
    String inputAccidents = "{93, 1, 10, 77, 55}";

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController
        .getAmbulancePaths(vertices, edges, ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);

    Assertions.assertEquals(5, ambulancePaths.size());
    Vertex a_1 = vertices.get("1");
    Vertex a_10 = vertices.get("10");
    Vertex a_55 = vertices.get("55");
    Vertex a_77 = vertices.get("77");
    Vertex a_93 = vertices.get("93");
    Assertions.assertTrue(ambulancePaths.containsKey(a_1));
    Assertions.assertTrue(ambulancePaths.containsKey(a_10));
    Assertions.assertTrue(ambulancePaths.containsKey(a_55));
    Assertions.assertTrue(ambulancePaths.containsKey(a_77));
    Assertions.assertTrue(ambulancePaths.containsKey(a_93));
    AssertAmbulanceAndPath(ambulancePaths, a_1, "[{ 13 }, { 3 }, { 2 }, { 1 }]", 3.280941);
    AssertAmbulanceAndPath(ambulancePaths, a_10, "[{ 28 }, { 18 }, { 8 }, { 9 }, { 10 }]", 2.95647);
    AssertAmbulanceAndPath(ambulancePaths, a_55, "[{ 65 }, { 55 }]", 0.995144);
    AssertAmbulanceAndPath(ambulancePaths, a_77, "[{ 67 }, { 76 }, { 77 }]", 2.33014);
    AssertAmbulanceAndPath(ambulancePaths, a_93, "[{ 72 }, { 71 }, { 81 }, { 91 }, { 92 }, { 93 }]", 4.886488);

  }

  private void AssertAmbulanceAndPath(Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths, Vertex vertex,
      String expectedPath, double expectedWeight) {
    ambulancePaths.forEach((key, value) -> {
      if (key.equals(vertex)) {
        Assertions.assertEquals(vertex.getAmbulance(), key.getAmbulance());
        Assertions.assertEquals(expectedPath, value.getVertexList().toString());
        Assertions.assertEquals(expectedWeight, value.getWeight(), DELTA);
      }
    });
  }

}
