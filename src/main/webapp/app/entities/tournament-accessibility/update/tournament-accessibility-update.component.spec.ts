import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TournamentAccessibilityFormService } from './tournament-accessibility-form.service';
import { TournamentAccessibilityService } from '../service/tournament-accessibility.service';
import { ITournamentAccessibility } from '../tournament-accessibility.model';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';

import { TournamentAccessibilityUpdateComponent } from './tournament-accessibility-update.component';

describe('TournamentAccessibility Management Update Component', () => {
  let comp: TournamentAccessibilityUpdateComponent;
  let fixture: ComponentFixture<TournamentAccessibilityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tournamentAccessibilityFormService: TournamentAccessibilityFormService;
  let tournamentAccessibilityService: TournamentAccessibilityService;
  let tournamentService: TournamentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TournamentAccessibilityUpdateComponent],
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
      .overrideTemplate(TournamentAccessibilityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TournamentAccessibilityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tournamentAccessibilityFormService = TestBed.inject(TournamentAccessibilityFormService);
    tournamentAccessibilityService = TestBed.inject(TournamentAccessibilityService);
    tournamentService = TestBed.inject(TournamentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tournament query and add missing value', () => {
      const tournamentAccessibility: ITournamentAccessibility = { id: 456 };
      const tournament: ITournament = { id: 14370 };
      tournamentAccessibility.tournament = tournament;

      const tournamentCollection: ITournament[] = [{ id: 44055 }];
      jest.spyOn(tournamentService, 'query').mockReturnValue(of(new HttpResponse({ body: tournamentCollection })));
      const additionalTournaments = [tournament];
      const expectedCollection: ITournament[] = [...additionalTournaments, ...tournamentCollection];
      jest.spyOn(tournamentService, 'addTournamentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tournamentAccessibility });
      comp.ngOnInit();

      expect(tournamentService.query).toHaveBeenCalled();
      expect(tournamentService.addTournamentToCollectionIfMissing).toHaveBeenCalledWith(
        tournamentCollection,
        ...additionalTournaments.map(expect.objectContaining)
      );
      expect(comp.tournamentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tournamentAccessibility: ITournamentAccessibility = { id: 456 };
      const tournament: ITournament = { id: 845 };
      tournamentAccessibility.tournament = tournament;

      activatedRoute.data = of({ tournamentAccessibility });
      comp.ngOnInit();

      expect(comp.tournamentsSharedCollection).toContain(tournament);
      expect(comp.tournamentAccessibility).toEqual(tournamentAccessibility);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITournamentAccessibility>>();
      const tournamentAccessibility = { id: 123 };
      jest.spyOn(tournamentAccessibilityFormService, 'getTournamentAccessibility').mockReturnValue(tournamentAccessibility);
      jest.spyOn(tournamentAccessibilityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tournamentAccessibility });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tournamentAccessibility }));
      saveSubject.complete();

      // THEN
      expect(tournamentAccessibilityFormService.getTournamentAccessibility).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tournamentAccessibilityService.update).toHaveBeenCalledWith(expect.objectContaining(tournamentAccessibility));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITournamentAccessibility>>();
      const tournamentAccessibility = { id: 123 };
      jest.spyOn(tournamentAccessibilityFormService, 'getTournamentAccessibility').mockReturnValue({ id: null });
      jest.spyOn(tournamentAccessibilityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tournamentAccessibility: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tournamentAccessibility }));
      saveSubject.complete();

      // THEN
      expect(tournamentAccessibilityFormService.getTournamentAccessibility).toHaveBeenCalled();
      expect(tournamentAccessibilityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITournamentAccessibility>>();
      const tournamentAccessibility = { id: 123 };
      jest.spyOn(tournamentAccessibilityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tournamentAccessibility });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tournamentAccessibilityService.update).toHaveBeenCalled();
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
  });
});
