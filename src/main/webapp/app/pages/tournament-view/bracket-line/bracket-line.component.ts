import { Component, Input, OnInit } from '@angular/core';
import { LineDescription } from '../tournament-view.component';

@Component({
  selector: 'jhi-bracket-line',
  templateUrl: './bracket-line.component.html',
  styleUrls: ['./bracket-line.component.scss'],
})
export class BracketLineComponent implements OnInit {
  @Input() data: LineDescription = {
    to: {
      x: 10,
      y: 10,
    },
    from: {
      x: 0,
      y: 0,
    },
  };
  svgProperties = {
    x1: '0%',
    y1: '0%',
    x2: '100%',
    y2: '100%',
  };

  constructor() {}

  ngOnInit(): void {
    this.svgProperties = {
      x1: `${this.data.from.x * 100}%`,
      y1: `${this.data.from.y * 100}%`,
      x2: `${this.data.to.x * 100}%`,
      y2: `${this.data.to.y * 100}%`,
    };
  }
}
