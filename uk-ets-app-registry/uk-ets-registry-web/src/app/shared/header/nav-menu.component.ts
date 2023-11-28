import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HeaderItem } from '@shared/header/header-item.enum';
import { MenuItem, NavMenu } from '@shared/model/navigation-menu';
import { SearchMode } from '@shared/resolvers/search.resolver';

@Component({
  selector: 'app-nav-menu',
  templateUrl: './nav-menu.component.html',
  styles: [],
})
export class NavMenuComponent {
  @Input() navMenu: NavMenu;
  @Input() authenticated: boolean;
  @Input() url: string;
  @Input() activeMenuItem: HeaderItem;

  @Output() readonly clickedItem = new EventEmitter<MenuItem>();

  searchMode = SearchMode;

  onClick(item: MenuItem) {
    this.clickedItem.emit(item);
  }
}
