package itsu.edu.programming.excel.repository;

import itsu.edu.programming.excel.model.Cell;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface CellRepository extends JpaRepository<Cell, String> {

  Optional<Cell> findByColumnIndexAndRowIndexAndSheetId(String columnIndex, long rowIndex, long id);

}
