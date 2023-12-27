package itsu.edu.programming.excel.service.jpa;

import itsu.edu.programming.excel.dto.CellContentDto;
import itsu.edu.programming.excel.exception.InvalidCellIdException;
import itsu.edu.programming.excel.exception.InvalidFormulaException;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Cell;
import itsu.edu.programming.excel.repository.CellRepository;
import itsu.edu.programming.excel.service.CellService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
public class JPACellService implements CellService {

  public static final Pattern CELL_ID_PATTERN = Pattern.compile("([A-Za-z]+)(\\S+)");
  public static final Pattern ARITHMETIC_OPERATION_PATTERN = Pattern.compile("[=+\\-*/]");

  public static final String ERROR_RESULT = "<ERROR>";

  public static final Pattern ANYTHING_AROUND_DIGIT_CELL_INDEX_PATTERN = Pattern.compile(".*\\d.*");
  public static final Pattern ANYTHING_AROUND_CHAR_CELL_INDEX_PATTERN = Pattern.compile(".*[A-Za-z].*");

  public static final int CHAR_GROUP_INDEX = 1;
  public static final int NUMBERS_GROUP_INDEX = 2;

  private CellRepository cellRepository;

  private CellMapper cellMapper;

  @Override
  public CellContentDto getCellContent(int sheetId, String cellId) {
    return cellMapper
            .cellToCellContentDto(findCell(sheetId, cellId)
                    .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Cell is not found"))
            );
  }

  private Optional<Cell> findCell(long sheetId, String cellId) {
    String[] parsedCellId;

    try {
      parsedCellId = parseCellId(cellId);
    } catch (InvalidCellIdException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    return cellRepository.findByColumnIndexAndRowIndexAndSheetId(
            parsedCellId[0],
            Long.parseLong(parsedCellId[CHAR_GROUP_INDEX]),
            sheetId
    );
  }

  @Override
  @Transactional
  public CellContentDto setCellValue(int sheetId, String cellId, Object object) {
    String value = (String) object;
    Cell cell = findCell(sheetId, cellId)
            .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Cell is not found"));

    cell.setValue(value);
    try {
      cell.setResult(processValueToResult(value, sheetId));
    } catch (ScriptException e) {
      log.error("{}", e.getMessage());
      cell.setResult(ERROR_RESULT);
    }

    return cellMapper.cellToCellContentDto(cellRepository.save(cell));
  }

  private String processValueToResult(String value, long sheetId) throws ScriptException {
    value = value.trim();
    if (isFormula(value)) {
      try {
        return parseFormula(value, sheetId);
      } catch (InvalidFormulaException e) {
        log.error("{}", e.getMessage());
        return ERROR_RESULT;
      }
    } else {
      return value;
    }
  }

  @Override
  public void clearCell(int sheetId, String cellId) {
    String[] parsedCellId;
    try {
      parsedCellId = parseCellId(cellId);
    } catch (InvalidCellIdException e) {
      log.error("{}", e.getMessage());
      throw new WebException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    Optional<Cell> cellOptional = cellRepository.findByColumnIndexAndRowIndexAndSheetId(
            parsedCellId[0],
            Long.parseLong(parsedCellId[CHAR_GROUP_INDEX]), sheetId
    );
    cellOptional.ifPresent(cell -> cellRepository.delete(cell));
  }

  private static String[] parseCellId(String cellId) throws InvalidCellIdException {
    Matcher matcher = CELL_ID_PATTERN.matcher(cellId);

    if (matcher.find()) {
      String letters = matcher.group(CHAR_GROUP_INDEX);
      String numbers = matcher.group(NUMBERS_GROUP_INDEX);

      if (isValidCellIndex(letters, numbers)) {
        return new String[]{letters, numbers};
      }
    }
    throw new InvalidCellIdException(cellId);
  }

  private static boolean isValidCellIndex(String letters, String numbers) {
    return !ANYTHING_AROUND_DIGIT_CELL_INDEX_PATTERN.matcher(letters).matches() &&
            !ANYTHING_AROUND_CHAR_CELL_INDEX_PATTERN.matcher(numbers).matches();
  }

  private String parseFormula(String formula, long sheetId) throws InvalidFormulaException {
    return createFormula(
            parseValues(sheetId, getValues(formula)),
            getArithmeticOperations(formula)
    );
  }

  private static String createFormula(Queue<String> parsedValues, Queue<String> arithmeticOperations) {
    StringBuilder mergedFormula = new StringBuilder();
    boolean isFirst = true;

    while (!parsedValues.isEmpty() || !arithmeticOperations.isEmpty()) {
      if (isFirst) {
        mergedFormula.append(arithmeticOperations.poll());
        isFirst = false;
      } else {
        mergedFormula.append(parsedValues.poll());
        if (!arithmeticOperations.isEmpty()) {
          mergedFormula.append(arithmeticOperations.poll());
        }
      }
    }

    log.info("{}", mergedFormula);
    return mergedFormula.toString();
  }

  private Queue<String> parseValues(long sheetId, Queue<String> values) throws InvalidFormulaException {
    Queue<String> parsedValues = new ArrayDeque<>();

    for (String value : values) {
      try {
        String[] parsedCellId = parseCellId(value);
        Optional<Cell> cellOptional = cellRepository.findByColumnIndexAndRowIndexAndSheetId(
                parsedCellId[0],
                Long.parseLong(parsedCellId[CHAR_GROUP_INDEX]), sheetId
        );

        if (cellOptional.isPresent()) {
          parsedValues.add(cellOptional.get().getResult());
        } else {
          parsedValues.add("0");
        }
      } catch (InvalidCellIdException invalidCellIdException) {
        log.warn("{}", invalidCellIdException.getMessage());
        try {
          Long.parseLong(value);
          parsedValues.add(value);
        } catch (NumberFormatException e) {
          throw new InvalidFormulaException(e.getMessage(), e);
        }
      }
    }

    return parsedValues;
  }

  private static Queue<String> getValues(String formula) {
    return new ArrayDeque<>(List.of(formula
            .substring(CHAR_GROUP_INDEX)
            .split(ARITHMETIC_OPERATION_PATTERN.pattern())
    ));
  }

  private static Queue<String> getArithmeticOperations(String formula) {
    Queue<String> symbols = new LinkedList<>();

    Matcher matcher = ARITHMETIC_OPERATION_PATTERN.matcher(formula);

    while (matcher.find()) {
      symbols.add(matcher.group());
    }
    return symbols;
  }

  private static boolean isFormula(String formula) {
    return formula.charAt(0) == '=';
  }
}
