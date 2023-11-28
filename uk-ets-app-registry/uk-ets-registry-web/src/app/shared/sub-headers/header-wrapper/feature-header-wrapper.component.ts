import {
  AfterViewInit,
  ApplicationRef,
  Component,
  ComponentFactoryResolver,
  Injector,
  OnDestroy,
  ViewChild
} from '@angular/core';
import { CdkPortal, DomPortalOutlet } from '@angular/cdk/portal';

/**
 * this is a wrapper component that takes advantage of the angular cdk portals
 * to be able to dynamically render task-headers, or any other feature header into
 * the open slot marked with id= 'feature-header' and is contained in the app.component.html
 */
@Component({
  selector: 'app-feature-header-wrapper',
  template: `
    <ng-container *cdkPortal>
      <ng-content> </ng-content>
    </ng-container>
  `
})
export class FeatureHeaderWrapperComponent implements AfterViewInit, OnDestroy {
  @ViewChild(CdkPortal)
  private portal: CdkPortal;

  private host: DomPortalOutlet;

  constructor(
    private crf: ComponentFactoryResolver,
    private applicationRef: ApplicationRef,
    private injector: Injector
  ) {}

  ngAfterViewInit(): void {
    this.host = new DomPortalOutlet(
      document.querySelector('#feature-header'),
      this.crf,
      this.applicationRef,
      this.injector
    );
    this.host.attach(this.portal);
  }

  ngOnDestroy(): void {
    this.host.detach();
  }
}
