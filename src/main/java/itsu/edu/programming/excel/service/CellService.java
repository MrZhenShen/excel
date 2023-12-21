package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellContentDto;

public interface CellService {

  CellContentDto getCellContent(long sheetId, String cellId);

  CellContentDto setCellValue(long sheetId, String cellId, String value);

  CellContentDto clearCell(long sheetId, String cellId);
}
