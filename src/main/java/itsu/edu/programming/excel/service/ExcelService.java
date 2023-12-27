package itsu.edu.programming.excel.service;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface ExcelService {

  XSSFWorkbook getExcel();

  void write();

  void evaluateFormulaCell(XSSFCell cell);

  void recalculateFormulas(XSSFSheet sheet);
}
