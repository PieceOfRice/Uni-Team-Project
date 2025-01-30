package team.bham.service;

public class TeamClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TeamClosedException() {
        super("Team closed!");
    }
}
