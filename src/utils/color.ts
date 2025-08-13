// Color helper built atop colorette for lightweight, dependency-based styling
// Provides central enable/disable switch respecting --no-color and test environments.
import * as c from 'colorette';

let enabled = true;
const isTest = !!process.env.JEST_WORKER_ID;

// In test environment disable colors unless FORCE_COLOR=1 explicitly set.
if (isTest) {
  enabled = process.env.FORCE_COLOR === '1';
}

export function setColorEnabled(v: boolean) {
  if (isTest && process.env.FORCE_COLOR !== '1') return; // force stay disabled in tests
  enabled = v;
  // colorette reads isColorSupported at import; soft-disable via wrappers
}

function wrap<T extends (str: string) => string>(fn: T) {
  return (s: string) => (enabled ? fn(s) : s);
}

export const color = {
  green: wrap(c.green),
  yellow: wrap(c.yellow),
  blue: wrap(c.blue),
  magenta: wrap(c.magenta),
  cyan: wrap(c.cyan),
  red: wrap(c.red),
  dim: wrap(c.dim),
  bold: wrap(c.bold),
};

export function heading(text: string): string {
  return color.bold(color.cyan(text));
}

export function subtle(text: string): string {
  return color.dim(text);
}

export function success(text: string): string {
  return color.green(text);
}

export function warn(text: string): string {
  return color.yellow(text);
}

export function failure(text: string): string {
  return color.red(text);
}
