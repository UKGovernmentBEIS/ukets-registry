import { Component, Inject, OnInit } from '@angular/core';
import { BulkActionForm } from '../util/bulk-action';
import { ActivatedRoute, Data } from '@angular/router';
import { Store } from '@ngrx/store';
import { assignTasks } from '../task-list.actions';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { HttpParams } from '@angular/common/http';
import { UntypedFormBuilder } from '@angular/forms';
import { errors } from '@shared/shared.action';

interface UserDTO {
  fullName: string;
  knownAs: string;
  displayName: string;
  urid: string;
}

@Component({
  selector: 'app-bulk-assign',
  templateUrl: './bulk-assign.component.html',
})
export class BulkAssignComponent extends BulkActionForm implements OnInit {
  selectedUrid: string;
  searchByNameRequestUrl =
    this.ukEtsRegistryApiBaseUrl + '/tasks.get.candidate-assignees';
  requestParams: HttpParams = new HttpParams();

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    protected route: ActivatedRoute,
    private store: Store,
    private formBuilder: UntypedFormBuilder
  ) {
    super(route);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.requestParams = this.requestParams.set(
      'requestIds',
      this.requestIds.join(',')
    );
    this.requestParams = this.requestParams.set(
      'accountIdentifiers',
      this.accountIdentifiers.join(',')
    );
    if (this.taskTypes) {
      this.requestParams = this.requestParams.set(
        'taskTypes',
        this.taskTypes.join(',')
      );
    }

    this.form = this.formBuilder.group({
      comment: [],
    });
  }

  doSubmit() {
    const comment: string = this.form.value['comment'];
    const urid: string = this.selectedUrid;
    this.store.dispatch(
      assignTasks({
        requestIds: this.requestIds,
        comment,
        urid,
        potentialErrors: this.potentialErrors,
      })
    );
  }

  validate(): boolean {
    return this.isValidUser();
  }

  initData(data: Data) {
    super.initData(data);
  }

  userResultFormatter(item: UserDTO): string {
    return item.displayName;
  }

  onSelectFromSearch($event: UserDTO) {
    this.selectedUrid = $event.urid;
  }

  isValidUser(): boolean {
    if (this.selectedUrid) {
      return true;
    } else {
      this.store.dispatch(
        errors({
          errorSummary: {
            errors: [
              {
                componentId: 'user-label',
                errorMessage: 'Please select a user',
              },
            ],
          },
        })
      );
      return false;
    }
  }
}
