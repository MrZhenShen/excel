package itsu.edu.programming.excel.aop;

import itsu.edu.programming.excel.service.ExcelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class ExcelAspect {

  private ExcelService excelService;

  @After("execution(* itsu.edu.programming.excel.service.apache.ApacheCellService.clear*(..)) " +
          "|| execution(* itsu.edu.programming.excel.service.apache.ApacheCellService.set*(..))")
  public void afterCellChanges() {
    excelService.write();

    log.info("Excel was written");
  }
}
