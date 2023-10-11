/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.FreezeType} HashgraphProto.proto.FreezeType
 */
declare class FreezeType {
    /**
     * @internal
     * @param {number} code
     * @returns {FreezeType}
     */
    static _fromCode(code: number): FreezeType;
    /**
     * @hideconstructor
     * @internal
     * @param {number} code
     */
    constructor(code: number);
    /** @readonly */
    readonly _code: number;
    /**
     * @returns {string}
     */
    toString(): string;
    /**
     * @returns {HashgraphProto.proto.FreezeType}
     */
    valueOf(): HashgraphProto.proto.FreezeType;
}
declare namespace FreezeType {
    const UnknownFreezeType: FreezeType;
    const FreezeOnly: FreezeType;
    const PrepareUpgrade: FreezeType;
    const FreezeUpgrade: FreezeType;
    const FreezeAbort: FreezeType;
    const TelemetryUpgrade: FreezeType;
}
export default FreezeType;
export namespace HashgraphProto {
    namespace proto {
        type FreezeType = import("@hashgraph/proto").proto.FreezeType;
    }
}
