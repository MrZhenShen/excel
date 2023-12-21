package itsu.edu.programming.excel.dto;

/**
 * A DTO for the {@link itsu.edu.programming.excel.model.Cell} entity
 */
public record CellDto(
        String cellId,
        String value,
        String result
) {
}
