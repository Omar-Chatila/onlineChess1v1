package Exceptions;

public class IllegalMoveException extends IllegalStateException {
    public IllegalMoveException(String errorMessage) {
        super(errorMessage);
    }
}
