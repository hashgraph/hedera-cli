export class DomainError extends Error {
  public code: number;
  constructor(message: string, code = 1) {
    super(message);
    this.name = 'DomainError';
    this.code = code;
  }
}

export function exitOnError<T extends (...args: any[]) => Promise<any> | any>(
  fn: T,
): T {
  return (async (...args: any[]) => {
    try {
      return await fn(...args);
    } catch (e: any) {
      if (e instanceof DomainError) {
        process.exit(e.code);
      }
      throw e;
    }
  }) as any as T;
}
