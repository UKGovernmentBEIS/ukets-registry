import { Component, Input } from '@angular/core';
import { MenuItem } from '@shared/model/navigation-menu';

@Component({
  selector: 'app-sub-menu',
  templateUrl: './sub-menu.component.html',
  styleUrls: ['./sub-menu.component.scss'],
})
export class SubMenuComponent {
  @Input() subMenuItems: MenuItem[];
  @Input() subMenuActive: string;
}
