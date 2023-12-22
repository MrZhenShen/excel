package itsu.edu.programming.excel.service;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTest {

  @Test
  void test() throws IOException {

    String fileLocation = "src/test/resources/static/Book1.xlsx";
    FileInputStream inputStream = new FileInputStream(fileLocation);
    XSSFWorkbook excel = new XSSFWorkbook(inputStream);
    XSSFSheet sheet = excel.getSheetAt(0);


    XSSFCell formulaCell;
    XSSFRow row = sheet.getRow(0);
    int lastCellNum = row.getLastCellNum();
    if (lastCellNum == 2) {
      formulaCell = row.createCell(lastCellNum);

    } else {
      formulaCell = row.getCell(2);
    }

    formulaCell.setCellFormula("SUM(A:A)");
    XSSFFormulaEvaluator formulaEvaluator = excel.getCreationHelper().createFormulaEvaluator();
    formulaEvaluator.evaluateFormulaCell(formulaCell);
    System.out.println(
            formulaCell.getCellFormula()
    );
    System.out.println(
            formulaCell.getRawValue()
    );
    FileOutputStream fileOut = new FileOutputStream(fileLocation);
    excel.write(fileOut);
    excel.close();
    fileOut.close();

  }
}
