/**
 * @typedef {import("./PublicKey.js").default} PublicKey
 * @typedef {import("./account/AccountId.js").default} AccountId
 */
export default class SignerSignature {
    /**
     * @param {object} props
     * @param {PublicKey} props.publicKey
     * @param {Uint8Array} props.signature
     * @param {AccountId} props.accountId
     */
    constructor(props: {
        publicKey: PublicKey;
        signature: Uint8Array;
        accountId: AccountId;
    });
    publicKey: import("./PublicKey.js").default;
    signature: Uint8Array;
    accountId: import("./account/AccountId.js").default;
}
export type PublicKey = import("./PublicKey.js").default;
export type AccountId = import("./account/AccountId.js").default;
