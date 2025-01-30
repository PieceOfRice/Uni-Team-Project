import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TeamPlayerFormService } from './team-player-form.service';
import { TeamPlayerService } from '../service/team-player.service';
import { ITeamPlayer } from '../team-player.model';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';

import { TeamPlayerUpdateComponent } from './team-player-update.component';

describe('TeamPlayer Management Update Component', () => {
  let comp: TeamPlayerUpdateComponent;
  let fixture: ComponentFixture<TeamPlayerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamPlayerFormService: TeamPlayerFormService;
  let teamPlayerService: TeamPlayerService;
  let playerDataService: PlayerDataService;
  let teamService: TeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TeamPlayerUpdateComponent],
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
      .overrideTemplate(TeamPlayerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamPlayerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamPlayerFormService = TestBed.inject(TeamPlayerFormService);
    teamPlayerService = TestBed.inject(TeamPlayerService);
    playerDataService = TestBed.inject(PlayerDataService);
    teamService = TestBed.inject(TeamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call PlayerData query and add missing value', () => {
      const teamPlayer: ITeamPlayer = { id: 456 };
      const player: IPlayerData = { id: 30629 };
      teamPlayer.player = player;

      const playerDataCollection: IPlayerData[] = [{ id: 78390 }];
      jest.spyOn(playerDataService, 'query').mockReturnValue(of(new HttpResponse({ body: playerDataCollection })));
      const additionalPlayerData = [player];
      const expectedCollection: IPlayerData[] = [...additionalPlayerData, ...playerDataCollection];
      jest.spyOn(playerDataService, 'addPlayerDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamPlayer });
      comp.ngOnInit();

      expect(playerDataService.query).toHaveBeenCalled();
      expect(playerDataService.addPlayerDataToCollectionIfMissing).toHaveBeenCalledWith(
        playerDataCollection,
        ...additionalPlayerData.map(expect.objectContaining)
      );
      expect(comp.playerDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Team query and add missing value', () => {
      const teamPlayer: ITeamPlayer = { id: 456 };
      const team: ITeam = { id: 82137 };
      teamPlayer.team = team;

      const teamCollection: ITeam[] = [{ id: 68048 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeams = [team];
      const expectedCollection: ITeam[] = [...additionalTeams, ...teamCollection];
      jest.spyOn(teamService, 'addTeamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamPlayer });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamToCollectionIfMissing).toHaveBeenCalledWith(
        teamCollection,
        ...additionalTeams.map(expect.objectContaining)
      );
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamPlayer: ITeamPlayer = { id: 456 };
      const player: IPlayerData = { id: 76796 };
      teamPlayer.player = player;
      const team: ITeam = { id: 71220 };
      teamPlayer.team = team;

      activatedRoute.data = of({ teamPlayer });
      comp.ngOnInit();

      expect(comp.playerDataSharedCollection).toContain(player);
      expect(comp.teamsSharedCollection).toContain(team);
      expect(comp.teamPlayer).toEqual(teamPlayer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamPlayer>>();
      const teamPlayer = { id: 123 };
      jest.spyOn(teamPlayerFormService, 'getTeamPlayer').mockReturnValue(teamPlayer);
      jest.spyOn(teamPlayerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamPlayer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamPlayer }));
      saveSubject.complete();

      // THEN
      expect(teamPlayerFormService.getTeamPlayer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamPlayerService.update).toHaveBeenCalledWith(expect.objectContaining(teamPlayer));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamPlayer>>();
      const teamPlayer = { id: 123 };
      jest.spyOn(teamPlayerFormService, 'getTeamPlayer').mockReturnValue({ id: null });
      jest.spyOn(teamPlayerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamPlayer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamPlayer }));
      saveSubject.complete();

      // THEN
      expect(teamPlayerFormService.getTeamPlayer).toHaveBeenCalled();
      expect(teamPlayerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamPlayer>>();
      const teamPlayer = { id: 123 };
      jest.spyOn(teamPlayerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamPlayer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamPlayerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlayerData', () => {
      it('Should forward to playerDataService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerDataService, 'comparePlayerData');
        comp.comparePlayerData(entity, entity2);
        expect(playerDataService.comparePlayerData).toHaveBeenCalledWith(entity, entity2);
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
