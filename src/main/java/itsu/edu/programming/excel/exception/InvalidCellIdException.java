package itsu.edu.programming.excel.exception;

public class InvalidCellIdException extends RuntimeException {

  public InvalidCellIdException(String cellId) {
    super(String.format("Invalid cell id: %s. It should start with character and end with number, for example 'AB12'",
            cellId)
    );
  }

  public InvalidCellIdException(String message, Throwable cause) {
    super(message, cause);
  }
}
