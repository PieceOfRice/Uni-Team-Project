import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParticipantFormService } from './participant-form.service';
import { ParticipantService } from '../service/participant.service';
import { IParticipant } from '../participant.model';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';

import { ParticipantUpdateComponent } from './participant-update.component';

describe('Participant Management Update Component', () => {
  let comp: ParticipantUpdateComponent;
  let fixture: ComponentFixture<ParticipantUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let participantFormService: ParticipantFormService;
  let participantService: ParticipantService;
  let teamService: TeamService;
  let tournamentService: TournamentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParticipantUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ParticipantUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParticipantUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    participantFormService = TestBed.inject(ParticipantFormService);
    participantService = TestBed.inject(ParticipantService);
    teamService = TestBed.inject(TeamService);
    tournamentService = TestBed.inject(TournamentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Team query and add missing value', () => {
      const participant: IParticipant = { id: 456 };
      const team: ITeam = { id: 9472 };
      participant.team = team;

      const teamCollection: ITeam[] = [{ id: 8559 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeams = [team];
      const expectedCollection: ITeam[] = [...additionalTeams, ...teamCollection];
      jest.spyOn(teamService, 'addTeamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ participant });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamToCollectionIfMissing).toHaveBeenCalledWith(
        teamCollection,
        ...additionalTeams.map(expect.objectContaining)
      );
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Tournament query and add missing value', () => {
      const participant: IParticipant = { id: 456 };
      const tournament: ITournament = { id: 1209 };
      participant.tournament = tournament;

      const tournamentCollection: ITournament[] = [{ id: 53001 }];
      jest.spyOn(tournamentService, 'query').mockReturnValue(of(new HttpResponse({ body: tournamentCollection })));
      const additionalTournaments = [tournament];
      const expectedCollection: ITournament[] = [...additionalTournaments, ...tournamentCollection];
      jest.spyOn(tournamentService, 'addTournamentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ participant });
      comp.ngOnInit();

      expect(tournamentService.query).toHaveBeenCalled();
      expect(tournamentService.addTournamentToCollectionIfMissing).toHaveBeenCalledWith(
        tournamentCollection,
        ...additionalTournaments.map(expect.objectContaining)
      );
      expect(comp.tournamentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const participant: IParticipant = { id: 456 };
      const team: ITeam = { id: 16161 };
      participant.team = team;
      const tournament: ITournament = { id: 61061 };
      participant.tournament = tournament;

      activatedRoute.data = of({ participant });
      comp.ngOnInit();

      expect(comp.teamsSharedCollection).toContain(team);
      expect(comp.tournamentsSharedCollection).toContain(tournament);
      expect(comp.participant).toEqual(participant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParticipant>>();
      const participant = { id: 123 };
      jest.spyOn(participantFormService, 'getParticipant').mockReturnValue(participant);
      jest.spyOn(participantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ participant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: participant }));
      saveSubject.complete();

      // THEN
      expect(participantFormService.getParticipant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(participantService.update).toHaveBeenCalledWith(expect.objectContaining(participant));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParticipant>>();
      const participant = { id: 123 };
      jest.spyOn(participantFormService, 'getParticipant').mockReturnValue({ id: null });
      jest.spyOn(participantService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ participant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: participant }));
      saveSubject.complete();

      // THEN
      expect(participantFormService.getParticipant).toHaveBeenCalled();
      expect(participantService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParticipant>>();
      const participant = { id: 123 };
      jest.spyOn(participantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ participant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(participantService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeam', () => {
      it('Should forward to teamService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamService, 'compareTeam');
        comp.compareTeam(entity, entity2);
        expect(teamService.compareTeam).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTournament', () => {
      it('Should forward to tournamentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tournamentService, 'compareTournament');
        comp.compareTournament(entity, entity2);
        expect(tournamentService.compareTournament).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
