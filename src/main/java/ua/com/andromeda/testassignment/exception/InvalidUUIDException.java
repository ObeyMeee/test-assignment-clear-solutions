package ua.com.andromeda.testassignment.exception;

public class InvalidUUIDException extends RuntimeException {
    public InvalidUUIDException() {
        super("Invalid UUID");
    }
}
