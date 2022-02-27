package util;

@FunctionalInterface
public interface Logging {
    void logUserLoginAttempt(String username, boolean success);
}
