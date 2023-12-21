package itsu.edu.programming.excel.mapper;

import itsu.edu.programming.excel.dto.CellDto;
import itsu.edu.programming.excel.model.Cell;
import itsu.edu.programming.excel.dto.CellContentDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CellMapper {
  Cell cellDtoToCell(CellDto cellDto);

  @Mapping(target = "cellId", expression = "java(mapCellId(cell))")
  CellDto cellToCellDto(Cell cell);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Cell updateCellFromCellDto(CellDto cellDto, @MappingTarget Cell cell);

  default String mapCellId(Cell cell) {
    return cell.getColumnIndex() + cell.getRowIndex();
  }

  Cell cellContentDtoToCell(CellContentDto cellContentDto);

  CellContentDto cellToCellContentDto(Cell cell);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Cell updateCellFromCellContentDto(CellContentDto cellContentDto, @MappingTarget Cell cell);
}
