import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-shared-three-line-address',
  templateUrl: './three-line-address.component.html'
})
export class ThreeLineAddressComponent {
  @Input()
  line1: string;
  @Input()
  line2: string;
  @Input()
  line3: string;
}
