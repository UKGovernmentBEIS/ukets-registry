import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { selectTaskResponse } from '@user-management/emergency-otp-change/selectors/emergency-otp-change.selectors';
import { Observable } from 'rxjs';
import { createEmergencyOtpChangeTask } from '@user-management/emergency-otp-change/actions/emergency-otp-change.actions';
import { EmergencyOtpChangeTaskResponse } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';
import { filter, take } from 'rxjs/operators';

@Component({
  selector: 'app-confirm-emergency-otp-change-container',
  template: `
    <app-confirm-emergency-otp-change
      *ngIf="taskResponse$ | async"
      [taskResponse]="taskResponse$ | async"
    ></app-confirm-emergency-otp-change>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmEmergencyOtpChangeContainerComponent implements OnInit {
  token: string;
  taskResponse$: Observable<EmergencyOtpChangeTaskResponse>;
  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    this.store.dispatch(createEmergencyOtpChangeTask({ token: this.token }));
    this.taskResponse$ = this.store.select(selectTaskResponse);

    this.taskResponse$
      .pipe(
        filter((response) => !!response),
        take(1)
      )
      .subscribe((response) => {
        if (!response.tokenExpired) {
          this.router.navigate(['/dashboard']);
        }
      });
  }
  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private router: Router
  ) {}
}
