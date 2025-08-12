import telemetryUtils from './telemetry';
import { Logger } from './logger';

export class DomainError extends Error {
  public code: number;

  constructor(message: string, code = 1) {
    super(message);
    this.name = 'DomainError';
    this.code = code;
  }
}

// Convenience helper to throw a DomainError (replaces scattered process.exit calls)
export function fail(message: string, code = 1): never {
  throw new DomainError(message, code);
}

// Wrap a top-level invocation so DomainErrors set exitCode and allow graceful shutdown
export function exitOnError<T extends (...args: any[]) => Promise<any> | any>(
  fn: T,
): T {
  return (async (...args: any[]) => {
    try {
      return await fn(...args);
    } catch (e: any) {
      if (e instanceof DomainError) {
        // Step A: graceful termination without hard process.exit
        process.exitCode = e.code;
        // Step C: attempt to flush telemetry before exiting naturally
        try {
          // Optional chaining in case older telemetry utils lack flush
          await telemetryUtils.flush?.();
        } catch {
          /* swallow */
        }
        return; // swallow the DomainError after setting exitCode
      }
      throw e;
    }
  }) as unknown as T;
}

// Install global handlers for unhandled rejections & uncaught exceptions (optional hardening)
export function installGlobalErrorHandlers(): void {
  const logger = Logger.getInstance();

  const flushTelemetry = async () => {
    try {
      await telemetryUtils.flush?.();
    } catch {
      /* ignore telemetry flush errors */
    }
  };

  if (!(global as any)._hcliGlobalHandlersInstalled) {
    (global as any)._hcliGlobalHandlersInstalled = true;

    process.on('unhandledRejection', (reason: any) => {
      if (reason instanceof DomainError) {
        if (process.exitCode == null) process.exitCode = reason.code;
      } else {
        logger.error('Unhandled promise rejection', reason as any);
        if (process.exitCode == null) process.exitCode = 1;
      }
      // Fire and forget; don't hold the process open excessively.
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      flushTelemetry();
    });

    process.on('uncaughtException', (err: any) => {
      if (err instanceof DomainError) {
        if (process.exitCode == null) process.exitCode = err.code;
      } else {
        logger.error('Uncaught exception', err as any);
        if (process.exitCode == null) process.exitCode = 1;
      }
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      flushTelemetry();
    });
  }
}
