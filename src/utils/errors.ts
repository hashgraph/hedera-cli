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
export function exitOnError<T extends (...args: any[]) => void | Promise<void>>( // eslint-disable-line @typescript-eslint/no-explicit-any
  fn: T,
): (...funcArgs: Parameters<T>) => Promise<void> {
  return async (...funcArgs: Parameters<T>): Promise<void> => {
    try {
      await fn(...funcArgs);
    } catch (e: unknown) {
      if (e instanceof DomainError) {
        process.exitCode = e.code;
        try {
          await telemetryUtils.flush?.();
        } catch {
          /* ignore flush errors */
        }
        return;
      }
      throw e;
    }
  };
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

  const g = global as unknown as { _hcliGlobalHandlersInstalled?: boolean };
  if (!g._hcliGlobalHandlersInstalled) {
    g._hcliGlobalHandlersInstalled = true;

    process.on('unhandledRejection', (reason: unknown) => {
      if (reason instanceof DomainError) {
        if (process.exitCode == null) process.exitCode = reason.code;
      } else {
        const detail =
          typeof reason === 'object' && reason !== null
            ? reason
            : { value: String(reason) };
        logger.error('Unhandled promise rejection', detail);
        if (process.exitCode == null) process.exitCode = 1;
      }
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      flushTelemetry();
    });

    process.on('uncaughtException', (err: unknown) => {
      if (err instanceof DomainError) {
        if (process.exitCode == null) process.exitCode = err.code;
      } else {
        const detail =
          typeof err === 'object' && err !== null
            ? err
            : { value: String(err) };
        logger.error('Uncaught exception', detail);
        if (process.exitCode == null) process.exitCode = 1;
      }
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      flushTelemetry();
    });
  }
}
