/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IProxyStaker} HashgraphProto.proto.IProxyStaker
 * @typedef {import("@hashgraph/proto").proto.IAccountID} HashgraphProto.proto.IAccountID
 */
/**
 * @typedef {import("bignumber.js").default} BigNumber
 */
/**
 * An account, and the amount that it sends or receives during a cryptocurrency transfer.
 */
export default class ProxyStaker {
    /**
     * @internal
     * @param {HashgraphProto.proto.IProxyStaker} transfer
     * @returns {ProxyStaker}
     */
    static _fromProtobuf(transfer: HashgraphProto.proto.IProxyStaker): ProxyStaker;
    /**
     * @private
     * @param {object} props
     * @param {AccountId} props.accountId
     * @param {number | string | Long | BigNumber | Hbar} props.amount
     */
    private constructor();
    /**
     * The Account ID that sends or receives cryptocurrency.
     *
     * @readonly
     */
    readonly accountId: AccountId;
    /**
     * The amount of tinybars that the account sends(negative)
     * or receives(positive).
     *
     * @readonly
     */
    readonly amount: Hbar;
    /**
     * @internal
     * @returns {HashgraphProto.proto.IProxyStaker}
     */
    _toProtobuf(): HashgraphProto.proto.IProxyStaker;
}
export namespace HashgraphProto {
    namespace proto {
        type IProxyStaker = import("@hashgraph/proto").proto.IProxyStaker;
        type IAccountID = import("@hashgraph/proto").proto.IAccountID;
    }
}
export type BigNumber = import("bignumber.js").default;
import AccountId from "./AccountId.js";
import Hbar from "../Hbar.js";
