export class AlgorithmIdentifier {
    /**
     * @param {import("../encoding/der.js").AsnType} asn
     */
    constructor(asn: import("../encoding/der.js").AsnType);
    /**
     * @type {string}
     */
    algIdent: string;
    /**
     * @type {import("../encoding/der.js").AsnType | undefined}
     */
    parameters: import("../encoding/der.js").AsnType | undefined;
    /**
     * @returns {string}
     */
    toString(): string;
}
export class PrivateKeyInfo {
    /**
     * @param {Uint8Array} encoded
     * @returns {PrivateKeyInfo}
     */
    static parse(encoded: Uint8Array): PrivateKeyInfo;
    /**
     * @param {import("../encoding/der.js").AsnType} asn
     */
    constructor(asn: import("../encoding/der.js").AsnType);
    /**
     * @type {number}
     */
    version: number;
    /**
     * @type {AlgorithmIdentifier}
     */
    algId: AlgorithmIdentifier;
    /**
     * @type {Uint8Array}
     */
    privateKey: Uint8Array;
}
export class EncryptedPrivateKeyInfo {
    /**
     * @param {Uint8Array} encoded
     * @returns {EncryptedPrivateKeyInfo}
     */
    static parse(encoded: Uint8Array): EncryptedPrivateKeyInfo;
    /**
     * @param {import("../encoding/der.js").AsnType} asn
     */
    constructor(asn: import("../encoding/der.js").AsnType);
    /**
     * @type {AlgorithmIdentifier}
     */
    algId: AlgorithmIdentifier;
    /**
     * @type {Uint8Array}
     */
    data: Uint8Array;
    /**
     * @param {string} passphrase
     * @returns {Promise<PrivateKeyInfo>}
     */
    decrypt(passphrase: string): Promise<PrivateKeyInfo>;
}
