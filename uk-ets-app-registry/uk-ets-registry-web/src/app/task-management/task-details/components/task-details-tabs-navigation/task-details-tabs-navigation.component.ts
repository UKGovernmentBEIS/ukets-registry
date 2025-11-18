import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MenuItem } from '@registry-web/shared/model/navigation-menu';
import { SharedModule } from '@registry-web/shared/shared.module';
import { selectTaskNotes } from '@task-details/components/task-notes/store/task-notes.selector';
import { Store } from '@ngrx/store';
import { fetchTaskNotes } from '@task-details/components/task-notes/store/task-notes.actions';

@Component({
  standalone: true,
  imports: [RouterModule, SharedModule],
  templateUrl: './task-details-tabs-navigation.component.html',
  selector: 'app-task-details-tabs-navigation',
})
export class TaskDetailsTabsNavigationComponent implements OnInit {
  @Input() requestId: string;
  @Input() isAdmin: boolean;

  subMenuItems: MenuItem[] = [];
  subMenuActive: string;

  constructor(
    private route: ActivatedRoute,
    private store: Store,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.subMenuActive = this.route.snapshot['_routerState'].url;

    const baseItems: MenuItem[] = [
      {
        label: 'Task details',
        routerLink: `/task-details/${this.requestId}`,
        protectedScopes: [],
      },
    ];

    if (this.isAdmin) {
      this.store.dispatch(
        fetchTaskNotes({
          requestId: this.requestId,
        })
      );

      this.store.select(selectTaskNotes).subscribe((notes) => {
        let lbl = 'Notes';
        if (notes.length > 0) {
          lbl = `Notes (${notes.length})`;
        }

        this.subMenuItems = [
          ...baseItems,
          {
            label: lbl,
            routerLink: `/task-details/${this.requestId}/notes-list`,
            protectedScopes: [],
          },
          {
            label: 'History and comments',
            routerLink: `/task-details/${this.requestId}/history`,
            protectedScopes: [],
          },
        ];
        this.cd.markForCheck();
      });
    } else {
      this.subMenuItems = [
        ...baseItems,
        {
          label: 'History and comments',
          routerLink: `/task-details/${this.requestId}/history`,
          protectedScopes: [],
        },
      ];
    }
  }
}
