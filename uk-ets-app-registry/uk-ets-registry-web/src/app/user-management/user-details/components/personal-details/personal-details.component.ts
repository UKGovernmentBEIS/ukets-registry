import { Component, Input, OnInit } from '@angular/core';
import { KeycloakUser } from '@shared/user/keycloak-user';

const REMOVED_DUE_TO_STATUS = 'REMOVED_DUE_TO_STATUS';

@Component({
  selector: 'app-personal-details',
  templateUrl: './personal-details.component.html',
})
export class PersonalDetailsComponent implements OnInit {
  @Input() user: KeycloakUser;
  hideHomeAddress: boolean;

  ngOnInit() {
    this.hideHomeAddress =
      (this.user?.attributes.buildingAndStreet == null ||
        this.user?.attributes.buildingAndStreet[0] === REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.buildingAndStreetOptional == null ||
        this.user?.attributes.buildingAndStreetOptional[0] ===
          REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.buildingAndStreetOptional2 == null ||
        this.user?.attributes.buildingAndStreetOptional2[0] ===
          REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.townOrCity == null ||
        this.user?.attributes.townOrCity[0] === REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.stateOrProvince == null ||
        this.user?.attributes.stateOrProvince[0] === REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.country == null ||
        this.user?.attributes.country[0] === REMOVED_DUE_TO_STATUS) &&
      (this.user?.attributes.postCode == null ||
        this.user?.attributes.postCode[0] === REMOVED_DUE_TO_STATUS);
  }
}
