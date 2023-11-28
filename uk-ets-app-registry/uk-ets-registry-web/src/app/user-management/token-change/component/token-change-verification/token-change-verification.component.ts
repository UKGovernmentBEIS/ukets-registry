import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-token-change-verification',
  templateUrl: './token-change-verification.component.html'
})
export class TokenChangeVerificationComponent {
  @Input() title: string;
  @Input() submittedIdentifier: string;
  @Input() whatNextMessage: string;
  @Input() backToLink: string;
  @Input() backToLabel: string;
}
