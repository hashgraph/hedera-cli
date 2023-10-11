/**
 * @param {HashAlgorithm} algorithm
 * @param {Uint8Array | string} password
 * @param {Uint8Array | string} salt
 * @param {number} iterations
 * @param {number} length
 * @returns {Promise<Uint8Array>}
 */
export function deriveKey(algorithm: HashAlgorithm, password: Uint8Array | string, salt: Uint8Array | string, iterations: number, length: number): Promise<Uint8Array>;
import { HashAlgorithm } from "./hmac.js";
