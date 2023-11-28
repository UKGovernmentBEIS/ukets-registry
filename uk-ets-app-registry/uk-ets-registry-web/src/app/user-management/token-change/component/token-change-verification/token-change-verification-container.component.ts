import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { selectSubmittedRequestIdentifier } from '@user-management/token-change/reducer/token-change.selectors';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-token-change-verification-container',
  template: `
    <app-token-change-verification
      [title]="'Change 2FA request successfully submitted.'"
      [whatNextMessage]="
        'Your request will be reviewed by the Registry Administrator.If your request is approved you will be able to activate a new authenticator app.'
      "
      [backToLabel]="'Go to sign in'"
      [backToLink]="'/dashboard'"
      [submittedIdentifier]="submittedIdentifier"
    ></app-token-change-verification>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TokenChangeVerificationContainerComponent implements OnInit {
  submittedIdentifier: string;
  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.submittedIdentifier = this.route.snapshot.paramMap.get(
      'submittedRequestIdentifier'
    );
  }
}
