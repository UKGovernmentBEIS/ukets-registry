import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-change-password-confirmation-container',
  templateUrl: './change-password-confirmation-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangePasswordConfirmationContainerComponent implements OnInit {
  emailPasswordChange: string;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.emailPasswordChange = this.route.snapshot.paramMap.get('email');
  }
}
