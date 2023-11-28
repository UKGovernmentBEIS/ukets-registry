import { UserStatusPipe } from './user-status.pipe';

describe('UserStatusPipe', () => {
  it('create an instance', () => {
    const pipe = new UserStatusPipe();
    expect(pipe).toBeTruthy();
  });

  it('should render Registered for status REGISTERED', () => {
    expect(new UserStatusPipe().transform('REGISTERED')).toEqual('Registered');
  });

  it('should render Validated for status VALIDATED', () => {
    expect(new UserStatusPipe().transform('VALIDATED')).toEqual('Validated');
  });

  it('should render Enrolled for status ENROLLED', () => {
    expect(new UserStatusPipe().transform('ENROLLED')).toEqual('Enrolled');
  });

  it('should render Unenrollment pending for status UNENROLLEMENT_PENDING', () => {
    expect(new UserStatusPipe().transform('UNENROLLEMENT_PENDING')).toEqual(
      'Unenrollment pending'
    );
  });

  it('should render Unenrolled for status UNENROLLED', () => {
    expect(new UserStatusPipe().transform('UNENROLLED')).toEqual('Unenrolled');
  });

  it('should render Suspended for status SUSPENDED', () => {
    expect(new UserStatusPipe().transform('SUSPENDED')).toEqual('Suspended');
  });
});
