/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.ITransaction} HashgraphProto.proto.ITransaction
 * @typedef {import("@hashgraph/proto").proto.ISignedTransaction} HashgraphProto.proto.ISignedTransaction
 * @typedef {import("@hashgraph/proto").proto.TransactionBody} HashgraphProto.proto.TransactionBody
 * @typedef {import("@hashgraph/proto").proto.ITransactionBody} HashgraphProto.proto.ITransactionBody
 * @typedef {import("@hashgraph/proto").proto.ITransactionResponse} HashgraphProto.proto.ITransactionResponse
 * @typedef {import("@hashgraph/proto").proto.ICryptoAddLiveHashTransactionBody} HashgraphProto.proto.ICryptoAddLiveHashTransactionBody
 * @typedef {import("@hashgraph/proto").proto.ILiveHash} HashgraphProto.proto.ILiveHash
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 * @typedef {import("../transaction/TransactionId.js").default} TransactionId
 */
export default class LiveHashAddTransaction extends Transaction {
    /**
     * @internal
     * @param {HashgraphProto.proto.ITransaction[]} transactions
     * @param {HashgraphProto.proto.ISignedTransaction[]} signedTransactions
     * @param {TransactionId[]} transactionIds
     * @param {AccountId[]} nodeIds
     * @param {HashgraphProto.proto.ITransactionBody[]} bodies
     * @returns {LiveHashAddTransaction}
     */
    static _fromProtobuf(transactions: HashgraphProto.proto.ITransaction[], signedTransactions: HashgraphProto.proto.ISignedTransaction[], transactionIds: TransactionId[], nodeIds: AccountId[], bodies: HashgraphProto.proto.ITransactionBody[]): LiveHashAddTransaction;
    /**
     * @param {object} [props]
     * @param {Uint8Array} [props.hash]
     * @param {Key[]} [props.keys]
     * @param {Duration | Long | number} [props.duration]
     * @param {AccountId | string} [props.accountId]
     */
    constructor(props?: {
        hash?: Uint8Array | undefined;
        keys?: Key[] | undefined;
        duration?: number | import("long").Long | Duration | undefined;
        accountId?: string | AccountId | undefined;
    } | undefined);
    /**
     * @private
     * @type {?Uint8Array}
     */
    private _hash;
    /**
     * @private
     * @type {?Key[]}
     */
    private _keys;
    /**
     * @private
     * @type {?Duration}
     */
    private _duration;
    /**
     * @private
     * @type {?AccountId}
     */
    private _accountId;
    /**
     * @returns {?Uint8Array}
     */
    get hash(): Uint8Array | null;
    /**
     * @param {Uint8Array} hash
     * @returns {LiveHashAddTransaction}
     */
    setHash(hash: Uint8Array): LiveHashAddTransaction;
    /**
     * @returns {?Key[]}
     */
    get keys(): Key[] | null;
    /**
     * @param {Key[] | KeyList} keys
     * @returns {LiveHashAddTransaction}
     */
    setKeys(keys: Key[] | KeyList): LiveHashAddTransaction;
    /**
     * @returns {?Duration}
     */
    get duration(): Duration | null;
    /**
     * @param {Duration | Long | number} duration
     * @returns {LiveHashAddTransaction}
     */
    setDuration(duration: Duration | Long | number): LiveHashAddTransaction;
    /**
     * @returns {?AccountId}
     */
    get accountId(): AccountId | null;
    /**
     * @param {AccountId | string} accountId
     * @returns {LiveHashAddTransaction}
     */
    setAccountId(accountId: AccountId | string): LiveHashAddTransaction;
    /**
     * @override
     * @protected
     * @returns {HashgraphProto.proto.ICryptoAddLiveHashTransactionBody}
     */
    protected override _makeTransactionData(): HashgraphProto.proto.ICryptoAddLiveHashTransactionBody;
}
export namespace HashgraphProto {
    namespace proto {
        type ITransaction = import("@hashgraph/proto").proto.ITransaction;
        type ISignedTransaction = import("@hashgraph/proto").proto.ISignedTransaction;
        type TransactionBody = import("@hashgraph/proto").proto.TransactionBody;
        type ITransactionBody = import("@hashgraph/proto").proto.ITransactionBody;
        type ITransactionResponse = import("@hashgraph/proto").proto.ITransactionResponse;
        type ICryptoAddLiveHashTransactionBody = import("@hashgraph/proto").proto.ICryptoAddLiveHashTransactionBody;
        type ILiveHash = import("@hashgraph/proto").proto.ILiveHash;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
export type TransactionId = import("../transaction/TransactionId.js").default;
import Transaction from "../transaction/Transaction.js";
import Key from "../Key.js";
import KeyList from "../KeyList.js";
import Duration from "../Duration.js";
import AccountId from "./AccountId.js";
