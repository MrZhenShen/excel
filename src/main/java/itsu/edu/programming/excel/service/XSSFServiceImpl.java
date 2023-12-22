package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellContentDto;
import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@Slf4j
public class XSSFServiceImpl implements XSSFService, CellService, SheetService {

  @Value("${excel.file.location}")
  private String FILE_LOCATION;

  @Override
  public CellContentDto getCellContent(int sheetId, String cellId) {

    CellReference cellReference = getCellReference(cellId);
    XSSFCell cell = getSheetAt(sheetId)
            .getRow(cellReference.getRow())
            .getCell(cellReference.getCol());

    return getCellContent(cell);
  }

  private XSSFSheet getSheetAt(int sheetId) {
    XSSFWorkbook excel;

    try (FileInputStream inputStream = new FileInputStream(FILE_LOCATION)) {
      excel = new XSSFWorkbook(inputStream);
    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    try {
      return excel.getSheetAt(sheetId);
    } catch (IllegalArgumentException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  private XSSFSheet getSheetAt(XSSFWorkbook excel, int sheetId) {
    try {
      if (excel.getNumberOfSheets() - 1 < sheetId) {
        return excel.createSheet();
      } else {
        return excel.getSheetAt(sheetId);
      }
    } catch (IllegalArgumentException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.BAD_REQUEST, e.getMessage());
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

  @Override
  public CellContentDto setCellValue(int sheetId, String cellId, Object value) {

    return dox((XSSFWorkbook excel) -> {
      CellReference cellReference = getCellReference(cellId);

      XSSFSheet sheet = getSheetAt(excel, sheetId);

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
        excel.getCreationHelper().createFormulaEvaluator().evaluateFormulaCell(cell);
      } else {
        cell.setCellType(CellType.STRING);
        cell.setCellValue((String) value);
      }

      recalculateFormulas(excel);

      return getCellContent(cell);
    });

  }

  @Override
  public void clearCell(int sheetId, String cellId) {
    dox((XSSFWorkbook excel) -> {
      XSSFSheet sheet = getSheetAt(excel, sheetId);
      if (sheet == null) {
        return;
      }

      CellReference cellReference = getCellReference(cellId);
      XSSFRow row = sheet.getRow(cellReference.getRow());
      if (row == null) {
        return;
      }

      XSSFCell cell = row.getCell(cellReference.getCol());
      if (cell == null) {
        return;
      }
      row.removeCell(cell);
    });
  }

  @Override
  public List<CellDto> getCellsBySheetId(int sheetId) {
    List<CellDto> cells = new ArrayList<>();

    Iterator<Row> rowIter = getSheetAt(sheetId).rowIterator();

    while (rowIter.hasNext()) {
      XSSFRow myRow = (XSSFRow) rowIter.next();

      Iterator<Cell> cellIter = myRow.cellIterator();
      while (cellIter.hasNext()) {
        XSSFCell cell = (XSSFCell) cellIter.next();

        CellContentDto cellContentDto = getCellContent(cell);
        cells.add(new CellDto(cell.getReference(), cellContentDto.value(), cellContentDto.result()));
      }
    }

    return cells;
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

  @Override
  public List<Integer> getAllId() {
    try (FileInputStream inputStream = new FileInputStream(FILE_LOCATION)) {
      return IntStream.range(0, new XSSFWorkbook(inputStream).getNumberOfSheets())
              .boxed().toList();
    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
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

  private void dox(Consumer<XSSFWorkbook> consumer) {
    XSSFWorkbook excel;
    try (FileInputStream inputStream = new FileInputStream(FILE_LOCATION)) {
      excel = new XSSFWorkbook(inputStream);
    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    consumer.accept(excel);

    try (FileOutputStream fileOut = new FileOutputStream(FILE_LOCATION)) {
      excel.write(fileOut);
      excel.close();

    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private CellContentDto dox(Function<XSSFWorkbook, CellContentDto> function) {
    XSSFWorkbook excel;
    try (FileInputStream inputStream = new FileInputStream(FILE_LOCATION)) {
      excel = new XSSFWorkbook(inputStream);
    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    CellContentDto cellContentDto = function.apply(excel);

    try (FileOutputStream fileOut = new FileOutputStream(FILE_LOCATION)) {
      excel.write(fileOut);
      excel.close();

    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    return cellContentDto;
  }

  private static void recalculateFormulas(XSSFWorkbook workbook) {
    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    for (Sheet sheet : workbook) {
      for (Row row : sheet) {
        for (Cell cell : row) {
          if (cell.getCellType() == CellType.FORMULA) {
            formulaEvaluator.evaluateFormulaCell(cell);
          }
        }
      }
    }
  }
}
