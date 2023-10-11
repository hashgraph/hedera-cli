/**
 * @typedef {object} AsnSeq
 * @property {AsnType[]} seq
 */
/**
 * @typedef {object} AsnInt
 * @property {number} int
 */
/**
 * @typedef {object} AsnBytes
 * @property {Uint8Array} bytes
 */
/**
 * @typedef {object} AsnIdent
 * @property {string} ident
 */
/**
 * @typedef {{}} AsnNull
 */
/**
 * @typedef {AsnSeq | AsnInt | AsnBytes | AsnIdent | AsnNull} AsnType
 */
/**
 * Note: may throw weird errors on malformed input. Catch and rethrow with, e.g. `BadKeyError`.
 *@param {Uint8Array} data
 *@returns {AsnType}
 */
export function decode(data: Uint8Array): AsnType;
export type AsnSeq = {
    seq: AsnType[];
};
export type AsnInt = {
    int: number;
};
export type AsnBytes = {
    bytes: Uint8Array;
};
export type AsnIdent = {
    ident: string;
};
export type AsnNull = {};
export type AsnType = AsnSeq | AsnInt | AsnBytes | AsnIdent | AsnNull;
