import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MatchFormService } from './match-form.service';
import { MatchService } from '../service/match.service';
import { IMatch } from '../match.model';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';

import { MatchUpdateComponent } from './match-update.component';

describe('Match Management Update Component', () => {
  let comp: MatchUpdateComponent;
  let fixture: ComponentFixture<MatchUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let matchFormService: MatchFormService;
  let matchService: MatchService;
  let tournamentService: TournamentService;
  let teamService: TeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MatchUpdateComponent],
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
      .overrideTemplate(MatchUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MatchUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    matchFormService = TestBed.inject(MatchFormService);
    matchService = TestBed.inject(MatchService);
    tournamentService = TestBed.inject(TournamentService);
    teamService = TestBed.inject(TeamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tournament query and add missing value', () => {
      const match: IMatch = { id: 456 };
      const tournament: ITournament = { id: 41519 };
      match.tournament = tournament;

      const tournamentCollection: ITournament[] = [{ id: 4727 }];
      jest.spyOn(tournamentService, 'query').mockReturnValue(of(new HttpResponse({ body: tournamentCollection })));
      const additionalTournaments = [tournament];
      const expectedCollection: ITournament[] = [...additionalTournaments, ...tournamentCollection];
      jest.spyOn(tournamentService, 'addTournamentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ match });
      comp.ngOnInit();

      expect(tournamentService.query).toHaveBeenCalled();
      expect(tournamentService.addTournamentToCollectionIfMissing).toHaveBeenCalledWith(
        tournamentCollection,
        ...additionalTournaments.map(expect.objectContaining)
      );
      expect(comp.tournamentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Team query and add missing value', () => {
      const match: IMatch = { id: 456 };
      const teamOne: ITeam = { id: 18651 };
      match.teamOne = teamOne;
      const teamTwo: ITeam = { id: 56131 };
      match.teamTwo = teamTwo;

      const teamCollection: ITeam[] = [{ id: 5252 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeams = [teamOne, teamTwo];
      const expectedCollection: ITeam[] = [...additionalTeams, ...teamCollection];
      jest.spyOn(teamService, 'addTeamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ match });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamToCollectionIfMissing).toHaveBeenCalledWith(
        teamCollection,
        ...additionalTeams.map(expect.objectContaining)
      );
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const match: IMatch = { id: 456 };
      const tournament: ITournament = { id: 39911 };
      match.tournament = tournament;
      const teamOne: ITeam = { id: 49369 };
      match.teamOne = teamOne;
      const teamTwo: ITeam = { id: 36215 };
      match.teamTwo = teamTwo;

      activatedRoute.data = of({ match });
      comp.ngOnInit();

      expect(comp.tournamentsSharedCollection).toContain(tournament);
      expect(comp.teamsSharedCollection).toContain(teamOne);
      expect(comp.teamsSharedCollection).toContain(teamTwo);
      expect(comp.match).toEqual(match);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMatch>>();
      const match = { id: 123 };
      jest.spyOn(matchFormService, 'getMatch').mockReturnValue(match);
      jest.spyOn(matchService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ match });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: match }));
      saveSubject.complete();

      // THEN
      expect(matchFormService.getMatch).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(matchService.update).toHaveBeenCalledWith(expect.objectContaining(match));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMatch>>();
      const match = { id: 123 };
      jest.spyOn(matchFormService, 'getMatch').mockReturnValue({ id: null });
      jest.spyOn(matchService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ match: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: match }));
      saveSubject.complete();

      // THEN
      expect(matchFormService.getMatch).toHaveBeenCalled();
      expect(matchService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMatch>>();
      const match = { id: 123 };
      jest.spyOn(matchService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ match });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(matchService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTournament', () => {
      it('Should forward to tournamentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tournamentService, 'compareTournament');
        comp.compareTournament(entity, entity2);
        expect(tournamentService.compareTournament).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTeam', () => {
      it('Should forward to teamService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamService, 'compareTeam');
        comp.compareTeam(entity, entity2);
        expect(teamService.compareTeam).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
