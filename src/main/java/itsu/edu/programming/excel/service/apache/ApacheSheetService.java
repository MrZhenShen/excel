package itsu.edu.programming.excel.service.apache;

import itsu.edu.programming.excel.dto.CellContentDto;
import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.service.SheetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@AllArgsConstructor
public class ApacheSheetService implements SheetService {

  private ApacheExcelService excelService;

  @Override
  public List<CellDto> getSheetCells(int sheetId) {
    List<CellDto> cells = new ArrayList<>();

    Iterator<Row> rowIter = getSheet(sheetId).rowIterator();

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

  @Override
  public XSSFSheet getSheet(int sheetId) {
    try {
      return retrieveSheetAt(sheetId);
    } catch (IllegalArgumentException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @Override
  public XSSFSheet getOrCreateSheet(int sheetId) {
    try {
      return retrieveSheetAt(sheetId);
    } catch (IllegalArgumentException e) {
      log.error("{}", e.getMessage());
      log.warn("Start creating new sheet");
      return createSheet();
    }
  }

  @Override
  public XSSFSheet createSheet() {
    return excelService.getExcel().createSheet();
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

  private XSSFSheet retrieveSheetAt(int sheetId) throws IllegalArgumentException {
    return excelService.getExcel().getSheetAt(sheetId);
  }


  @Override
  public List<Integer> getAllId() {
    return IntStream
            .range(0, excelService.getExcel().getNumberOfSheets())
            .boxed()
            .toList();
  }
}
