import { Component, Input, OnInit } from '@angular/core';
import { AlertsModel } from '@registry-web/dashboard/alert-monitoring/model';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss'],
})
export class AlertsComponent implements OnInit {
  @Input() alerts: [AlertsModel, boolean];

  ngOnInit(): void {
    console.log('There are alerts!', this.alerts);
  }
}
