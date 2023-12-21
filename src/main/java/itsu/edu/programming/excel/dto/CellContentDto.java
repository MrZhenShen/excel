package itsu.edu.programming.excel.dto;

import itsu.edu.programming.excel.model.Cell;

/**
 * A DTO for the {@link Cell} entity
 */
public record CellContentDto(
        String value,
        String result
) {
}
