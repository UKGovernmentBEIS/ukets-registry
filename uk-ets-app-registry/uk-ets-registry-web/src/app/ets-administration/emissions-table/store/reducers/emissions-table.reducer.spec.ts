import {
  processSelectedFileError,
  uploadEmissionsTableFileSuccess,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@shared/file/actions/file-upload-api.actions';
import { FileBase, UploadStatus } from '@shared/model/file';
import { EmissionsTableState, initialState, reducer } from '.';
import {
  cancelEmissionsTableUpload,
  clearEmissionsTableUpload,
  submitEmissionsTableRequestSuccess,
} from '../actions';

describe('Emissions Table reducer', () => {
  it('sets the status via a uploadSelectedFileHasStarted', () => {
    const status = UploadStatus.Started;
    const uploadSelectedFileHasStartedAction = uploadSelectedFileHasStarted({
      status,
    });
    const afterUploadSelectedFileHasStartedAction = reducer(
      initialState,
      uploadSelectedFileHasStartedAction
    );
    expect(afterUploadSelectedFileHasStartedAction.fileHeader.id).toBeNull();
    expect(
      afterUploadSelectedFileHasStartedAction.fileHeader.fileSize
    ).toBeNull();
    expect(
      afterUploadSelectedFileHasStartedAction.fileHeader.fileName
    ).toBeNull();
    expect(afterUploadSelectedFileHasStartedAction.progress).toEqual(0);
    expect(afterUploadSelectedFileHasStartedAction.status).toEqual(status);
    expect(afterUploadSelectedFileHasStartedAction.requestId).toBeNull();
  });

  it('sets the progress via a uploadSelectedFileInProgress', () => {
    const progress = 35;
    const uploadSelectedFileInProgressAction = uploadSelectedFileInProgress({
      progress,
    });
    const afterUploadSelectedFileInProgressAction = reducer(
      initialState,
      uploadSelectedFileInProgressAction
    );
    expect(afterUploadSelectedFileInProgressAction.fileHeader.id).toBeNull();
    expect(
      afterUploadSelectedFileInProgressAction.fileHeader.fileSize
    ).toBeNull();
    expect(
      afterUploadSelectedFileInProgressAction.fileHeader.fileName
    ).toBeNull();
    expect(afterUploadSelectedFileInProgressAction.progress).toEqual(progress);
    expect(afterUploadSelectedFileInProgressAction.status).toBeTruthy();
    expect(afterUploadSelectedFileInProgressAction.requestId).toBeNull();
  });

  it('sets the file via a uploadEmissionsTableFileSuccess', () => {
    const fileHeader: FileBase = {
      id: 9656,
      fileName:
        'UK_Emissions_28062021_SEPA_83BBCD0F874C2E436A5E6DA772AA2822.xlsx',
      fileSize: '5435234',
    };
    const uploadSelectedFileInProgressAction = uploadEmissionsTableFileSuccess({
      fileHeader,
    });
    const afterUploadSelectedFileSuccessAction = reducer(
      initialState,
      uploadSelectedFileInProgressAction
    );
    expect(afterUploadSelectedFileSuccessAction.progress).toEqual(100);
    expect(afterUploadSelectedFileSuccessAction.status).toEqual(
      UploadStatus.Completed
    );
    expect(afterUploadSelectedFileSuccessAction.fileHeader.id).toEqual(
      fileHeader.id
    );
    expect(afterUploadSelectedFileSuccessAction.fileHeader.fileSize).toEqual(
      fileHeader.fileSize
    );
    expect(afterUploadSelectedFileSuccessAction.fileHeader.fileName).toEqual(
      fileHeader.fileName
    );
  });

  it('sets the status to failed via a processSelectedFileError', () => {
    const status = UploadStatus.Failed;
    const startedState: EmissionsTableState = {
      fileHeader: {
        id: 24423,
        fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
        fileSize: '3534455',
      },
      status: UploadStatus.Started,
      progress: 65,
      requestId: null,
    };
    const processSelectedFileErrorAction = processSelectedFileError({
      status,
    });
    const afterProcessSelectedFileErrorAction = reducer(
      startedState,
      processSelectedFileErrorAction
    );
    expect(afterProcessSelectedFileErrorAction.progress).toBeTruthy();
    expect(afterProcessSelectedFileErrorAction.status).toEqual(status);
    expect(afterProcessSelectedFileErrorAction.fileHeader.id).toEqual(
      startedState.fileHeader.id
    );
    expect(afterProcessSelectedFileErrorAction.fileHeader.fileSize).toEqual(
      startedState.fileHeader.fileSize
    );
    expect(afterProcessSelectedFileErrorAction.fileHeader.fileName).toEqual(
      startedState.fileHeader.fileName
    );
    expect(afterProcessSelectedFileErrorAction.requestId).toBeNull();
  });

  it('clears the state via a cancelEmissionsTableUpload', () => {
    const nonEmptyState: EmissionsTableState = {
      fileHeader: {
        id: 24423,
        fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
        fileSize: '3534455',
      },
      status: UploadStatus.Completed,
      progress: null,
      requestId: null,
    };

    const beforeCancelEmissionsTableUploadAction = reducer(
      nonEmptyState,
      {} as any
    );
    expect(beforeCancelEmissionsTableUploadAction.fileHeader).toBeTruthy();
    expect(beforeCancelEmissionsTableUploadAction.fileHeader.id).toBeTruthy();
    expect(
      beforeCancelEmissionsTableUploadAction.fileHeader.fileSize
    ).toBeTruthy();
    expect(
      beforeCancelEmissionsTableUploadAction.fileHeader.fileName
    ).toBeTruthy();
    expect(beforeCancelEmissionsTableUploadAction.progress).toBeNull();
    expect(beforeCancelEmissionsTableUploadAction.status).toEqual(
      UploadStatus.Completed
    );
    expect(beforeCancelEmissionsTableUploadAction.requestId).toBeNull();

    const cancelEmissionsTableUploadAction = cancelEmissionsTableUpload();
    const afterCancelEmissionsTableUploadAction = reducer(
      nonEmptyState,
      cancelEmissionsTableUploadAction
    );
    expect(afterCancelEmissionsTableUploadAction.fileHeader.id).toBeNull();
    expect(
      afterCancelEmissionsTableUploadAction.fileHeader.fileSize
    ).toBeNull();
    expect(
      afterCancelEmissionsTableUploadAction.fileHeader.fileName
    ).toBeNull();
    expect(afterCancelEmissionsTableUploadAction.progress).toBeNull();
    expect(afterCancelEmissionsTableUploadAction.status).toEqual(
      UploadStatus.Ready
    );
    expect(afterCancelEmissionsTableUploadAction.requestId).toBeNull();
  });

  it('clears the state via a clearEmissionsTableUploadAction', () => {
    const nonEmptyState: EmissionsTableState = {
      fileHeader: {
        id: 24423,
        fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
        fileSize: '3534455',
      },
      status: UploadStatus.Completed,
      progress: null,
      requestId: null,
    };

    const beforeClearEmissionsTableUploadAction = reducer(
      nonEmptyState,
      {} as any
    );
    expect(beforeClearEmissionsTableUploadAction.fileHeader).toBeTruthy();
    expect(beforeClearEmissionsTableUploadAction.fileHeader.id).toBeTruthy();
    expect(
      beforeClearEmissionsTableUploadAction.fileHeader.fileSize
    ).toBeTruthy();
    expect(
      beforeClearEmissionsTableUploadAction.fileHeader.fileName
    ).toBeTruthy();
    expect(beforeClearEmissionsTableUploadAction.progress).toBeNull();
    expect(beforeClearEmissionsTableUploadAction.status).toEqual(
      UploadStatus.Completed
    );
    expect(beforeClearEmissionsTableUploadAction.requestId).toBeNull();

    const clearEmissionsTableUploadAction = clearEmissionsTableUpload();
    const afterClearEmissionsTableUploadAction = reducer(
      nonEmptyState,
      clearEmissionsTableUploadAction
    );
    expect(afterClearEmissionsTableUploadAction.fileHeader.id).toBeNull();
    expect(afterClearEmissionsTableUploadAction.fileHeader.fileSize).toBeNull();
    expect(afterClearEmissionsTableUploadAction.fileHeader.fileName).toBeNull();
    expect(afterClearEmissionsTableUploadAction.progress).toBeNull();
    expect(afterClearEmissionsTableUploadAction.status).toEqual(
      UploadStatus.Ready
    );
    expect(afterClearEmissionsTableUploadAction.requestId).toBeNull();
  });

  it('sets the requestId via the submitEmissionsTableRequestSuccessAction', () => {
    const nonEmptyState: EmissionsTableState = {
      fileHeader: {
        id: 24423,
        fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
        fileSize: '3534455',
      },
      status: UploadStatus.Completed,
      progress: null,
      requestId: null,
    };
    const requestId = 'UK789065';

    const beforeSubmitEmissionsTableRequestSuccessAction = reducer(
      nonEmptyState,
      {} as any
    );
    expect(
      beforeSubmitEmissionsTableRequestSuccessAction.fileHeader
    ).toBeTruthy();
    expect(
      beforeSubmitEmissionsTableRequestSuccessAction.fileHeader.id
    ).toBeTruthy();
    expect(
      beforeSubmitEmissionsTableRequestSuccessAction.fileHeader.fileSize
    ).toBeTruthy();
    expect(
      beforeSubmitEmissionsTableRequestSuccessAction.fileHeader.fileName
    ).toBeTruthy();
    expect(beforeSubmitEmissionsTableRequestSuccessAction.progress).toBeNull();
    expect(beforeSubmitEmissionsTableRequestSuccessAction.status).toEqual(
      UploadStatus.Completed
    );
    expect(beforeSubmitEmissionsTableRequestSuccessAction.requestId).toBeNull();

    const submitEmissionsTableRequestSuccessAction =
      submitEmissionsTableRequestSuccess({
        requestId,
      });
    const afterSubmitEmissionsTableRequestSuccessAction = reducer(
      nonEmptyState,
      submitEmissionsTableRequestSuccessAction
    );
    expect(
      afterSubmitEmissionsTableRequestSuccessAction.fileHeader.id
    ).toBeTruthy();
    expect(
      afterSubmitEmissionsTableRequestSuccessAction.fileHeader.fileSize
    ).toBeTruthy();
    expect(
      afterSubmitEmissionsTableRequestSuccessAction.fileHeader.fileName
    ).toBeTruthy();
    expect(afterSubmitEmissionsTableRequestSuccessAction.progress).toBeNull();
    expect(afterSubmitEmissionsTableRequestSuccessAction.status).toEqual(
      UploadStatus.Completed
    );
    expect(afterSubmitEmissionsTableRequestSuccessAction.requestId).toEqual(
      requestId
    );
  });
});
