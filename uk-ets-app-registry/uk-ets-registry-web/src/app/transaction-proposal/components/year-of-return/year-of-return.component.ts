import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-year-of-return',
  templateUrl: './year-of-return.component.html',
})
export class YearOfReturnComponent {
  @Input() allocationYear: number;
  @Input() label: string;
}
