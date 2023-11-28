import { Component, OnInit } from '@angular/core';
import { Notice } from '@kp-administration/itl-notices/model';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { getNotice, selectNotice } from '@kp-administration/store';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-notification-details-container',
  template: `
    <app-feature-header-wrapper>
      <app-notice-details-header
        [notice]="notice$ | async"
      ></app-notice-details-header>
    </app-feature-header-wrapper>
    <router-outlet></router-outlet>
  `
})
export class NoticeDetailsContainerComponent implements OnInit {
  notice$: Observable<Notice> = this.store.select(selectNotice).pipe(
    filter(notice => notice !== undefined),
    map(notice => notice[0])
  );

  constructor(
    private readonly store: Store,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(
      getNotice({
        noticeIdentifier: Number(this.route.snapshot.paramMap.get('id'))
      })
    );
  }
}
