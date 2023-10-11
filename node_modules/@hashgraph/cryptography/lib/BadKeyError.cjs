"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
/**
 * Signals that a key could not be realized from the input.
 */
class BadKeyError extends Error {
  /**
   * @param {Error | string} messageOrCause
   */
  constructor(messageOrCause) {
    super(messageOrCause instanceof Error ? messageOrCause.message : messageOrCause);
    this.name = "BadKeyError";
    if (messageOrCause instanceof Error) {
      /** @type {Error=} */
      this.cause = messageOrCause;
      this.stack = messageOrCause.stack;
    }
  }
}
exports.default = BadKeyError;