import { Component, Input, OnInit } from '@angular/core';
import { ArInAccount } from '@user-management/user-details/model';
import { accountAccessStateMap } from '@shared/model/account';

@Component({
  selector: 'app-ar-in-accounts',
  templateUrl: './ar-in-accounts.component.html',
  styleUrls: ['./ar-in-accounts.component.scss'],
})
export class ArInAccountsComponent {
  @Input() arInAccount: ArInAccount[];
  accountAccessStateMap = accountAccessStateMap;
}
