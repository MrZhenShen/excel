package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Cell;
import itsu.edu.programming.excel.model.Sheet;
import itsu.edu.programming.excel.repository.SheetRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class SheetServiceTest {

  @InjectMocks
  private SheetServiceImpl sheetService;

  @Mock
  private SheetRepository sheetRepository;

  @Mock
  private CellMapper cellMapper;

  @Nested
  class GetAllCellBySheet {

    @Test
    void testGetCellsBySheetId() {
      // Arrange
      long sheetId = 1L;
      Sheet sheet = new Sheet();
      sheet.setId(sheetId);

      Set<Cell> cells = new HashSet<>();
      cells.add(new Cell());
      cells.add(new Cell());
      System.out.println(cells);

      sheet.setCells(cells);

      when(sheetRepository.findById(sheetId)).thenReturn(Optional.of(sheet));

      when(cellMapper.cellToCellDto(any(Cell.class))).thenAnswer(invocation -> {
        Cell cell = invocation.getArgument(0);
        return new CellDto(cell.getColumnIndex() + cell.getRowIndex(), cell.getValue(), cell.getResult());
      });

      // Act
      Set<CellDto> result = sheetService.getCellsBySheetId(sheetId);

      // Assert
      assertNotNull(result, "Result of getCellsBySheetId should not be null");
      assertEquals(cells.size(), result.size(), "Size result is not equal to mocked size of cells ");

      verify(sheetRepository, times(1)).findById(sheetId);
      verify(cellMapper, times(cells.size())).cellToCellDto(any(Cell.class));
    }

    @Test
    void testGetCellsBySheetIdSheetNotFound() {
      // Arrange
      long sheetId = 1L;

      when(sheetRepository.findById(sheetId)).thenReturn(Optional.empty());

      // Act
      WebException exception = assertThrows(
              WebException.class,
              () -> sheetService.getCellsBySheetId(sheetId),
              "Not existing sheet should throw a WebException."
      );

      // Assert
      assertEquals(
              HttpStatus.NOT_FOUND,
              exception.toExceptionResponse().getStatusCode(),
              "WebException should have NOT_FOUND status."
      );

      // Assert: Expecting WebException
    }
  }

  @Nested
  class GetAllSheetsId {

    @Test
    void testGetAllSheetsId() {
      // Arrange
      Set<Long> sheetIds = new HashSet<>();
      sheetIds.add(1L);
      sheetIds.add(2L);

      List<Sheet> sheets = new ArrayList<>();
      Sheet sheet1 = new Sheet();
      sheet1.setId(1L);
      Sheet sheet2 = new Sheet();
      sheet2.setId(2L);
      sheets.add(sheet1);
      sheets.add(sheet2);

      when(sheetRepository.findAll()).thenReturn(sheets);

      // Act
      Set<Long> result = sheetService.getAllSheetsId();

      // Assert
      assertNotNull(result, "Result should not be null");
      assertEquals(sheetIds, result, "Resulted ids are not equal to retrieved from repository");

      verify(sheetRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSheetsIdEmptyList() {
      // Arrange
      when(sheetRepository.findAll()).thenReturn(Collections.emptyList());

      // Act
      Set<Long> result = sheetService.getAllSheetsId();

      // Assert
      assertNotNull(result, "Result should not be null");
      assertTrue(result.isEmpty(), "Result should be empty");

      verify(sheetRepository, times(1)).findAll();
    }
  }
}
