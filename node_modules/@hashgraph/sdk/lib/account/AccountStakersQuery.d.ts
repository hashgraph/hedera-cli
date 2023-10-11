/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.ICryptoGetStakersQuery} HashgraphProto.proto.ICryptoGetStakersQuery
 * @typedef {import("@hashgraph/proto").proto.ICryptoGetStakersResponse} HashgraphProto.proto.ICryptoGetStakersResponse
 * @typedef {import("@hashgraph/proto").proto.IAllProxyStakers} HashgraphProto.proto.IAllProxyStakers
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 */
/**
 * Get all the accounts that are proxy staking to this account.
 * For each of them, give the amount currently staked.
 *
 * This is not yet implemented, but will be in a future version of the API.
 *
 * @augments {Query<ProxyStaker[]>}
 */
export default class AccountStakersQuery extends Query<ProxyStaker[]> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {AccountStakersQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): AccountStakersQuery;
    /**
     * @param {object} [props]
     * @param {(AccountId | string)=} props.accountId
     */
    constructor(props?: {
        accountId?: (AccountId | string) | undefined;
    } | undefined);
    /**
     * @type {?AccountId}
     * @private
     */
    private _accountId;
    /**
     * @returns {?AccountId}
     */
    get accountId(): AccountId | null;
    /**
     * Set the account ID for which the stakers are being requested.
     *
     * @param {AccountId | string} accountId
     * @returns {this}
     */
    setAccountId(accountId: AccountId | string): this;
    /**
     * @protected
     * @override
     * @param {HashgraphProto.proto.IResponse} response
     * @returns {Promise<ProxyStaker[]>}
     */
    protected override _mapResponse(response: HashgraphProto.proto.IResponse): Promise<ProxyStaker[]>;
}
export namespace HashgraphProto {
    namespace proto {
        type IQuery = import("@hashgraph/proto").proto.IQuery;
        type IQueryHeader = import("@hashgraph/proto").proto.IQueryHeader;
        type IResponse = import("@hashgraph/proto").proto.IResponse;
        type IResponseHeader = import("@hashgraph/proto").proto.IResponseHeader;
        type ICryptoGetStakersQuery = import("@hashgraph/proto").proto.ICryptoGetStakersQuery;
        type ICryptoGetStakersResponse = import("@hashgraph/proto").proto.ICryptoGetStakersResponse;
        type IAllProxyStakers = import("@hashgraph/proto").proto.IAllProxyStakers;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
import ProxyStaker from "./ProxyStaker.js";
import Query from "../query/Query.js";
import AccountId from "./AccountId.js";
