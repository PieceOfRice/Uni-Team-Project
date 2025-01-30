import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OverwatchMapService } from '../service/overwatch-map.service';

import { OverwatchMapComponent } from './overwatch-map.component';

describe('OverwatchMap Management Component', () => {
  let comp: OverwatchMapComponent;
  let fixture: ComponentFixture<OverwatchMapComponent>;
  let service: OverwatchMapService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'overwatch-map', component: OverwatchMapComponent }]), HttpClientTestingModule],
      declarations: [OverwatchMapComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(OverwatchMapComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OverwatchMapComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OverwatchMapService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.overwatchMaps?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to overwatchMapService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getOverwatchMapIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getOverwatchMapIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
