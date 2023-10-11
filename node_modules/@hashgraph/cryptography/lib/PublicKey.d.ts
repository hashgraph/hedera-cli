/**
 * @typedef {import("./PrivateKey.js").Transaction} Transaction
 */
/**
 * An public key on the Hedera™ network.
 */
export default class PublicKey extends Key {
    /**
     * @param {Uint8Array} data
     * @returns {PublicKey}
     */
    static fromBytes(data: Uint8Array): PublicKey;
    /**
     * @param {Uint8Array} data
     * @returns {PublicKey}
     */
    static fromBytesED25519(data: Uint8Array): PublicKey;
    /**
     * @param {Uint8Array} data
     * @returns {PublicKey}
     */
    static fromBytesECDSA(data: Uint8Array): PublicKey;
    /**
     * Parse a public key from a string of hexadecimal digits.
     *
     * The public key may optionally be prefixed with
     * the DER header.
     * @param {string} text
     * @returns {PublicKey}
     */
    static fromString(text: string): PublicKey;
    /**
     * @param {string} text
     * @returns {PublicKey}
     */
    static fromStringED25519(text: string): PublicKey;
    /**
     * @param {string} text
     * @returns {PublicKey}
     */
    static fromStringECDSA(text: string): PublicKey;
    /**
     * @internal
     * @hideconstructor
     * @param {Ed25519PublicKey | EcdsaPublicKey} key
     */
    constructor(key: Ed25519PublicKey | EcdsaPublicKey);
    /**
     * @type {Ed25519PublicKey | EcdsaPublicKey}
     * @private
     * @readonly
     */
    private readonly _key;
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
     * @deprecated - use `@hashgraph/sdk`.PublicKey instead
     * @param {Transaction} transaction
     * @returns {boolean}
     */
    verifyTransaction(transaction: Transaction): boolean;
    /**
     * @returns {Uint8Array}
     */
    toBytes(): Uint8Array;
    /**
     * @returns {Uint8Array}
     */
    toBytesDer(): Uint8Array;
    /**
     * @returns {Uint8Array}
     */
    toBytesRaw(): Uint8Array;
    /**
     * @returns {string}
     */
    toStringDer(): string;
    /**
     * @returns {string}
     */
    toStringRaw(): string;
    /**
     * @returns {string}
     */
    toEthereumAddress(): string;
    /**
     * @param {PublicKey} other
     * @returns {boolean}
     */
    equals(other: PublicKey): boolean;
}
export type Transaction = import("./PrivateKey.js").Transaction;
import Key from "./Key.js";
import Ed25519PublicKey from "./Ed25519PublicKey.js";
import EcdsaPublicKey from "./EcdsaPublicKey.js";
