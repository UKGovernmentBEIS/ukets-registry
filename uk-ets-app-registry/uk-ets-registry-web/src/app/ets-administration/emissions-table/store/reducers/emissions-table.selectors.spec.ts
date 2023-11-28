import { UploadStatus } from '@shared/model/file';
import {
  EmissionsTableState,
  selectEmissionsTableFile,
  selectEmissionsTableRequestId,
  selectUploadFileIsInProgress,
  selectUploadFileProgress,
} from '.';

describe('Selectors', () => {
  const nonEmptyState: EmissionsTableState = {
    fileHeader: {
      id: 24423,
      fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
      fileSize: '3534455',
    },
    status: UploadStatus.Completed,
    progress: 57,
    requestId: 'UK839282',
  };

  it('should select the File Header', () => {
    const result = selectEmissionsTableFile.projector(nonEmptyState);
    expect(result.id).toEqual(24423);
    expect(result.fileSize).toEqual('3534455');
    expect(result.fileName).toEqual(
      'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx'
    );
  });

  it('should select the task (a.k.a. requestId)', () => {
    const result = selectEmissionsTableRequestId.projector(nonEmptyState);
    expect(result).toEqual('UK839282');
  });

  it('should select the upload file progress', () => {
    const result = selectUploadFileProgress.projector(nonEmptyState);
    expect(result).toEqual(57);
  });

  it('should report the upload file progress as finished', () => {
    const result = selectUploadFileIsInProgress.projector(nonEmptyState);
    expect(result).toBeFalsy();
  });

  it('should report the upload file progress as pending', () => {
    const emissionsTableState: EmissionsTableState = {
      fileHeader: {
        id: 24423,
        fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
        fileSize: '3534455',
      },
      status: UploadStatus.Started,
      progress: 57,
      requestId: 'UK839282',
    };

    const result = selectUploadFileIsInProgress.projector(emissionsTableState);
    expect(result).toBeTruthy();
  });
});
