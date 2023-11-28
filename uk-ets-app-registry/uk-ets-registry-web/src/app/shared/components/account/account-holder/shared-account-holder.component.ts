import { Component, Input, OnInit } from '@angular/core';
import {
  AccountHolder,
  AccountHolderType
} from '../../../model/account/account-holder';
import { IUkOfficialCountry } from '../../../countries/country.interface';
import * as SharedUtil from '../../../shared.util';

@Component({
  selector: 'app-shared-account-holder',
  templateUrl: './shared-account-holder.component.html'
})
export class SharedAccountHolderComponent {
  @Input()
  accountHolder: AccountHolder;
  @Input()
  countries: IUkOfficialCountry[];

  accountHolderTypes = AccountHolderType;
  sharedUtil = SharedUtil;
}
