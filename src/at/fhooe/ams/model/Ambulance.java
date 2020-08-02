package at.fhooe.ams.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Ambulance {

  private final String name;
  private final AmbulanceStatus status;
  public static final int BREAK_DURATION = 1;

  @Override
  public String toString() {
    return "Ambulance: " + name;
  }

}
