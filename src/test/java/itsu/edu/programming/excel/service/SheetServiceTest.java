package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Cell;
import itsu.edu.programming.excel.model.Sheet;
import itsu.edu.programming.excel.repository.SheetRepository;
import itsu.edu.programming.excel.service.jpa.JPASheetService;
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
  private JPASheetService sheetService;

  @Mock
  private SheetRepository sheetRepository;

  @Mock
  private CellMapper cellMapper;

  @Nested
  class GetAllJPACellBySheet {

    @Test
    void testGetCellsBySheetId() {
      // Arrange
      int sheetId = 1;
      Sheet Sheet = new Sheet();
      Sheet.setId(sheetId);

      Set<Cell> Cells = new HashSet<>();
      Cells.add(new Cell());
      Cells.add(new Cell());
      System.out.println(Cells);

      Sheet.setCells(Cells);

      when(sheetRepository.findById(sheetId)).thenReturn(Optional.of(Sheet));

      when(cellMapper.cellToCellDto(any(Cell.class))).thenAnswer(invocation -> {
        Cell Cell = invocation.getArgument(0);
        return new CellDto(Cell.getColumnIndex() + Cell.getRowIndex(), Cell.getValue(), Cell.getResult());
      });

      // Act
      List<CellDto> result = sheetService.getSheetCells(sheetId);

      // Assert
      assertNotNull(result, "Result of getCellsBySheetId should not be null");
      assertEquals(Cells.size(), result.size(), "Size result is not equal to mocked size of cells ");

      verify(sheetRepository, times(1)).findById(sheetId);
      verify(cellMapper, times(Cells.size())).cellToCellDto(any(Cell.class));
    }

    @Test
    void testGetCellsBySheetIdSheetNotFound() {
      // Arrange
      int sheetId = 1;

      when(sheetRepository.findById(sheetId)).thenReturn(Optional.empty());

      // Act
      WebException exception = assertThrows(
              WebException.class,
              () -> sheetService.getSheetCells(sheetId),
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
      List<Integer> sheetIds = new ArrayList<>();
      sheetIds.add(1);
      sheetIds.add(2);

      List<Sheet> Sheets = new ArrayList<>();
      Sheet Sheet1 = new Sheet();
      Sheet1.setId(1);
      Sheet Sheet2 = new Sheet();
      Sheet2.setId(2);
      Sheets.add(Sheet1);
      Sheets.add(Sheet2);

      when(sheetRepository.findAll()).thenReturn(Sheets);

      // Act
      List<Integer> result = sheetService.getAllId();

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
      List<Integer> result = sheetService.getAllId();

      // Assert
      assertNotNull(result, "Result should not be null");
      assertTrue(result.isEmpty(), "Result should be empty");

      verify(sheetRepository, times(1)).findAll();
    }
  }
}
