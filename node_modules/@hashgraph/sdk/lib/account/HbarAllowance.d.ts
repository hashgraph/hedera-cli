/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IGrantedCryptoAllowance} HashgraphProto.proto.IGrantedCryptoAllowance
 * @typedef {import("@hashgraph/proto").proto.ICryptoAllowance} HashgraphProto.proto.ICryptoAllowance
 * @typedef {import("@hashgraph/proto").proto.IAccountID} HashgraphProto.proto.IAccountID
 */
/**
 * @typedef {import("long")} Long
 */
/**
 * @typedef {import("../client/Client.js").default<*, *>} Client
 */
export default class HbarAllowance {
    /**
     * @internal
     * @param {HashgraphProto.proto.ICryptoAllowance} allowance
     * @returns {HbarAllowance}
     */
    static _fromProtobuf(allowance: HashgraphProto.proto.ICryptoAllowance): HbarAllowance;
    /**
     * @internal
     * @param {HashgraphProto.proto.IGrantedCryptoAllowance} allowance
     * @param {AccountId} ownerAccountId
     * @returns {HbarAllowance}
     */
    static _fromGrantedProtobuf(allowance: HashgraphProto.proto.IGrantedCryptoAllowance, ownerAccountId: AccountId): HbarAllowance;
    /**
     * @internal
     * @param {object} props
     * @param {AccountId | null} props.spenderAccountId
     * @param {AccountId | null} props.ownerAccountId
     * @param {Hbar | null} props.amount
     */
    constructor(props: {
        spenderAccountId: AccountId | null;
        ownerAccountId: AccountId | null;
        amount: Hbar | null;
    });
    /**
     * The account ID of the hbar allowance spender.
     *
     * @readonly
     */
    readonly spenderAccountId: AccountId | null;
    /**
     * The account ID of the hbar allowance owner.
     *
     * @readonly
     */
    readonly ownerAccountId: AccountId | null;
    /**
     * The current balance of the spender's allowance in tinybars.
     *
     * @readonly
     */
    readonly amount: Hbar | null;
    /**
     * @internal
     * @returns {HashgraphProto.proto.ICryptoAllowance}
     */
    _toProtobuf(): HashgraphProto.proto.ICryptoAllowance;
    /**
     * @param {Client} client
     */
    _validateChecksums(client: import("../client/Client.js").default<any, any>): void;
    /**
     * @returns {object}
     */
    toJSON(): object;
}
export namespace HashgraphProto {
    namespace proto {
        type IGrantedCryptoAllowance = import("@hashgraph/proto").proto.IGrantedCryptoAllowance;
        type ICryptoAllowance = import("@hashgraph/proto").proto.ICryptoAllowance;
        type IAccountID = import("@hashgraph/proto").proto.IAccountID;
    }
}
export type Long = import("long");
export type Client = import("../client/Client.js").default<any, any>;
import AccountId from "./AccountId.js";
import Hbar from "../Hbar.js";
