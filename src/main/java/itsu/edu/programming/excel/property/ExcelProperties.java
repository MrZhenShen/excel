package itsu.edu.programming.excel.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "excel")
@Getter
@Setter
public class ExcelProperties {

  private FileProperties file;

  @Getter
  @Setter
  public static class FileProperties {
    private String location;
  }
}
