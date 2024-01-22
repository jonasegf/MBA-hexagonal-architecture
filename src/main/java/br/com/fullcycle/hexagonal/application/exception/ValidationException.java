package br.com.fullcycle.hexagonal.application.exception;

public class ValidationException extends RuntimeException {
  public ValidationException(final String message, final Throwable cause) {
    super(message, cause, true, false);
  }

  public ValidationException(final String message) {
    super(message, null, true, false);
  }
}
