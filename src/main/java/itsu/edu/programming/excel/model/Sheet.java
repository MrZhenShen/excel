package itsu.edu.programming.excel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Set;

@Entity(name = "sheet")
@Table(name = "sheet")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Sheet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "sheet")
  @ToString.Exclude
  private Set<Cell> cells;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Sheet sheet)) return false;

    if (id != sheet.id) return false;
    return Objects.equals(cells, sheet.cells);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (cells != null ? cells.hashCode() : 0);
    return result;
  }
}
