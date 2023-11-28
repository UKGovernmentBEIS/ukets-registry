import { Component, Input, OnInit } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { isAuthenticated, selectLoggedInUser } from '../auth/auth.selector';
import { Observable } from 'rxjs';
import { AuthModel } from '../auth/auth.model';
import { BannerType } from '@shared/banner/banner-type.enum';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterLinks } from './registry-activation/model/registry-activation.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  isAuthenticated$: Observable<boolean>;
  loggedInUser$: Observable<AuthModel>;
  @Input() systemAdministrationEnabled: boolean;

  bannerTypes = BannerType;
  underConstructionRouterLink = '/under-construction';
  routerLinks = RouterLinks;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.isAuthenticated$ = this.store.pipe(select(isAuthenticated));
    this.loggedInUser$ = this.store.select(selectLoggedInUser);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  reset() {
    this.router.navigate([
      this.activatedRoute.snapshot['_routerState'].url +
        '/system-administration/reset',
    ]);
  }
}
