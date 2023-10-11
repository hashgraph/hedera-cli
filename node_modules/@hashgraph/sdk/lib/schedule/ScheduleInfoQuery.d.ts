/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IQuery} HashgraphProto.proto.IQuery
 * @typedef {import("@hashgraph/proto").proto.IQueryHeader} HashgraphProto.proto.IQueryHeader
 * @typedef {import("@hashgraph/proto").proto.IResponse} HashgraphProto.proto.IResponse
 * @typedef {import("@hashgraph/proto").proto.IResponseHeader} HashgraphProto.proto.IResponseHeader
 * @typedef {import("@hashgraph/proto").proto.IScheduleInfo} HashgraphProto.proto.IScheduleInfo
 * @typedef {import("@hashgraph/proto").proto.IScheduleGetInfoQuery} HashgraphProto.proto.IScheduleGetInfoQuery
 * @typedef {import("@hashgraph/proto").proto.IScheduleGetInfoResponse} HashgraphProto.proto.IScheduleGetInfoResponse
 */
/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("../client/Client.js").default<*, *>} Client
 * @typedef {import("../account/AccountId.js").default} AccountId
 */
/**
 * @augments {Query<ScheduleInfo>}
 */
export default class ScheduleInfoQuery extends Query<ScheduleInfo> {
    /**
     * @internal
     * @param {HashgraphProto.proto.IQuery} query
     * @returns {ScheduleInfoQuery}
     */
    static _fromProtobuf(query: HashgraphProto.proto.IQuery): ScheduleInfoQuery;
    /**
     * @param {object} properties
     * @param {ScheduleId | string} [properties.scheduleId]
     */
    constructor(properties?: {
        scheduleId?: string | ScheduleId | undefined;
    });
    /**
     * @private
     * @type {?ScheduleId}
     */
    private _scheduleId;
    /**
     * @returns {?ScheduleId}
     */
    get scheduleId(): ScheduleId | null;
    /**
     *
     * @param {ScheduleId | string} scheduleId
     * @returns {ScheduleInfoQuery}
     */
    setScheduleId(scheduleId: ScheduleId | string): ScheduleInfoQuery;
}
export namespace HashgraphProto {
    namespace proto {
        type IQuery = import("@hashgraph/proto").proto.IQuery;
        type IQueryHeader = import("@hashgraph/proto").proto.IQueryHeader;
        type IResponse = import("@hashgraph/proto").proto.IResponse;
        type IResponseHeader = import("@hashgraph/proto").proto.IResponseHeader;
        type IScheduleInfo = import("@hashgraph/proto").proto.IScheduleInfo;
        type IScheduleGetInfoQuery = import("@hashgraph/proto").proto.IScheduleGetInfoQuery;
        type IScheduleGetInfoResponse = import("@hashgraph/proto").proto.IScheduleGetInfoResponse;
    }
}
export type Channel = import("../channel/Channel.js").default;
export type Client = import("../client/Client.js").default<any, any>;
export type AccountId = import("../account/AccountId.js").default;
import ScheduleInfo from "./ScheduleInfo.js";
import Query from "../query/Query.js";
import ScheduleId from "./ScheduleId.js";
