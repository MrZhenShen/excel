package itsu.edu.programming.excel.repository;

import itsu.edu.programming.excel.model.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SheetRepository extends JpaRepository<Sheet, Integer> {

}
