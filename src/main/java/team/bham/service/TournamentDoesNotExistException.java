package team.bham.service;

public class TournamentDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TournamentDoesNotExistException() {
        super("Tournament does not exist!");
    }
}
