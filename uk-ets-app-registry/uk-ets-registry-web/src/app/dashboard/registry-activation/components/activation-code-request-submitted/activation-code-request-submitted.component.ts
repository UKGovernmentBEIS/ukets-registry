import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-activation-code-request-submitted',
  templateUrl: './activation-code-request-submitted.component.html',
})
export class ActivationCodeRequestSubmittedComponent {
  @Input()
  submittedIdentifier: string;
}
