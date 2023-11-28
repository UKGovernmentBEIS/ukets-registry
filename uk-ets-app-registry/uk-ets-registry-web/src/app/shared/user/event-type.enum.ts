export enum EventType {
  ACCOUNT_STATUS_CHANGED = 'ACCOUNT_STATUS_CHANGED',
  ACCOUNT_TASK_COMPLETED = 'ACCOUNT_TASK_COMPLETED',
  ACCOUNT_TASK_CLAIMED = 'ACCOUNT_TASK_CLAIMED',
  ACCOUNT_TASK_ASSIGNED = 'ACCOUNT_TASK_ASSIGNED',
  ACCOUNT_TRANSACTION_TASK = 'ACCOUNT_TRANSACTION_TASK',
  ACCOUNT_TASK_EDIT = 'ACCOUNT_TASK_EDIT',
  TASK_COMMENT = 'TASK_COMMENT',
  TASK_REQUESTED = 'TASK_REQUESTED',
  TASK_CLAIMED = 'TASK_CLAIMED',
  TASK_ASSIGNED = 'TASK_ASSIGNED',
  TASK_COMPLETED = 'TASK_COMPLETED',
  TASK_APPROVED = 'TASK_APPROVED',
  TASK_REJECTED = 'TASK_REJECTED',
  TASK_EDIT = 'TASK_EDIT',
  TRANSACTION_PROPOSAL = 'TRANSACTION_PROPOSAL',
  TRANSACTION_TASK_COMPLETED = 'TRANSACTION_TASK_COMPLETED'
}

export const EVENT_TYPE_LABELS: Record<
  EventType,
  { label: string; description?: string }
> = {
  ACCOUNT_STATUS_CHANGED: { label: 'Account status changed' },
  ACCOUNT_TASK_COMPLETED: { label: 'Account task completed' },
  ACCOUNT_TASK_CLAIMED: { label: 'Account task claimed' },
  ACCOUNT_TASK_ASSIGNED: { label: 'Account task assigned' },
  ACCOUNT_TRANSACTION_TASK: { label: 'Account transaction task' },
  ACCOUNT_TASK_EDIT: { label: 'Account task edit' },
  TASK_COMMENT: { label: 'Comment' },
  TASK_REQUESTED: { label: 'Task requested' },
  TASK_CLAIMED: { label: 'Task claimed' },
  TASK_ASSIGNED: { label: 'Task assigned' },
  TASK_COMPLETED: { label: 'Task completed' },
  TASK_APPROVED: { label: 'Task approved' },
  TASK_REJECTED: { label: 'Task rejected' },
  TASK_EDIT: { label: 'Task edit' },
  TRANSACTION_PROPOSAL: { label: 'Transaction proposal submitted' },
  TRANSACTION_TASK_COMPLETED: { label: 'Transaction task completed' }
};
