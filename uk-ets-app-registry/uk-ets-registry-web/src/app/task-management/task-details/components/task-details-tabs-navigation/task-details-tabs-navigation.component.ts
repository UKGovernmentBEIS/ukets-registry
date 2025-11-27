import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MenuItem } from '@registry-web/shared/model/navigation-menu';
import { SharedModule } from '@registry-web/shared/shared.module';

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

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.subMenuActive = this.route.snapshot['_routerState'].url;

    this.subMenuItems.push({
      label: 'Task details',
      routerLink: `/task-details/${this.requestId}`,
      protectedScopes: [],
    });

    if (this.isAdmin) {
      this.subMenuItems.push({
        label: 'Notes',
        routerLink: `/task-details/${this.requestId}/notes-list`,
        protectedScopes: [],
      });
    }

    this.subMenuItems.push({
      label: 'History and comments',
      routerLink: `/task-details/${this.requestId}/history`,
      protectedScopes: [],
    });
  }
}
