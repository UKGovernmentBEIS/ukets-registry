import {
  RequestType,
  TaskDetailsBase,
} from '@shared/task-and-regulator-notice-management/model';

export interface RegulatorNoticeTaskDetails extends TaskDetailsBase {
  taskType: RequestType.REGULATOR_NOTICE;
  processType: string;
  accountHolderName: string;
  accountHolderIdentifier: number;
  operatorId: number;
  permitOrMonitoringPlanIdentifier: string;
  file: {
    fileName: string;
    id: number;
  };
}
