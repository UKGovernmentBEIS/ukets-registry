import { Component, EventEmitter, Output } from '@angular/core';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-registry-administration',
  templateUrl: './registry-administration.component.html',
})
export class RegistryAdministrationComponent {
  @Output() readonly setUserAsAuthority = new EventEmitter();

  onSetUserAsAuthority() {
    this.setUserAsAuthority.emit();
  }
}
