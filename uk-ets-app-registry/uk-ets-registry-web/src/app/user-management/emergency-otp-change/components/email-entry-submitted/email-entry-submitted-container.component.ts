import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { EmergencyOtpChangeRoutes } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';
import { selectEmail } from '@user-management/emergency-otp-change/selectors/emergency-otp-change.selectors';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-email-entry-submitted-container',
  template: `
    <app-email-entry-submitted
      [email]="email$ | async"
      (emergencyOtpChange)="onEmergencyOtpChange()"
    ></app-email-entry-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmailEntrySubmittedContainerComponent implements OnInit {
  email$: Observable<string>;

  ngOnInit(): void {
    this.email$ = this.store.select(selectEmail);
  }

  onEmergencyOtpChange() {
    this.router.navigate([
      EmergencyOtpChangeRoutes.ROOT,
      EmergencyOtpChangeRoutes.INIT
    ]);
  }

  constructor(private store: Store, private router: Router) {}
}
