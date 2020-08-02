package at.fhooe.ams.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Hospital {

  private String name;
  public static final int HOSPITAL_DURATION = 3;

}
