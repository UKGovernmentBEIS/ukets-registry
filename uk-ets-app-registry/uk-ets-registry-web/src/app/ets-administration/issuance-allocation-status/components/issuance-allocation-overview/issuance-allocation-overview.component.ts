import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-issuance-allocation-overview',
  templateUrl: './issuance-allocation-overview.component.html'
})
export class IssuanceAllocationOverviewComponent {
  constructor(private _router: Router) {}

  navigateToUploadAllocationTable() {
    this._router.navigate(['ets-administration/allocation-table'], {
      skipLocationChange: false
    });
  }
}
