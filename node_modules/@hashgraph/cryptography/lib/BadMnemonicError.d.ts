export default class BadMnemonicError extends Error {
    /**
     * @param {Mnemonic} mnemonic
     * @param {string} reason
     * @param {number[]} unknownWordIndices
     * @hideconstructor
     */
    constructor(mnemonic: Mnemonic, reason: string, unknownWordIndices: number[]);
    /** The reason for which the mnemonic failed validation. */
    reason: "BadLength" | "UnknownWords" | "ChecksumMismatch";
    /** The mnemonic that failed validation. */
    mnemonic: import("./Mnemonic.js").default;
    /**
     * The indices in the mnemonic that were not found in the BIP-39
     * standard English word list.
     */
    unknownWordIndices: number[];
}
export type Mnemonic = import("./Mnemonic.js").default;
