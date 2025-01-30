package team.bham.service;

public class TeamAlreadyJoinedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TeamAlreadyJoinedException() {
        super("Team player already exist!");
    }
}
