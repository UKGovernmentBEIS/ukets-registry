import { Injectable } from '@angular/core';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Injectable({ providedIn: 'root' })
export class ExtendedTitleStrategyService extends TitleStrategy {
  readonly appTitle: string = 'UK Emissions Trading Registry - GOV.UK';

  constructor(private readonly title: Title) {
    super();
  }

  override updateTitle(routerState: RouterStateSnapshot) {
    const title = this.buildTitle(routerState);
    if (title !== undefined) {
      this.title.setTitle(this.appTitle + ` - ${title}`);
    } else {
      this.title.setTitle(this.appTitle);
    }
  }
}
