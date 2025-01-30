package team.bham.service;

public class UserMissingPlayerDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserMissingPlayerDataException() {
        super("User does not have player data!");
    }
}
