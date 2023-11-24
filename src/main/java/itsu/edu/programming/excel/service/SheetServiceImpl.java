package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Sheet;
import itsu.edu.programming.excel.repository.SheetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SheetServiceImpl implements SheetService {

  private SheetRepository sheetRepository;

  private CellMapper cellMapper;

  @Override
  public Set<CellDto> getCellsBySheetId(long sheetId) {
    Sheet sheet = sheetRepository
            .findById(sheetId)
            .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sheet is missing"));

    if (!Hibernate.isInitialized(sheet.getCells())) {
      Hibernate.initialize(sheet.getCells());
    }

    return sheet
            .getCells()
            .stream()
            .map(cellMapper::cellToCellDto)
            .collect(Collectors.toSet());
  }

  @Override
  public Set<Long> getAllSheetsId() {
    return sheetRepository
            .findAll()
            .stream()
            .map(Sheet::getId)
            .collect(Collectors.toSet());
  }
}
