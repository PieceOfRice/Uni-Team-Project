package team.bham.service;

public class TeamDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TeamDoesNotExistException() {
        super("Team does not exist!");
    }
}
