package team.bham.service;

public class TournamentClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TournamentClosedException() {
        super("Tournament is closed!");
    }
}
