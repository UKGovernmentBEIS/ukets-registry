import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';

@Component({
  selector: 'app-ar-update-type',
  templateUrl: './ar-update-type.component.html',
})
export class ArUpdateTypeComponent {
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  displayChangeLink: boolean;
  @Output() readonly clickChange = new EventEmitter();

  onChangeClicked() {
    this.clickChange.emit();
  }
}
