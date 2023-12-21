package itsu.edu.programming.excel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CellId implements Serializable {

  private String columnIndex;
  private long rowIndex;
  private Sheet sheet;
}
