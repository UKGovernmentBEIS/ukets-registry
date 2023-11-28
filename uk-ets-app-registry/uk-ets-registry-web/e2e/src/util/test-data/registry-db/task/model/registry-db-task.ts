import { RegistryDbTaskBuilder } from './registry-db-task.builder';

export class RegistryDbTask {
  id: string;
  account_id: string;
  transaction_identifier: string;
  claimed_by: string;
  status: string;
  outcome: string;
  type: string;
  request_identifier: string;
  initiated_by: string;
  completed_by: string;
  before: string;
  after: string;
  difference: string;
  parent_task_id: string;
  initiated_date: string;
  claimed_date: string;
  completed_date: string;
  user_id: string;
  file: string;
  recipient_account_number: string;

  constructor(builder: RegistryDbTaskBuilder) {
    this.account_id = builder.getAccount_id();
    this.transaction_identifier = builder.getTransaction_identifier();
    this.claimed_by = builder.getClaimed_by();
    this.status = builder.getStatus();
    this.outcome = builder.getOutcome();
    this.type = builder.getType();
    this.request_identifier = builder.getRequest_identifier();
    this.initiated_by = builder.getInitiated_by();
    this.completed_by = builder.getCompleted_by();
    this.before = builder.getBefore();
    this.after = builder.getAfter();
    this.difference = builder.getDifference();
    this.parent_task_id = builder.getParent_task_id();
    this.initiated_date = builder.getInitiated_date();
    this.claimed_date = builder.getClaimed_date();
    this.completed_date = builder.getCompleted_date();
    this.user_id = builder.getUser_id();
    this.file = builder.getFile();
    this.recipient_account_number = builder.getRecipient_account_number();
  }
}
