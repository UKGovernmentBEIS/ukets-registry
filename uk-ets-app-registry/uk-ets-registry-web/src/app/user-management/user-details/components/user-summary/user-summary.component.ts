import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { KeycloakUser } from '@shared/user';
import {
  AGENT_TYPE_LABELS,
  AgentType,
  ViewMode,
} from '@user-management/user-details/model';
import { CRC_LABELS } from '../../model/crc.model';

@Component({
  selector: 'app-user-summary',
  templateUrl: './user-summary.component.html',
  styles: ``,
})
export class UserSummaryComponent implements OnInit {
  @Input()
  user: KeycloakUser;
  @Input()
  isSeniorAdmin: boolean;
  @Input()
  isSeniorOrJuniorAdmin: boolean;
  @Input()
  currentViewMode: ViewMode;

  agentType: AgentType;
  agentTypeLabel: string;

  crc: string;
  crcLabel: string;

  @Output()
  readonly agentChange = new EventEmitter<string>();

  @Output()
  readonly crcChange = new EventEmitter<string>();

  ngOnInit() {
    this.agentType =
      this.user?.attributes?.agent && this.user.attributes.agent.length > 0
        ? (this.user.attributes.agent[0] as AgentType)
        : 'NO';
    this.agentTypeLabel = AGENT_TYPE_LABELS[this.agentType].label;

    this.crc =
      this.user?.attributes?.crc && this.user.attributes.crc.length > 0
        ? this.user.attributes.crc[0]
        : 'false';
    this.crcLabel = CRC_LABELS[this.crc].label;
  }

  onClickCrcChange() {
    this.crcChange.emit(this.user.attributes.crc[0]);
  }

  onClickAgentChange() {
    this.agentChange.emit(this.user.attributes.agent[0]);
  }
}
