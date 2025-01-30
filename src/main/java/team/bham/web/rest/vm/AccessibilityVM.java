package team.bham.web.rest.vm;

import team.bham.domain.Tournament;
import team.bham.domain.enumeration.VenueAccessibilities;

public class AccessibilityVM {

    private Long id;
    private VenueAccessibilities accessibility;
    private Tournament tournament;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VenueAccessibilities getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(VenueAccessibilities accessibility) {
        this.accessibility = accessibility;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
}
