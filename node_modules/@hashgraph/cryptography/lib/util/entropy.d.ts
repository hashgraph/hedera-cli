/**
 * @param {string[]} words
 * @param {string[]} wordlist
 * @returns {[Uint8Array, number]}
 */
export function legacy1(words: string[], wordlist: string[]): [Uint8Array, number];
/**
 * @param {string[]} words
 * @param {string[]} wordlist
 * @returns {Promise<Uint8Array>}
 */
export function legacy2(words: string[], wordlist: string[]): Promise<Uint8Array>;
/**
 * @param {Uint8Array} data
 * @returns {number}
 */
export function crc8(data: Uint8Array): number;
/**
 * @param {number[]} nums
 * @param {number} fromRadix
 * @param {number} toRadix
 * @param {number} toLength
 * @returns {Uint8Array}
 */
export function convertRadix(nums: number[], fromRadix: number, toRadix: number, toLength: number): Uint8Array;
/**
 * @param {Uint8Array} data
 * @returns {boolean[]}
 */
export function bytesToBits(data: Uint8Array): boolean[];
