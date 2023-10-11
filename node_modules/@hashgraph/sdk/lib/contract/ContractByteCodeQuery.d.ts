/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.IContractGetBytecodeQuery} HashgraphProto.proto.IContractGetBytecodeQuery
 * @typedef {import("@hashgraph/proto").proto.IContractGetBytecodeResponse} HashgraphProto.proto.IContractGetBytecodeResponse
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 * @typedef {import("../account/AccountId.js").default} AccountId
 */
/**
 * @augments {Query<Uint8Array>}
 */
export default class ContractByteCodeQuery extends Query<Uint8Array> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {ContractByteCodeQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): ContractByteCodeQuery;
    /**
     * @param {object} props
     * @param {ContractId | string} [props.contractId]
     */
    constructor(props?: {
        contractId?: string | ContractId | undefined;
    });
    /**
     * @type {?ContractId}
     * @private
     */
    private _contractId;
    /**
     * @returns {?ContractId}
     */
    get contractId(): ContractId | null;
    /**
     * Set the contract ID for which the info is being requested.
     *
     * @param {ContractId | string} contractId
     * @returns {ContractByteCodeQuery}
     */
    setContractId(contractId: ContractId | string): ContractByteCodeQuery;
    /**
     * @protected
     * @override
     * @param {HashgraphProto.proto.IResponse} response
     * @returns {Promise<Uint8Array>}
     */
    protected override _mapResponse(response: HashgraphProto.proto.IResponse): Promise<Uint8Array>;
}
export namespace HashgraphProto {
    namespace proto {
        type IQuery = import("@hashgraph/proto").proto.IQuery;
        type IQueryHeader = import("@hashgraph/proto").proto.IQueryHeader;
        type IResponse = import("@hashgraph/proto").proto.IResponse;
        type IResponseHeader = import("@hashgraph/proto").proto.IResponseHeader;
        type IContractGetBytecodeQuery = import("@hashgraph/proto").proto.IContractGetBytecodeQuery;
        type IContractGetBytecodeResponse = import("@hashgraph/proto").proto.IContractGetBytecodeResponse;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
export type AccountId = import("../account/AccountId.js").default;
import Query from "../query/Query.js";
import ContractId from "./ContractId.js";
