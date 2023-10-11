/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.ITokenRelationship} HashgraphProto.proto.ITokenRelationship
 * @typedef {import("@hashgraph/proto").proto.ITokenID} HashgraphProto.proto.ITokenID
 */
/**
 * @typedef {import("long")} Long
 */
/**
 * @augments {ObjectMap<TokenId, TokenRelationship>}
 */
export default class TokenRelationshipMap extends ObjectMap<TokenId, TokenRelationship> {
    /**
     * @param {HashgraphProto.proto.ITokenRelationship[]} relationships
     * @returns {TokenRelationshipMap}
     */
    static _fromProtobuf(relationships: HashgraphProto.proto.ITokenRelationship[]): TokenRelationshipMap;
    constructor();
    /**
     * @returns {HashgraphProto.proto.ITokenRelationship[]}
     */
    _toProtobuf(): HashgraphProto.proto.ITokenRelationship[];
}
export namespace HashgraphProto {
    namespace proto {
        type ITokenRelationship = import("@hashgraph/proto").proto.ITokenRelationship;
        type ITokenID = import("@hashgraph/proto").proto.ITokenID;
    }
}
export type Long = import("long");
import TokenId from "../token/TokenId.js";
import TokenRelationship from "./TokenRelationship.js";
import ObjectMap from "../ObjectMap.js";
