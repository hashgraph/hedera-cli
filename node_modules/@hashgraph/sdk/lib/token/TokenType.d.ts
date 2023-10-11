/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.TokenType} HashgraphProto.proto.TokenType
 */
declare class TokenType {
    /**
     * @internal
     * @param {number} code
     * @returns {TokenType}
     */
    static _fromCode(code: number): TokenType;
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
     * @returns {HashgraphProto.proto.TokenType}
     */
    valueOf(): HashgraphProto.proto.TokenType;
}
declare namespace TokenType {
    const FungibleCommon: TokenType;
    const NonFungibleUnique: TokenType;
}
export default TokenType;
export namespace HashgraphProto {
    namespace proto {
        type TokenType = import("@hashgraph/proto").proto.TokenType;
    }
}
