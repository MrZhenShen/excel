package itsu.edu.programming.excel.service.jpa;

import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Sheet;
import itsu.edu.programming.excel.repository.SheetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class JPASheetService {

  private SheetRepository sheetRepository;

  private CellMapper cellMapper;

  @Transactional
  public List<CellDto> getSheetCells(int sheetId) {
    return sheetRepository
            .findById(sheetId)
            .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sheet is not found"))
            .getCells()
            .stream()
            .map(cellMapper::cellToCellDto)
            .toList();
  }

  public List<Integer> getAllId() {
    return sheetRepository
            .findAll()
            .stream()
            .map(Sheet::getId)
            .toList();
  }

  public Sheet getSheet(int sheetId) {
    return sheetRepository
            .findById(sheetId)
            .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sheet is not found"));
  }

  public Sheet getOrCreateSheet(int sheetId) {
    return sheetRepository
            .findById(sheetId)
            .orElseGet(this::createSheet);
  }

  public Sheet createSheet() {
    return sheetRepository.save(new Sheet());
  }
}
