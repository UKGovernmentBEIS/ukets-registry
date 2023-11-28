import { Component, OnInit } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { MenuItem, NavMenu } from '@shared/model/navigation-menu';
import { selectNavMenu } from '@shared/shared.selector';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sitemap-container',
  template: `
    <app-sitemap
      [navMenu]="navMenu$ | async"
      (clickedItem)="onClickedItem($event)"
    ></app-sitemap>
  `
})
export class SitemapContainerComponent implements OnInit {
  navMenu$: Observable<NavMenu>;

  constructor(private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.navMenu$ = this.store.pipe(select(selectNavMenu));
  }

  onClickedItem(item: MenuItem) {
    const url =
      item.subMenus?.length > 0 ? item.subMenus[0].routerLink : item.routerLink;
    this.router.navigate([url], {
      queryParams: item.queryParams
    });
  }
}
