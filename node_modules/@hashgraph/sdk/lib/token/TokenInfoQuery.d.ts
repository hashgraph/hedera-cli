/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.ITokenInfo} HashgraphProto.proto.ITokenInfo
 * @typedef {import("@hashgraph/proto").proto.ITokenGetInfoQuery} HashgraphProto.proto.ITokenGetInfoQuery
 * @typedef {import("@hashgraph/proto").proto.ITokenGetInfoResponse} HashgraphProto.proto.ITokenGetInfoResponse
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 * @typedef {import("../account/AccountId.js").default} AccountId
 */
/**
 * @augments {Query<TokenInfo>}
 */
export default class TokenInfoQuery extends Query<TokenInfo> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {TokenInfoQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): TokenInfoQuery;
    /**
     * @param {object} properties
     * @param {TokenId | string} [properties.tokenId]
     */
    constructor(properties?: {
        tokenId?: string | TokenId | undefined;
    });
    /**
     * @private
     * @type {?TokenId}
     */
    private _tokenId;
    /**
     * @returns {?TokenId}
     */
    get tokenId(): TokenId | null;
    /**
     * Set the token ID for which the info is being requested.
     *
     * @param {TokenId | string} tokenId
     * @returns {TokenInfoQuery}
     */
    setTokenId(tokenId: TokenId | string): TokenInfoQuery;
}
export namespace HashgraphProto {
    namespace proto {
        type IQuery = import("@hashgraph/proto").proto.IQuery;
        type IQueryHeader = import("@hashgraph/proto").proto.IQueryHeader;
        type IResponse = import("@hashgraph/proto").proto.IResponse;
        type IResponseHeader = import("@hashgraph/proto").proto.IResponseHeader;
        type ITokenInfo = import("@hashgraph/proto").proto.ITokenInfo;
        type ITokenGetInfoQuery = import("@hashgraph/proto").proto.ITokenGetInfoQuery;
        type ITokenGetInfoResponse = import("@hashgraph/proto").proto.ITokenGetInfoResponse;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
export type AccountId = import("../account/AccountId.js").default;
import TokenInfo from "./TokenInfo.js";
import Query from "../query/Query.js";
import TokenId from "./TokenId.js";
