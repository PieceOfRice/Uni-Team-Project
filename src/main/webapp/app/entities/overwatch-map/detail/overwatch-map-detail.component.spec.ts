import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OverwatchMapDetailComponent } from './overwatch-map-detail.component';

describe('OverwatchMap Management Detail Component', () => {
  let comp: OverwatchMapDetailComponent;
  let fixture: ComponentFixture<OverwatchMapDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OverwatchMapDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ overwatchMap: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OverwatchMapDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OverwatchMapDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load overwatchMap on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.overwatchMap).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
