/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.IFileGetInfoQuery} HashgraphProto.proto.IFileGetInfoQuery
 * @typedef {import("@hashgraph/proto").proto.IFileGetInfoResponse} HashgraphProto.proto.IFileGetInfoResponse
 * @typedef {import("@hashgraph/proto").proto.FileGetInfoResponse.IFileInfo} HashgraphProto.proto.IFileInfo
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 * @typedef {import("../account/AccountId.js").default} AccountId
 */
/**
 * @augments {Query<FileInfo>}
 */
export default class FileInfoQuery extends Query<FileInfo> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {FileInfoQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): FileInfoQuery;
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
     * @returns {FileInfoQuery}
     */
    setFileId(fileId: FileId | string): FileInfoQuery;
}
export namespace HashgraphProto {
    namespace proto {
        type IQuery = import("@hashgraph/proto").proto.IQuery;
        type IQueryHeader = import("@hashgraph/proto").proto.IQueryHeader;
        type IResponse = import("@hashgraph/proto").proto.IResponse;
        type IResponseHeader = import("@hashgraph/proto").proto.IResponseHeader;
        type IFileGetInfoQuery = import("@hashgraph/proto").proto.IFileGetInfoQuery;
        type IFileGetInfoResponse = import("@hashgraph/proto").proto.IFileGetInfoResponse;
        type IFileInfo = import("@hashgraph/proto").proto.FileGetInfoResponse.IFileInfo;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
export type AccountId = import("../account/AccountId.js").default;
import FileInfo from "./FileInfo.js";
import Query from "../query/Query.js";
import FileId from "./FileId.js";
