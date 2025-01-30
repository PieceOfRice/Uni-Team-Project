import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlayerDataFormService } from './player-data-form.service';
import { PlayerDataService } from '../service/player-data.service';
import { IPlayerData } from '../player-data.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { PlayerDataUpdateComponent } from './player-data-update.component';

describe('PlayerData Management Update Component', () => {
  let comp: PlayerDataUpdateComponent;
  let fixture: ComponentFixture<PlayerDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerDataFormService: PlayerDataFormService;
  let playerDataService: PlayerDataService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlayerDataUpdateComponent],
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
      .overrideTemplate(PlayerDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerDataFormService = TestBed.inject(PlayerDataFormService);
    playerDataService = TestBed.inject(PlayerDataService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const playerData: IPlayerData = { id: 456 };
      const user: IUser = { id: 29208 };
      playerData.user = user;

      const userCollection: IUser[] = [{ id: 39336 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playerData });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const playerData: IPlayerData = { id: 456 };
      const user: IUser = { id: 22951 };
      playerData.user = user;

      activatedRoute.data = of({ playerData });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.playerData).toEqual(playerData);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerData>>();
      const playerData = { id: 123 };
      jest.spyOn(playerDataFormService, 'getPlayerData').mockReturnValue(playerData);
      jest.spyOn(playerDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerData }));
      saveSubject.complete();

      // THEN
      expect(playerDataFormService.getPlayerData).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerDataService.update).toHaveBeenCalledWith(expect.objectContaining(playerData));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerData>>();
      const playerData = { id: 123 };
      jest.spyOn(playerDataFormService, 'getPlayerData').mockReturnValue({ id: null });
      jest.spyOn(playerDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerData: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerData }));
      saveSubject.complete();

      // THEN
      expect(playerDataFormService.getPlayerData).toHaveBeenCalled();
      expect(playerDataService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerData>>();
      const playerData = { id: 123 };
      jest.spyOn(playerDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerDataService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
