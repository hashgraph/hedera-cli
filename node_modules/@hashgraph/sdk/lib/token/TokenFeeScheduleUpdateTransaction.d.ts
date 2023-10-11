/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.ITransaction} HashgraphProto.proto.ITransaction
 * @typedef {import("@hashgraph/proto").proto.ISignedTransaction} HashgraphProto.proto.ISignedTransaction
 * @typedef {import("@hashgraph/proto").proto.TransactionBody} HashgraphProto.proto.TransactionBody
 * @typedef {import("@hashgraph/proto").proto.ITransactionBody} HashgraphProto.proto.ITransactionBody
 * @typedef {import("@hashgraph/proto").proto.ITransactionResponse} HashgraphProto.proto.ITransactionResponse
 * @typedef {import("@hashgraph/proto").proto.ITokenFeeScheduleUpdateTransactionBody} HashgraphProto.proto.ITokenFeeScheduleUpdateTransactionBody
 * @typedef {import("@hashgraph/proto").proto.ITokenID} HashgraphProto.proto.ITokenID
 */
/**
 * @typedef {import("bignumber.js").default} BigNumber
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../transaction/TransactionId.js").default} TransactionId
 * @typedef {import("./CustomFee.js").default} CustomFee
 * @typedef {import("../account/AccountId.js").default} AccountId
 */
/**
 * FeeScheduleUpdate a new Hedera™ crypto-currency token.
 */
export default class TokenFeeScheduleUpdateTransaction extends Transaction {
    /**
     * @internal
     * @param {HashgraphProto.proto.ITransaction[]} transactions
     * @param {HashgraphProto.proto.ISignedTransaction[]} signedTransactions
     * @param {TransactionId[]} transactionIds
     * @param {AccountId[]} nodeIds
     * @param {HashgraphProto.proto.ITransactionBody[]} bodies
     * @returns {TokenFeeScheduleUpdateTransaction}
     */
    static _fromProtobuf(transactions: HashgraphProto.proto.ITransaction[], signedTransactions: HashgraphProto.proto.ISignedTransaction[], transactionIds: TransactionId[], nodeIds: AccountId[], bodies: HashgraphProto.proto.ITransactionBody[]): TokenFeeScheduleUpdateTransaction;
    /**
     * @param {object} [props]
     * @param {TokenId | string} [props.tokenId]
     * @param {CustomFee[]} [props.customFees]
     */
    constructor(props?: {
        tokenId?: string | TokenId | undefined;
        customFees?: import("./CustomFee.js").default[] | undefined;
    } | undefined);
    /**
     * @private
     * @type {?TokenId}
     */
    private _tokenId;
    /**
     * @private
     * @type {CustomFee[]}
     */
    private _customFees;
    /**
     * @returns {?TokenId}
     */
    get tokenId(): TokenId | null;
    /**
     * @param {TokenId | string} tokenId
     * @returns {this}
     */
    setTokenId(tokenId: TokenId | string): this;
    /**
     * @returns {CustomFee[]}
     */
    get customFees(): import("./CustomFee.js").default[];
    /**
     * @param {CustomFee[]} fees
     * @returns {this}
     */
    setCustomFees(fees: CustomFee[]): this;
    /**
     * @override
     * @protected
     * @returns {HashgraphProto.proto.ITokenFeeScheduleUpdateTransactionBody}
     */
    protected override _makeTransactionData(): HashgraphProto.proto.ITokenFeeScheduleUpdateTransactionBody;
}
export namespace HashgraphProto {
    namespace proto {
        type ITransaction = import("@hashgraph/proto").proto.ITransaction;
        type ISignedTransaction = import("@hashgraph/proto").proto.ISignedTransaction;
        type TransactionBody = import("@hashgraph/proto").proto.TransactionBody;
        type ITransactionBody = import("@hashgraph/proto").proto.ITransactionBody;
        type ITransactionResponse = import("@hashgraph/proto").proto.ITransactionResponse;
        type ITokenFeeScheduleUpdateTransactionBody = import("@hashgraph/proto").proto.ITokenFeeScheduleUpdateTransactionBody;
        type ITokenID = import("@hashgraph/proto").proto.ITokenID;
    }
}
export type BigNumber = import("bignumber.js").default;
export type Channel = import("../channel/Channel.js").default;
export type TransactionId = import("../transaction/TransactionId.js").default;
export type CustomFee = import("./CustomFee.js").default;
export type AccountId = import("../account/AccountId.js").default;
import Transaction from "../transaction/Transaction.js";
import TokenId from "./TokenId.js";
