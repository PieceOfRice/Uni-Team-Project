package team.bham.service;

public class ParticipantDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ParticipantDoesNotExistException() {
        super("Participant does not exist!");
    }
}
