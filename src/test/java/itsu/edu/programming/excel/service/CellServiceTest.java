package itsu.edu.programming.excel.service;

import itsu.edu.programming.excel.dto.CellContentDto;
import itsu.edu.programming.excel.exception.WebException;
import itsu.edu.programming.excel.mapper.CellMapper;
import itsu.edu.programming.excel.model.Cell;
import itsu.edu.programming.excel.repository.CellRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class CellServiceTest {

  @InjectMocks
  private CellServiceImpl cellService;

  @Mock
  private CellRepository cellRepository;

  @Mock
  private CellMapper cellMapper;

  @Nested
  class GetCell {

    @ParameterizedTest
    @CsvSource({
            "A",
            "1",
            "A1B12",
            "AB1C2"
    })
    void Should_Fail_When_CellIDIsDoesNotMatchCellPattern(String cellId) {
      // Act, Assert
      WebException exception = assertThrows(
              WebException.class,
              () -> cellService.getCellContent(1L, cellId),
              "Cell id should be illegal"
      );
      assertEquals(
              HttpStatus.BAD_REQUEST,
              exception.toExceptionResponse().getStatusCode(),
              "WebException should have BAD_REQUEST status."
      );
    }

    @ParameterizedTest
    @CsvSource({
            "A1, A, 1",
            "A12, A, 12",
            "AB12, AB, 12"
    })
    void Should_Success_When_CellIDIsMatchCellPattern(String cellId, String columnIndex, long rowIndex) {
      // Arrange
      long sheetId = 1L;

      Cell cell = new Cell();
      cell.setValue("1");
      cell.setResult("1");

      when(cellRepository.findByColumnIndexAndRowIndexAndSheetId(columnIndex, rowIndex, sheetId)).thenReturn(Optional.of(cell));
      when(cellMapper.cellToCellContentDto(cell)).thenReturn(new CellContentDto(cell.getValue(), cell.getResult()));

      // Act
      CellContentDto result = cellService.getCellContent(sheetId, cellId);

      // Assert
      assertNotNull(result, "Result should not be null");
      assertEquals("1", result.value(), "Cell value is not correct");

      verify(cellRepository, times(1)).findByColumnIndexAndRowIndexAndSheetId(columnIndex, rowIndex, sheetId);
      verify(cellMapper, times(1)).cellToCellContentDto(cell);
    }
  }

  @Nested
  class SetCellValue {

//    @Test
//    void test() {
//      cellService.setCellValue(anyLong(), anyString(), "1");
//
//    }
  }
}
