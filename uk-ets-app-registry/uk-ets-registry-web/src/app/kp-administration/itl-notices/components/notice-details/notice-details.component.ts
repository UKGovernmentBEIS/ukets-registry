import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { noticeTypeMap, Notice } from '@kp-administration/itl-notices/model';
import { selectNoticeByIndex } from '@kp-administration/store';
import { clearSubMenu } from '@shared/shared.action';

@Component({
  selector: 'app-notice-details',
  templateUrl: './notice-details.component.html'
})
export class NoticeDetailsComponent implements OnInit {
  notice$: Observable<Notice> = this.store.select(selectNoticeByIndex, {
    index: this.route.snapshot.paramMap.get('typeId')
  });

  noticeTypeMap = noticeTypeMap;

  constructor(
    private readonly store: Store,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(clearSubMenu());
  }
}
