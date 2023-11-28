import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-guidance-section',
  templateUrl: './guidance-section.component.html',
})
export class GuidanceSectionComponent {
  @Input()
  title: string;
  @Input()
  summary: string;
  @Input()
  link: string;
  @Input()
  from = 'UK Emissions Trading Registry';
  @Input()
  updated = '23 March 2021';
}
