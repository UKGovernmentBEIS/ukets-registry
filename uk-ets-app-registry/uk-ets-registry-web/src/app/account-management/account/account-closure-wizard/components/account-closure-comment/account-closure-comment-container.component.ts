import { ChangeDetectionStrategy, Component, OnInit } from "@angular/core";
import { Store } from "@ngrx/store";
import { ActivatedRoute } from "@angular/router";
import { canGoBack, clearErrors, errors } from "@shared/shared.action";
import { ErrorDetail, ErrorSummary } from "@shared/error-summary";
import { Observable } from "rxjs";
import { selectClosureComment } from "@account-management/account/account-closure-wizard/reducers";
import {
  cancelClicked,
  setClosureComment
} from "@account-management/account/account-closure-wizard/actions/account-closure.action";

@Component({
  selector: 'app-account-closure-comment-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Explain why you are closing this account'"
    ></div>
    <app-account-closure-comment
      [comment]="comment$ | async"
      (justificationComment)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-account-closure-comment>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountClosureCommentContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  comment$: Observable<string>;

  ngOnInit(): void {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.comment$ = this.store.select(selectClosureComment);
  }

  onContinue(comment: string) {
    this.store.dispatch(setClosureComment({ closureComment: comment }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
