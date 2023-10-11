/**
 * Mostly copied from https://github.com/bitcoinjs/bip32/blob/master/ts-src/bip32.ts
 * We cannot use that library directly because it uses `Buffer` and we want to avoid
 * polyfills as much as possible. Also, we only need the `derive` function.
 * @param {Uint8Array} parentKey
 * @param {Uint8Array} chainCode
 * @param {number} index
 * @returns {Promise<{ keyData: Uint8Array; chainCode: Uint8Array }>}
 */
export function derive(parentKey: Uint8Array, chainCode: Uint8Array, index: number): Promise<{
    keyData: Uint8Array;
    chainCode: Uint8Array;
}>;
/**
 * @param {Uint8Array} seed
 * @returns {Promise<{ keyData: Uint8Array; chainCode: Uint8Array }>}
 */
export function fromSeed(seed: Uint8Array): Promise<{
    keyData: Uint8Array;
    chainCode: Uint8Array;
}>;
/**
 * Harden the index
 * @param {number} index         the derivation index
 * @returns {number}              the hardened index
 */
export function toHardenedIndex(index: number): number;
/**
 * Check if the index is hardened
 * @param {number} index         the derivation index
 * @returns {boolean}            true if the index is hardened
 */
export function isHardenedIndex(index: number): boolean;
