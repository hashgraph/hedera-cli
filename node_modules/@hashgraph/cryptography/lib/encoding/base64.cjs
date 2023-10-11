"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.decode = decode;
exports.encode = encode;
/**
 * @param {string} text
 * @returns {Uint8Array}
 */
function decode(text) {
  return Buffer.from(text, "base64");
}

/**
 * @param {Uint8Array} data
 * @returns {string};
 */
function encode(data) {
  return Buffer.from(data).toString("base64");
}