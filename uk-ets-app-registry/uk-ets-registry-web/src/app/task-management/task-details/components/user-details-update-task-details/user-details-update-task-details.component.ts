import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';
import { selectAllCountries } from '@registry-web/shared/shared.selector';
import { UserDetailsUpdateTaskDetails } from '@registry-web/task-management/model';
import { Observable } from 'rxjs';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { userStatusMap } from '@shared/user';

@Component({
  selector: 'app-user-details-update-task-details',
  templateUrl: './user-details-update-task-details.component.html',
})
export class UserDetailsUpdateTaskDetailsComponent implements OnInit {
  @Input()
  userDetailsUpdateTaskDetails: UserDetailsUpdateTaskDetails;
  @Output() readonly requestDocumentEmitter = new EventEmitter();
  countries$: Observable<IUkOfficialCountry[]>;

  userStatusMap = userStatusMap;

  isCompleted: boolean;

  constructor(private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.countries$ = this.store.select(selectAllCountries);
    this.isCompleted =
      this.userDetailsUpdateTaskDetails.taskStatus === 'COMPLETED';
  }

  onUserRequestDocuments() {
    this.requestDocumentEmitter.emit({
      originatingPath: this.router.url,
      parentRequestId: this.userDetailsUpdateTaskDetails.requestId,
      documentsRequestType: DocumentsRequestType.USER,
      recipientName:
        this.userDetailsUpdateTaskDetails.current.alsoKnownAs &&
        this.userDetailsUpdateTaskDetails.current.alsoKnownAs.length > 0
          ? this.userDetailsUpdateTaskDetails.current.alsoKnownAs
          : this.userDetailsUpdateTaskDetails.current.firstName +
            ' ' +
            this.userDetailsUpdateTaskDetails.current.lastName,
      recipientUrid: this.userDetailsUpdateTaskDetails.current.urid,
    });
  }

  getSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'User Details',
          class: 'summary-list-change-header-font-weight govuk-body-l',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
        action: {
          label: '',
          visible: true,
          visuallyHidden: '',
          url: '',
        },
      },
      {
        key: { label: 'User ID' },
        value: [
          {
            label: this.userDetailsUpdateTaskDetails.current.urid,
            url:
              '/user-details/' + this.userDetailsUpdateTaskDetails.current.urid,
          },
        ],
        action: {
          label: '',
          visible: true,
          visuallyHidden: '',
          url: '',
        },
      },
      {
        key: { label: 'Status' },
        value: [
          {
            label: this.userDetailsUpdateTaskDetails.current.status,
            class:
              'govuk-tag govuk-tag--' +
              this.userStatusMap[
                this.userDetailsUpdateTaskDetails.userDetails[0].attributes
                  .state[0]
              ].color,
          },
        ],
        action: {
          label: '',
          visible: true,
          visuallyHidden: '',
          url: '',
        },
      },
    ];
  }
}
