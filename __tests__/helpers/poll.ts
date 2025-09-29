export interface PollOptions {
  timeout?: number; // total timeout ms
  interval?: number; // interval ms
  description?: string; // optional description for error message
}

/**
 * Polls an async or sync predicate until it returns a truthy value or timeout.
 * Returns the truthy value. Throws on timeout with a helpful message.
 */
export async function waitFor<T>(
  fn: () => Promise<T> | T,
  opts: PollOptions = {},
): Promise<T> {
  const timeout = opts.timeout ?? 15000;
  const interval = opts.interval ?? 500;
  const start = Date.now();
  // First attempt immediately
  // eslint-disable-next-line no-constant-condition
  while (true) {
    const result = await fn();
    if (result) return result;
    if (Date.now() - start >= timeout) {
      throw new Error(
        `waitFor timeout after ${timeout}ms${opts.description ? ` while waiting for ${opts.description}` : ''}`,
      );
    }
    await new Promise((r) => setTimeout(r, interval));
  }
}

/** Convenience sleep */
export const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));
