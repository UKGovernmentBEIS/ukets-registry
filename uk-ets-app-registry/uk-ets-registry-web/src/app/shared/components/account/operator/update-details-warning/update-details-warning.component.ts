import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { UpdateWarningPageComponent } from '@notes/components/update-warning-page/update-warning-page.component';
import { CommonModule } from '@angular/common';
import { fetchCurrentOperatorInfo } from '@operator-update/actions/operator-update.actions';
import { ViewMode } from '@user-management/user-details/model';

@Component({
  standalone: true,
  imports: [UpdateWarningPageComponent, CommonModule],
  selector: 'app-update-details-warning',
  template: ` <app-update-warning-page
    [updateType]="updateType"
  ></app-update-warning-page>`,
})
export class UpdateDetailsWarningComponent implements OnInit {
  updateType: 'UPDATE_HOLDER' | 'UPDATE_OPERATOR';
  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
        extras: {
          skipLocationChange: true,
        },
      })
    );
    this.updateType = this.route.snapshot.url
      .map((s) => s.path)
      .includes('confirm-update-operator')
      ? 'UPDATE_OPERATOR'
      : 'UPDATE_HOLDER';
    this.store.dispatch(
      fetchCurrentOperatorInfo({
        accountId: this.route.snapshot.paramMap.get('accountId'),
      })
    );
  }
}
