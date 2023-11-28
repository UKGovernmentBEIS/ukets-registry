import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  AccountHolder,
  AccountHolderContactInfo,
  Account,
} from '@shared/model/account';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { FileDetails } from '@shared/model/file/file-details.model';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';

@Component({
  selector: 'app-account-holder',
  templateUrl: './account-holder.component.html',
})
export class AccountHolderComponent {
  @Input()
  account: Account;
  @Input()
  accountHolder: AccountHolder;
  @Input()
  accountHolderContactInfo: AccountHolderContactInfo;
  @Input()
  countries: IUkOfficialCountry[];
  @Input()
  documents: FileDetails[];
  @Input()
  canRequestUpdateContact: boolean;
  @Input()
  canRequestUpdateDocuments: boolean;
  @Input()
  canDeleteFile: boolean;
  @Output()
  readonly downloadAccountHolderFileEmitter = new EventEmitter<FileDetails>();
  @Output()
  readonly removeAccountHolderFileEmitter = new EventEmitter();
  @Output() readonly requestDocuments = new EventEmitter();

  primaryContactType = ContactType.PRIMARY;
  alternativeContactType = ContactType.ALTERNATIVE;

  constructor(private activatedRoute: ActivatedRoute, private router: Router) {}

  isKpNonGovernmentAccount() {
    return (
      !this.account.accountType.startsWith('UK_') &&
      !this.account.governmentAccount
    );
  }

  onAccountHolderRequestDocuments() {
    this.requestDocuments.emit();
  }

  downloadFile(file: FileDetails) {
    this.downloadAccountHolderFileEmitter.emit(file);
  }

  deleteFile(file: FileDetails) {
    this.removeAccountHolderFileEmitter.emit(file);

    this.removeAccountHolderFileEmitter.emit({
      file,
      id: this.accountHolder.id,
    });
  }

  goToRequestUpdate() {
    this.router.navigate([
      this.activatedRoute.snapshot['_routerState'].url +
        `/${AccountHolderDetailsWizardPathsModel.BASE_PATH}`,
    ]);
  }
}
