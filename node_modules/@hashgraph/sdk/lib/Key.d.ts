/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IKey} HashgraphProto.proto.IKey
 */
export default class Key {
    /**
     * @internal
     * @param {HashgraphProto.proto.IKey} key
     * @returns {Key}
     */
    static _fromProtobufKey(key: HashgraphProto.proto.IKey): Key;
    /**
     * @internal
     * @abstract
     * @returns {HashgraphProto.proto.IKey}
     */
    _toProtobufKey(): HashgraphProto.proto.IKey;
}
export namespace HashgraphProto {
    namespace proto {
        type IKey = import("@hashgraph/proto").proto.IKey;
    }
}
