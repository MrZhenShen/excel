package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellContentDto;

public interface CellService {

  CellContentDto getCellContent(int sheetId, String cellId);

  CellContentDto setCellValue(int sheetId, String cellId, Object value);

  void clearCell(int sheetId, String cellId);
}
