package itsu.edu.programming.excel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity(name = "cell")
@Table(name = "cell")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@IdClass(CellId.class)
public class Cell {

  @Id
  @Column(nullable = false)
  private String columnIndex;

  @Id
  @Column(nullable = false)
  private long rowIndex;

  @Id
  @ManyToOne
  @JoinColumn(name = "sheet_id", nullable = false)
  private Sheet sheet;

  @Column(nullable = false)
  private String value;

  @Column(nullable = false)
  private String result;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Cell cell)) return false;

    if (rowIndex != cell.rowIndex) return false;
    if (columnIndex != null ? !columnIndex.equals(cell.columnIndex) : cell.columnIndex != null) return false;
    if (sheet != null ? !sheet.equals(cell.sheet) : cell.sheet != null) return false;
    return Objects.equals(value, cell.value);
  }

  @Override
  public int hashCode() {
    int result = columnIndex != null ? columnIndex.hashCode() : 0;
    result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
    result = 31 * result + (sheet != null ? sheet.hashCode() : 0);
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
}
