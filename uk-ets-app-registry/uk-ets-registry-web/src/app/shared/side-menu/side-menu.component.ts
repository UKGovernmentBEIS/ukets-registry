import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss'],
})
export class SideMenuComponent {
  @Input()
  menuItems: string[];
  @Input()
  selectedItem: string;
  @Output() readonly selectItem = new EventEmitter();
}
