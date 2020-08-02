package at.fhooe.ams.util;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class StringUtil {

  public static String normalizeSet(String string) {
    return string.replaceAll("\\{", "").replaceAll("\\}", "");
  }

  public static String[] trimAll(String[] string) {
    return Arrays.stream(string).map(String::trim).toArray(String[]::new);
  }

}
