import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { EmergencyPasswordOtpChangeRoutes } from '@user-management/emergency-password-otp-change/model';
import { selectEmail } from '@user-management/emergency-password-otp-change/selectors';

@Component({
  selector: 'app-email-entry-submitted-container',
  template: `
    <app-email-entry-submitted
      [email]="email$ | async"
      (emergencyPasswordOtpChange)="onEmergencyPasswordOtpChange()"
    ></app-email-entry-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmailEntrySubmittedContainerComponent implements OnInit {
  email$: Observable<string>;

  ngOnInit(): void {
    this.email$ = this.store.select(selectEmail);
  }

  onEmergencyPasswordOtpChange() {
    this.router.navigate([
      EmergencyPasswordOtpChangeRoutes.ROOT,
      EmergencyPasswordOtpChangeRoutes.INIT
    ]);
  }

  constructor(private store: Store, private router: Router) {}
}
