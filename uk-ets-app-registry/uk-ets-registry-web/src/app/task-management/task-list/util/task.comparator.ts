import { Task } from '@task-management/model';

export function indexOfTask(list: Task[], task: Task): number {
  for (let i = 0; i < list.length; i++) {
    if (compare(list[i], task) === 0) {
      return i;
    }
  }
  return -1;
}

export function compare(task1: Task, task2: Task): number {
  if (!!task1 && !!task2 && task1.requestId === task2.requestId) {
    return 0;
  }
  return -1;
}
