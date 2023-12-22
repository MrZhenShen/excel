package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;

import java.util.List;

public interface SheetService {

  List<CellDto> getCellsBySheetId(int sheetId);

  List<Integer> getAllId();
}
