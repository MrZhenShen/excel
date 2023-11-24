package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;

import java.util.Set;


public interface SheetService {

  Set<CellDto> getCellsBySheetId(long sheetId);

  Set<Long> getAllSheetsId();
}
