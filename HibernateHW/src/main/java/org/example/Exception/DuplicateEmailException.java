package org.example.Exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String email, int existingUserId) {
        super("Пользователь с email '" + email + "' уже существует (ID: " + existingUserId + ")");
    }
}
