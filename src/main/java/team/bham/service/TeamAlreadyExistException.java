package team.bham.service;

public class TeamAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TeamAlreadyExistException() {
        super("Team name already used!");
    }
}
