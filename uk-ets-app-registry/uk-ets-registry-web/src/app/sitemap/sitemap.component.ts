import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem, NavMenu } from '@shared/model/navigation-menu';

@Component({
  selector: 'app-sitemap',
  templateUrl: './sitemap.component.html',
  styles: [],
})
export class SitemapComponent {
  @Input() navMenu: NavMenu;

  @Output() readonly clickedItem = new EventEmitter<MenuItem>();

  onClick(item: MenuItem) {
    this.clickedItem.emit(item);
  }
}
