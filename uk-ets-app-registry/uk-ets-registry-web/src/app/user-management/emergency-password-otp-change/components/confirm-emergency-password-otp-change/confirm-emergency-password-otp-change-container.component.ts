import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { EmergencyPasswordOtpChangeTaskResponse } from '@user-management/emergency-password-otp-change/model';
import { createEmergencyPasswordOtpChangeTask } from '@user-management/emergency-password-otp-change/actions';
import { selectTaskResponse } from '@user-management/emergency-password-otp-change/selectors';

@Component({
  selector: 'app-confirm-emergency-password-otp-change-container',
  template: `
    <app-confirm-emergency-password-otp-change
      *ngIf="taskResponse$ | async"
      [taskResponse]="taskResponse$ | async"
    ></app-confirm-emergency-password-otp-change>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfirmEmergencyPasswordOtpChangeContainerComponent
  implements OnInit {
  token: string;
  taskResponse$: Observable<EmergencyPasswordOtpChangeTaskResponse>;
  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    this.store.dispatch(
      createEmergencyPasswordOtpChangeTask({ token: this.token })
    );
    this.taskResponse$ = this.store.select(selectTaskResponse);
  }
  constructor(private store: Store, private route: ActivatedRoute) {}
}
