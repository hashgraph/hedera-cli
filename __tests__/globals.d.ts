// Global test-only declarations
// Ensures we only declare the availability flag once.

export {}; // make this a module so global augmentation works

declare global {
  // Flag set by availability probe; undefined means not yet checked.
  // true => localnet available, false => unavailable
  // eslint-disable-next-line no-var
  var __LOCALNET_AVAILABLE__: boolean | undefined;
}
