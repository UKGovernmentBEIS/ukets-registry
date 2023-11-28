import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { UpdateAllocationStatusRequest } from '@allocation-status/model';
import { selectUpdateRequest } from '@allocation-status/reducers/allocation-status.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { updateAllocationStatus } from '@allocation-status/actions/allocation-status.actions';

@Component({
  selector: 'app-check-update-request-container',
  template: `
    <app-check-update-request
      [updateRequest]="updateAllocationStatusRequest$ | async"
      (changeUpdate)="onChangeUpdate()"
      (applyButtonClicked)="update()"
    >
    </app-check-update-request>
  `
})
export class CheckUpdateRequestContainerComponent implements OnInit {
  updateAllocationStatusRequest$: Observable<UpdateAllocationStatusRequest>;
  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  onChangeUpdate() {
    this.router.navigate([
      `/account/${this.route.parent.snapshot.params.accountId}/allocation-status`
    ]);
  }

  update() {
    this.store.dispatch(updateAllocationStatus());
  }

  ngOnInit(): void {
    this.updateAllocationStatusRequest$ = this.store.select(
      selectUpdateRequest
    );
  }
}
