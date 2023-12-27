package itsu.edu.programming.excel.service.apache;

import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.property.ExcelProperties;
import itsu.edu.programming.excel.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class ApacheExcelService implements ExcelService {

  private XSSFWorkbook excel;

  private final ExcelProperties excelProperties;

  @Autowired
  public ApacheExcelService(ExcelProperties excelProperties) {
    this.excelProperties = excelProperties;
  }

  @Override
  public XSSFWorkbook getExcel() {
    try (FileInputStream inputStream = new FileInputStream(excelProperties.getFile().getLocation())) {
      excel = new XSSFWorkbook(inputStream);
      return excel;
    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  public void write() {
    try (FileOutputStream fileOut = new FileOutputStream(excelProperties.getFile().getLocation())) {
      excel.write(fileOut);
      excel.close();

    } catch (IOException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  public void evaluateFormulaCell(XSSFCell cell) {
    excel.getCreationHelper().createFormulaEvaluator().evaluateFormulaCell(cell);
  }

  @Override
  public void recalculateFormulas(XSSFSheet sheet) {
    FormulaEvaluator formulaEvaluator = excel.getCreationHelper().createFormulaEvaluator();

    for (Row row : sheet) {
      for (Cell cell : row) {
        if (cell.getCellType() == CellType.FORMULA) {
          formulaEvaluator.evaluateFormulaCell(cell);
        }
      }
    }
  }

}
