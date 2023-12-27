package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

public interface SheetService {

  List<CellDto> getSheetCells(int sheetId);

  List<Integer> getAllId();

  XSSFSheet getSheet(int sheetId);

  XSSFSheet getOrCreateSheet(int sheetId);

  XSSFSheet createSheet();
}
