/**
 * @augments {ObjectMap<AccountId, NodeAccountIdSignatureMap>}
 */
export default class SignatureMap extends ObjectMap<AccountId, NodeAccountIdSignatureMap> {
    /**
     * @param {import("./Transaction.js").default} transaction
     * @returns {SignatureMap}
     */
    static _fromTransaction(transaction: import("./Transaction.js").default): SignatureMap;
    constructor();
}
import AccountId from "../account/AccountId.js";
import NodeAccountIdSignatureMap from "./NodeAccountIdSignatureMap.js";
import ObjectMap from "../ObjectMap.js";
