import { Component, OnInit } from '@angular/core';
import { Notice } from '@kp-administration/itl-notices/model';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { getNotice, selectNotice } from '@kp-administration/store';
import { clearSubMenu } from '@shared/shared.action';

@Component({
  selector: 'app-notice-details-list',
  templateUrl: './notice-details-list.component.html'
})
export class NoticeDetailsListComponent implements OnInit {
  notice$: Observable<Notice[]> = this.store.select(selectNotice);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly store: Store
  ) {}

  ngOnInit(): void {
    this.store.dispatch(clearSubMenu());
    this.store.dispatch(
      getNotice({
        noticeIdentifier: Number(this.route.snapshot.paramMap.get('id'))
      })
    );
  }
}
