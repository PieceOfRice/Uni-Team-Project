import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OverwatchMapFormService } from './overwatch-map-form.service';
import { OverwatchMapService } from '../service/overwatch-map.service';
import { IOverwatchMap } from '../overwatch-map.model';

import { OverwatchMapUpdateComponent } from './overwatch-map-update.component';

describe('OverwatchMap Management Update Component', () => {
  let comp: OverwatchMapUpdateComponent;
  let fixture: ComponentFixture<OverwatchMapUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let overwatchMapFormService: OverwatchMapFormService;
  let overwatchMapService: OverwatchMapService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OverwatchMapUpdateComponent],
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
      .overrideTemplate(OverwatchMapUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OverwatchMapUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    overwatchMapFormService = TestBed.inject(OverwatchMapFormService);
    overwatchMapService = TestBed.inject(OverwatchMapService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const overwatchMap: IOverwatchMap = { id: 456 };

      activatedRoute.data = of({ overwatchMap });
      comp.ngOnInit();

      expect(comp.overwatchMap).toEqual(overwatchMap);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOverwatchMap>>();
      const overwatchMap = { id: 123 };
      jest.spyOn(overwatchMapFormService, 'getOverwatchMap').mockReturnValue(overwatchMap);
      jest.spyOn(overwatchMapService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ overwatchMap });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: overwatchMap }));
      saveSubject.complete();

      // THEN
      expect(overwatchMapFormService.getOverwatchMap).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(overwatchMapService.update).toHaveBeenCalledWith(expect.objectContaining(overwatchMap));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOverwatchMap>>();
      const overwatchMap = { id: 123 };
      jest.spyOn(overwatchMapFormService, 'getOverwatchMap').mockReturnValue({ id: null });
      jest.spyOn(overwatchMapService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ overwatchMap: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: overwatchMap }));
      saveSubject.complete();

      // THEN
      expect(overwatchMapFormService.getOverwatchMap).toHaveBeenCalled();
      expect(overwatchMapService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOverwatchMap>>();
      const overwatchMap = { id: 123 };
      jest.spyOn(overwatchMapService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ overwatchMap });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(overwatchMapService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
