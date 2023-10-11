/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.ICryptoGetLiveHashQuery} HashgraphProto.proto.ICryptoGetLiveHashQuery
 * @typedef {import("@hashgraph/proto").proto.ICryptoGetLiveHashResponse} HashgraphProto.proto.ICryptoGetLiveHashResponse
 * @typedef {import("@hashgraph/proto").proto.ILiveHash} HashgraphProto.proto.ILiveHash
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 */
/**
 * @augments {Query<LiveHash>}
 */
export default class LiveHashQuery extends Query<LiveHash> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {LiveHashQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): LiveHashQuery;
    /**
     * @param {object} [props]
     * @param {AccountId | string} [props.accountId]
     * @param {Uint8Array} [props.hash]
     */
    constructor(props?: {
        accountId?: string | AccountId | undefined;
        hash?: Uint8Array | undefined;
    } | undefined);
    /**
     * @type {?AccountId}
     * @private
     */
    private _accountId;
    /**
     * @type {?Uint8Array}
     * @private
     */
    private _hash;
    /**
     * @returns {?AccountId}
     */
    get accountId(): AccountId | null;
    /**
     * Set the account to which the livehash is associated.
     *
     * @param {AccountId | string} accountId
     * @returns {this}
     */
    setAccountId(accountId: AccountId | string): this;
    /**
     * @returns {?Uint8Array}
     */
    get liveHash(): Uint8Array | null;
    /**
     * Set the SHA-384 data in the livehash.
     *
     * @param {Uint8Array} hash
     * @returns {this}
     */
    setHash(hash: Uint8Array): this;
    /**
     * @protected
     * @override
     * @param {HashgraphProto.proto.IResponse} response
     * @returns {Promise<LiveHash>}
     */
    protected override _mapResponse(response: HashgraphProto.proto.IResponse): Promise<LiveHash>;
}
export namespace HashgraphProto {
    namespace proto {
        type IQuery = import("@hashgraph/proto").proto.IQuery;
        type IQueryHeader = import("@hashgraph/proto").proto.IQueryHeader;
        type IResponse = import("@hashgraph/proto").proto.IResponse;
        type IResponseHeader = import("@hashgraph/proto").proto.IResponseHeader;
        type ICryptoGetLiveHashQuery = import("@hashgraph/proto").proto.ICryptoGetLiveHashQuery;
        type ICryptoGetLiveHashResponse = import("@hashgraph/proto").proto.ICryptoGetLiveHashResponse;
        type ILiveHash = import("@hashgraph/proto").proto.ILiveHash;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
import LiveHash from "./LiveHash.js";
import Query from "../query/Query.js";
import AccountId from "./AccountId.js";
