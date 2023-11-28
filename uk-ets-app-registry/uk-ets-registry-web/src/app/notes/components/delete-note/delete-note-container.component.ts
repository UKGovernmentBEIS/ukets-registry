import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { MenuItemEnum } from '../../../account-management/account/account-details/model';
import * as AccountNotesActions from '@registry-web/notes/store/notes.actions';

@Component({
  selector: 'app-delete-note-container',
  template: ` <app-delete-note (handleDelete)="onDelete()"></app-delete-note>`,
})
export class DeleteNoteContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
        extras: {
          skipLocationChange: true,
          queryParams: { selectedSideMenu: MenuItemEnum.NOTES },
        },
      })
    );
  }

  onDelete() {
    this.store.dispatch(AccountNotesActions.deleteNote());
  }
}
