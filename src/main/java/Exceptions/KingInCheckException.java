package Exceptions;

public class KingInCheckException extends IllegalStateException {
    public KingInCheckException(String errorMessage) {
        super(errorMessage);
    }
}
