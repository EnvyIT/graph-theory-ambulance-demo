package at.fhooe.ams;

import at.fhooe.ams.algorithm.GraphController;
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

public class App {



  public static void main(String[] args) {
    System.out.println("======= Routeplanning for Ambulances =======");

    if(args.length != 6) {
      System.out.println("You must call the program with args: [edges] [weights] [ambulances] [states] [hospitals] [accidents]");
      return;
    }
    String inputEdges =  args[0];
    String inputWeights = args[1];
    String inputAmbulances = args[2];
    String inputStates = args[3];
    String inputHospitals = args[4];
    String inputAccidents = args[5];

    Map<String, Vertex> vertices = Parser.parseVertices(inputEdges);
    Map<Vertex, Ambulance> ambulances = Parser.parseAmbulances(inputAmbulances, inputStates, vertices);
    Map<Vertex, Hospital> hospitals = Parser.parseHospitals(inputHospitals, vertices);
    Map<Vertex, Accident> accidents = Parser.parseAccidents(inputAccidents, vertices);
    List<Pair<Vertex, Vertex>> edges = Parser.parseEdges(inputEdges, vertices);
    List<Double> weights = Parser.parseWeights(inputWeights);

    Map<Vertex, GraphPath<Vertex, DefaultWeightedEdge>> ambulancePaths = GraphController.getAmbulancePaths(vertices, edges ,ambulances, hospitals, accidents, weights);

    GraphController.printAccidentPaths(ambulancePaths);
  }

}
