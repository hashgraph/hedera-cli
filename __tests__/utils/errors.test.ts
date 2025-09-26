import {
  DomainError,
  exitOnError,
  installGlobalErrorHandlers,
} from '../../src/utils/errors';
import { Logger } from '../../src/utils/logger';
import telemetryUtils from '../../src/utils/telemetry';

jest.mock('../../src/utils/telemetry', () => ({
  __esModule: true,
  default: {
    recordCommand: jest.fn().mockResolvedValue(undefined),
    flush: jest.fn().mockResolvedValue(undefined),
  },
}));

// TODO: Re-enable this suite. It makes CI exit with code 1 (global exitCode/listener leakage). Tracked in #827.
// https://github.com/hashgraph/hedera-cli/issues/827
describe.skip('errors utilities', () => {
  beforeEach(() => {
    // Reset exitCode between tests
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (process as any).exitCode = undefined;
    jest.clearAllMocks();
  });

  it('exitOnError swallows DomainError, sets process.exitCode, and flushes telemetry', async () => {
    const wrapped = exitOnError(async () => {
      throw new DomainError('Test domain failure', 5);
    });

    await expect(wrapped()).resolves.toBeUndefined();
    expect(process.exitCode).toBe(5);
    expect((telemetryUtils as any).flush).toHaveBeenCalledTimes(1);
  });

  it('exitOnError rethrows non-DomainError', async () => {
    const wrapped = exitOnError(async () => {
      throw new Error('Generic failure');
    });

    await expect(wrapped()).rejects.toThrow('Generic failure');
    expect(process.exitCode).toBeUndefined();
    expect((telemetryUtils as any).flush).not.toHaveBeenCalled();
  });

  describe('global handlers', () => {
    let unhandledRejectionHandler: ((reason: any) => void) | undefined;
    let uncaughtExceptionHandler: ((err: any) => void) | undefined;

    beforeAll(() => {
      const originalOn = process.on;
      jest.spyOn(process, 'on').mockImplementation(((
        event: any,
        listener: any,
      ) => {
        if (event === 'unhandledRejection')
          unhandledRejectionHandler = listener;
        if (event === 'uncaughtException') uncaughtExceptionHandler = listener;
        return originalOn.call(process, event, listener);
      }) as any);
      installGlobalErrorHandlers();
    });

    it('unhandledRejection with DomainError sets exitCode to DomainError code', async () => {
      unhandledRejectionHandler?.(new DomainError('UR Domain', 7));
      expect(process.exitCode).toBe(7);
      expect((telemetryUtils as any).flush).toHaveBeenCalledTimes(1);
    });

    it('unhandledRejection with generic error sets exitCode to 1 and logs', async () => {
      // Reset exitCode
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (process as any).exitCode = undefined;
      const logger = Logger.getInstance();
      const spy = jest.spyOn(logger, 'error').mockImplementation(() => {});
      unhandledRejectionHandler?.(new Error('UR Generic'));
      expect(process.exitCode).toBe(1);
      expect(spy).toHaveBeenCalled();
      expect((telemetryUtils as any).flush).toHaveBeenCalledTimes(1);
      spy.mockRestore();
    });

    it('uncaughtException with DomainError sets exitCode', async () => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (process as any).exitCode = undefined;
      uncaughtExceptionHandler?.(new DomainError('UE Domain', 4));
      expect(process.exitCode).toBe(4);
      expect((telemetryUtils as any).flush).toHaveBeenCalledTimes(1);
    });

    it('uncaughtException with generic error sets exitCode=1 and logs', async () => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (process as any).exitCode = undefined;
      const logger = Logger.getInstance();
      const spy = jest.spyOn(logger, 'error').mockImplementation(() => {});
      uncaughtExceptionHandler?.(new Error('UE Generic'));
      expect(process.exitCode).toBe(1);
      expect(spy).toHaveBeenCalled();
      expect((telemetryUtils as any).flush).toHaveBeenCalledTimes(1);
      spy.mockRestore();
    });
  });
});
