/**
 * An public key on the Hedera™ network.
 */
export default class Ed25519PublicKey extends Key {
    /**
     * @param {Uint8Array} data
     * @returns {Ed25519PublicKey}
     */
    static fromBytes(data: Uint8Array): Ed25519PublicKey;
    /**
     * @param {Uint8Array} data
     * @returns {Ed25519PublicKey}
     */
    static fromBytesDer(data: Uint8Array): Ed25519PublicKey;
    /**
     * @param {Uint8Array} data
     * @returns {Ed25519PublicKey}
     */
    static fromBytesRaw(data: Uint8Array): Ed25519PublicKey;
    /**
     * Parse a public key from a string of hexadecimal digits.
     *
     * The public key may optionally be prefixed with
     * the DER header.
     * @param {string} text
     * @returns {Ed25519PublicKey}
     */
    static fromString(text: string): Ed25519PublicKey;
    /**
     * @internal
     * @hideconstructor
     * @param {Uint8Array} keyData
     */
    constructor(keyData: Uint8Array);
    /**
     * @type {Uint8Array}
     * @private
     * @readonly
     */
    private readonly _keyData;
    /**
     * @returns {string}
     */
    get _type(): string;
    /**
     * Verify a signature on a message with this public key.
     * @param {Uint8Array} message
     * @param {Uint8Array} signature
     * @returns {boolean}
     */
    verify(message: Uint8Array, signature: Uint8Array): boolean;
    /**
     * @returns {Uint8Array}
     */
    toBytesDer(): Uint8Array;
    /**
     * @returns {Uint8Array}
     */
    toBytesRaw(): Uint8Array;
    /**
     * @param {Ed25519PublicKey} other
     * @returns {boolean}
     */
    equals(other: Ed25519PublicKey): boolean;
}
import Key from "./Key.js";
