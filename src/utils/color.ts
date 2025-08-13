// Simple color wrapper (can be disabled globally)
// Using ANSI codes directly to avoid extra dependency
const codes = {
  reset: '\u001b[0m',
  dim: '\u001b[2m',
  green: '\u001b[32m',
  yellow: '\u001b[33m',
  blue: '\u001b[34m',
  magenta: '\u001b[35m',
  cyan: '\u001b[36m',
  red: '\u001b[31m',
};

let enabled = true;
export function setColorEnabled(v: boolean) {
  enabled = v;
}
function wrap(color: keyof typeof codes, text: string) {
  return enabled ? codes[color] + text + codes.reset : text;
}
export const color = {
  green: (s: string) => wrap('green', s),
  yellow: (s: string) => wrap('yellow', s),
  blue: (s: string) => wrap('blue', s),
  magenta: (s: string) => wrap('magenta', s),
  cyan: (s: string) => wrap('cyan', s),
  red: (s: string) => wrap('red', s),
  dim: (s: string) => wrap('dim', s),
};
