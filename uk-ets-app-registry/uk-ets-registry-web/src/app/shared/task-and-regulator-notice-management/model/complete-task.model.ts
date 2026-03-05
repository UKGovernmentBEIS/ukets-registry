import { TaskOutcome } from './task-and-regulator-notice-management.model';

export type Comment = string;
export type Amount = number;

export interface CompleteTaskFormInfo {
  comment: Comment;
  otp: string;
  amountPaid: Amount;
}

export type BaseCompleteTaskInfo = Partial<
  Pick<CompleteTaskFormInfo, 'amountPaid'>
> &
  Pick<CompleteTaskFormInfo, 'comment'> & {
    taskId: string;
  };

export type CompleteTaskInfo = BaseCompleteTaskInfo & {
  taskOutcome: TaskOutcome;
};
