import { Component, Input, OnInit } from '@angular/core';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-sub-wizard-title',
  templateUrl: './sub-wizard-title.component.html',
})
export class SubWizardTitleComponent implements OnInit {
  @Input() subTitle: string;
  @Input() contactType: string;
  @Input() isAHUpdateWizard = false;
  title: string;

  ngOnInit() {
    if (this.isAHUpdateWizard) {
      this.title = 'Request to update the account holder';
    } else {
      this.title =
        this.contactType === ContactType.PRIMARY
          ? 'Add the Primary Contact'
          : 'Add an alternative Primary Contact';
    }
  }
}
