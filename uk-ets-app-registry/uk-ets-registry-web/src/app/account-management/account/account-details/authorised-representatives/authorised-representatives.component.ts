import { Component, Input } from '@angular/core';
import {
  AccountAccessState,
  ArSubmittedUpdateRequest,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AuthRepTableColumns,
  CustomColumn,
} from '@shared/components/account/authorised-representatives';
import { AuthorisedRepresentativeUpdateTypePipe } from '@shared/pipes';
import { Configuration } from '@shared/configuration/configuration.interface';

@Component({
  selector: 'app-authorised-representatives',
  templateUrl: './authorised-representatives.component.html',
})
export class AuthorisedRepresentativesComponent {
  @Input() authorisedReps: AuthorisedRepresentative[];
  @Input() pendingRequests: ArSubmittedUpdateRequest[];
  @Input() accountId: string;
  @Input() canRequestUpdate: boolean;
  @Input() isAdmin: boolean;
  @Input() isReadOnlyAdmin: boolean;
  @Input() addedARs: number;
  @Input() removedARs: number;
  @Input() accountOpeningDate: string;
  @Input() configuration: Configuration[];

  displayedColumns: AuthRepTableColumns[] = [
    AuthRepTableColumns.NAME,
    AuthRepTableColumns.ACCESS_RIGHTS,
    AuthRepTableColumns.WORK_CONTACT,
    AuthRepTableColumns.USER_STATUS,
  ];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private arUpdateTypePipe: AuthorisedRepresentativeUpdateTypePipe
  ) {}

  getAuthorisedRepsByState(state: AccountAccessState) {
    return this.authorisedReps.filter((ar) => ar.state === state);
  }

  getAvailableAuthorisedReps() {
    return this.authorisedReps.filter(
      (ar) =>
        ar.state === 'ACTIVE' ||
        ar.state === 'SUSPENDED' ||
        ar.state === 'REQUESTED'
    );
  }

  generateCustomRequestColumn(): CustomColumn {
    return {
      columnName: 'Request',
      columnValues: this.pendingRequests.map((pr) =>
        this.arUpdateTypePipe.transform(pr.updateType)
      ),
    };
  }

  getPendingArs() {
    return this.pendingRequests.map((pr) => pr.candidate);
  }

  goToRequestUpdate() {
    this.router.navigate([
      this.activatedRoute.snapshot['_routerState'].url +
        '/authorised-representatives',
    ]);
  }
}
