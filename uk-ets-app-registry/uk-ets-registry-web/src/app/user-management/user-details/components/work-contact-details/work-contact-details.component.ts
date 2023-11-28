import { Component, Input, OnInit } from '@angular/core';
import { KeycloakUser } from '@shared/user/keycloak-user';

const REMOVED_DUE_TO_STATUS = 'REMOVED_DUE_TO_STATUS';

@Component({
  selector: 'app-work-contact-details',
  templateUrl: './work-contact-details.component.html',
})
export class WorkContactDetailsComponent implements OnInit {
  @Input()
  user: KeycloakUser;
  hideWorkAddress: boolean;

  ngOnInit() {
    this.hideWorkAddress =
      (this.user?.attributes.workBuildingAndStreet == null ||
        this.user?.attributes.workBuildingAndStreet[0] ===
          REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.workBuildingAndStreetOptional == null ||
        this.user?.attributes.workBuildingAndStreetOptional[0] ===
          REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.workBuildingAndStreetOptional2 == null ||
        this.user?.attributes.workBuildingAndStreetOptional2[0] ===
          REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.workTownOrCity == null ||
        this.user?.attributes.workTownOrCity[0] === REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.workStateOrProvince == null ||
        this.user?.attributes.workStateOrProvince[0] ===
          REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.workCountry == null ||
        this.user?.attributes.workCountry[0] === REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.workPostCode == null ||
        this.user?.attributes.workPostCode[0] === REMOVED_DUE_TO_STATUS);
  }
}
