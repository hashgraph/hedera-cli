/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.IFileGetContentsQuery} HashgraphProto.proto.IFileGetContentsQuery
 * @typedef {import("@hashgraph/proto").proto.IFileGetContentsResponse} HashgraphProto.proto.IFileGetContentsResponse
 * @typedef {import("@hashgraph/proto").proto.FileGetContentsResponse.IFileContents} HashgraphProto.proto.FileGetContentsResponse.IFileContents
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 * @typedef {import("../account/AccountId.js").default} AccountId
 */
/**
 * @augments {Query<Uint8Array>}
 */
export default class FileContentsQuery extends Query<Uint8Array> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {FileContentsQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): FileContentsQuery;
    /**
     * @param {object} [props]
     * @param {FileId | string} [props.fileId]
     */
    constructor(props?: {
        fileId?: string | FileId | undefined;
    } | undefined);
    /**
     * @type {?FileId}
     * @private
     */
    private _fileId;
    /**
     * @returns {?FileId}
     */
    get fileId(): FileId | null;
    /**
     * Set the file ID for which the info is being requested.
     *
     * @param {FileId | string} fileId
     * @returns {FileContentsQuery}
     */
    setFileId(fileId: FileId | string): FileContentsQuery;
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
        type IFileGetContentsQuery = import("@hashgraph/proto").proto.IFileGetContentsQuery;
        type IFileGetContentsResponse = import("@hashgraph/proto").proto.IFileGetContentsResponse;
        namespace FileGetContentsResponse {
            type IFileContents = import("@hashgraph/proto").proto.FileGetContentsResponse.IFileContents;
        }
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
export type AccountId = import("../account/AccountId.js").default;
import Query from "../query/Query.js";
import FileId from "./FileId.js";
