package itsu.edu.programming.excel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import itsu.edu.programming.excel.dto.CellContentDto;
import itsu.edu.programming.excel.service.apache.ApacheCellService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cell")
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CellController {

  private ApacheCellService cellService;

  @Operation(summary = "Get cell")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Cell found"),
          @ApiResponse(responseCode = "400", description = "Invalid cell ID format"),
          @ApiResponse(responseCode = "404", description = "Cell not found")
  })
  @GetMapping("/{sheetId}/{cellId}")
  public CellContentDto getCell(@PathVariable("sheetId") int sheetId, @PathVariable("cellId") String cellId) {
    return cellService.getCellContent(sheetId, cellId);
  }

  @Operation(summary = "Set value to cell")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Cell updated"),
          @ApiResponse(responseCode = "201", description = "Sheet created. Cell set"),
          @ApiResponse(responseCode = "422", description = "Invalid formula in value")
  })
  @PostMapping("/{sheetId}/{cellId}")
  public CellContentDto setValueToCell(
          @PathVariable("sheetId") int sheetId,
          @PathVariable("cellId") String cellId,
          @RequestParam(required = false) String value) {
    if (value == null || value.isBlank() || value.isEmpty()) {
      cellService.clearCell(sheetId, cellId);
      return null;
    } else {
      return cellService.setCellValue(sheetId, cellId, value);
    }
  }
}
