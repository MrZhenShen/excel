package itsu.edu.programming.excel.service.apache;

import itsu.edu.programming.excel.dto.CellContentDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.service.CellService;
import itsu.edu.programming.excel.service.ExcelService;
import itsu.edu.programming.excel.service.SheetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ApacheCellService implements CellService {

  private ExcelService excelService;
  private SheetService apacheSheetService;

  @Override
  public CellContentDto getCellContent(int sheetId, String cellId) {
    CellReference cellReference = getCellReference(cellId);
    XSSFCell cell = apacheSheetService.getSheet(sheetId)
            .getRow(cellReference.getRow())
            .getCell(cellReference.getCol());

    return getCellContent(cell);
  }

  @Override
  public CellContentDto setCellValue(int sheetId, String cellId, Object value) {
    CellReference cellReference = getCellReference(cellId);

    XSSFSheet sheet = apacheSheetService.getOrCreateSheet(sheetId);
    XSSFRow row = sheet.getRow(cellReference.getRow());
    if (row == null) {
      row = sheet.createRow(cellReference.getRow());
    }

    XSSFCell cell = row.getCell(cellReference.getCol());
    if (cell == null) {
      cell = row.createCell(cellReference.getCol());
    }

    if (isNumeric(value.toString())) {
      cell.setCellType(CellType.NUMERIC);
      cell.setCellValue(Double.parseDouble(value.toString()));
    } else if (isFormula((String) value)) {
      cell.setCellFormula(((String) value).substring(1));
      excelService.evaluateFormulaCell(cell);
    } else {
      cell.setCellType(CellType.STRING);
      cell.setCellValue((String) value);
    }
    excelService.recalculateFormulas(sheet);
    return getCellContent(cell);
  }

  @Override
  public void clearCell(int sheetId, String cellId) {
    XSSFSheet sheet = apacheSheetService.getSheet(sheetId);
    if (sheet == null) {
      return;
    }

    CellReference cellReference = getCellReference(cellId);
    XSSFRow row = sheet.getRow(cellReference.getRow());
    if (row == null) {
      return;
    }

    XSSFCell cell = row.getCell(cellReference.getCol());
    if (cell != null) {
      row.removeCell(cell);
    }
  }

  private CellReference getCellReference(String cellId) {
    try {
      return new CellReference(cellId);
    } catch (IllegalArgumentException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  private static CellContentDto getCellContent(XSSFCell cell) {
    return switch (cell.getCellType()) {
      case NUMERIC -> new CellContentDto(
              String.valueOf(cell.getNumericCellValue()),
              String.valueOf(cell.getNumericCellValue())
      );
      case FORMULA -> new CellContentDto(cell.getCellFormula(), cell.getRawValue());
      default -> new CellContentDto(cell.getStringCellValue(), cell.getStringCellValue());
    };
  }

  private static boolean isNumeric(String value) {
    try {
      Double.parseDouble(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean isFormula(String formula) {
    return formula.charAt(0) == '=';
  }
}
