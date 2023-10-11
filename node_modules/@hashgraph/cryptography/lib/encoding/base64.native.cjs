"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.decode = decode;
exports.encode = encode;
var _buffer = require("buffer");
/**
 * @param {string} text
 * @returns {Uint8Array}
 */
function decode(text) {
  return Uint8Array.from(_buffer.Buffer.from(text, "base64"));
}

/**
 * @param {Uint8Array} data
 * @returns {string};
 */
function encode(data) {
  return _buffer.Buffer.from(data).toString("base64");
}