package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Sheet;
import itsu.edu.programming.excel.repository.SheetRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SheetServiceImpl {

  private SheetRepository sheetRepository;

  private CellMapper cellMapper;

//  @Override
//  @Transactional
//  public Set<CellDto> getCellsBySheetId(long sheetId) {
//    Sheet sheet = sheetRepository
//            .findById(sheetId)
//            .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Sheet is not found"));
//
//    return sheet
//            .getCells()
//            .stream()
//            .map(cellMapper::cellToCellDto)
//            .collect(Collectors.toSet());
//  }
//
//  @Override
//  public Set<Long> getAllId() {
//    return sheetRepository
//            .findAll()
//            .stream()
//            .map(Sheet::getId)
//            .collect(Collectors.toSet());
//  }
//
//  @Override
//  public boolean existsById(long sheetId) {
//    return sheetRepository.existsById(sheetId);
//  }
//
//  @Override
//  public long create() {
//    return sheetRepository.save(new Sheet()).getId();
//  }
}
