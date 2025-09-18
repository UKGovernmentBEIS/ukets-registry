import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateSuccessComponent } from './document-update-success.component';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { selectUpdateType } from '../../store/documents-wizard.selector';

@Component({
  standalone: true,
  selector: 'app-document-update-success-container',
  imports: [DocumentUpdateSuccessComponent, CommonModule],
  template: `<app-document-update-success
    [storedUpdateType]="updateType$ | async"
  ></app-document-update-success>`,
})
export class DocumentUpdateSuccessContainerComponent implements OnInit {
  constructor(private store: Store) {}
  updateType$: Observable<DocumentUpdateType>;

  ngOnInit(): void {
    this.updateType$ = this.store.select(selectUpdateType);
    this.store.dispatch(clearGoBackRoute());
  }
}
