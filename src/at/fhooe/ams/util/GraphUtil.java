package at.fhooe.ams.util;

import at.fhooe.ams.model.Accident;
import at.fhooe.ams.model.Ambulance;
import at.fhooe.ams.model.Hospital;
import at.fhooe.ams.model.Vertex;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphUtil {

  public static void setParams(Graph<Vertex, DefaultWeightedEdge> graph, Map<Vertex, Ambulance> ambulances, Map<Vertex, Hospital> hospital, Map<Vertex, Accident> accidents) {
    graph.vertexSet().forEach(vertex -> {
      if(ambulances.containsKey(vertex)) {
        vertex.setAmbulance(ambulances.get(vertex));
      } else if (hospital.containsKey(vertex)) {
        vertex.setHospital(hospital.get(vertex));
      } else if (accidents.containsKey(vertex)) {
        vertex.setAccident(accidents.get(vertex));
      }
    });
  }

  public static GraphPath<Vertex, DefaultWeightedEdge> mergeHospitalPath(GraphPath<Vertex, DefaultWeightedEdge> path,
      GraphPath<Vertex, DefaultWeightedEdge> vertexDefaultWeightedEdgeGraphPath) {
    GraphWalk<Vertex, DefaultWeightedEdge> current = (GraphWalk<Vertex, DefaultWeightedEdge>)path;
    GraphWalk<Vertex, DefaultWeightedEdge> other = (GraphWalk<Vertex, DefaultWeightedEdge>)vertexDefaultWeightedEdgeGraphPath;
    double totalWeight = current.getWeight()  + other.getWeight() + Hospital.HOSPITAL_DURATION;
    return  other.concat(current, g -> totalWeight);
  }

  public static void addBreakTime(GraphPath<Vertex, DefaultWeightedEdge> path) {
    ((GraphWalk<Vertex, DefaultWeightedEdge>)path).setWeight(path.getWeight() + Ambulance.BREAK_DURATION);
  }

}
