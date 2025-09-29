import { Logger } from '../../src/utils/logger';

/**
 * Temporarily set the global logger level for the duration of a callback.
 * Safe because each Jest test file runs in an isolated process.
 */
export async function withLoggerLevel<T>(
  level: 'verbose' | 'quiet' | 'normal',
  fn: () => Promise<T> | T,
): Promise<T> {
  const logger = Logger.getInstance();
  // Support legacy level property via mode mapping
  const prev = (logger as unknown as { mode?: string }).mode || 'normal';
  try {
    logger.setLevel(level);
    return await fn();
  } finally {
    // Map back to legacy setLevel if possible (prev may be quiet/normal/verbose/silent)
    if (prev === 'silent') {
      // silent not part of legacy API; set quiet then elevate transport manually
      logger.setLevel('quiet');
      (logger as unknown as { setMode?: (m: string) => void }).setMode?.(
        'silent',
      );
    } else if (prev === 'verbose' || prev === 'quiet' || prev === 'normal') {
      logger.setLevel(prev);
    } else {
      logger.setLevel('normal');
    }
  }
}

/** Run a block with the logger fully silenced (quiet mode). */
export function withSilencedLogs<T>(fn: () => Promise<T> | T): Promise<T> {
  return withLoggerLevel('quiet', fn);
}

/** Run a block forcing normal logs even if prior code changed the level. */
export function withNormalLogs<T>(fn: () => Promise<T> | T): Promise<T> {
  return withLoggerLevel('normal', fn);
}

/** Run a block with verbose logs. */
export function withVerboseLogs<T>(fn: () => Promise<T> | T): Promise<T> {
  return withLoggerLevel('verbose', fn);
}

/** Imperative helpers (avoid if you can; prefer scoped versions above). */
export function setLoggerQuiet() {
  Logger.getInstance().setLevel('quiet');
}
export function setLoggerNormal() {
  Logger.getInstance().setLevel('normal');
}
export function setLoggerVerbose() {
  Logger.getInstance().setLevel('verbose');
}
