import { Component, Input } from '@angular/core';
import { AccountOpeningTaskDetails } from '@task-management/model';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { AccountHolderType } from '@shared/model/account';

@Component({
  selector: 'app-transferring-account-holder-task-details',
  templateUrl: './transferring-account-holder-task-details.component.html',
  styleUrls: ['./transferring-account-holder-task-details.component.css'],
})
export class TransferringAccountHolderTaskDetailsComponent {
  @Input()
  taskDetails: AccountOpeningTaskDetails;
  @Input() installationTransfer: boolean;
  @Input() hasAccountHolderChanged: boolean;
  accountHolderType = AccountHolderType;

  constructor(private store: Store, private router: Router) {}
}
