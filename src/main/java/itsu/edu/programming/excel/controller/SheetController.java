package itsu.edu.programming.excel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.service.XSSFServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sheet")
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class SheetController {

  private XSSFServiceImpl sheetService;

  @Operation(summary = "Get all cells from sheet")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Sheet is found"),
          @ApiResponse(responseCode = "404", description = "Sheet is not found")
  })
  @PostMapping("/{sheetId}")
  public List<CellDto> getAllCellBySheet(@RequestParam int sheetId) {
    return sheetService.getCellsBySheetId(sheetId);
  }

  @Operation(summary = "Get all sheet IDs")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public List<Integer> getAllSheetsId() {
    return sheetService.getAllId();
  }
}
