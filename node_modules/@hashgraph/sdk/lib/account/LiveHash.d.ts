/**
 * @namespace proto
 * @typedef {import("@hashgraph/proto").proto.IAccountID} HashgraphProto.proto.IAccountID
 * @typedef {import("@hashgraph/proto").proto.ILiveHash} HashgraphProto.proto.ILiveHash
 * @typedef {import("@hashgraph/proto").proto.IDuration} HashgraphProto.proto.IDuration
 */
/**
 * Response when the client sends the node CryptoGetInfoQuery.
 */
export default class LiveHash {
    /**
     * @internal
     * @param {HashgraphProto.proto.ILiveHash} liveHash
     * @returns {LiveHash}
     */
    static _fromProtobuf(liveHash: HashgraphProto.proto.ILiveHash): LiveHash;
    /**
     * @private
     * @param {object} props
     * @param {AccountId} props.accountId
     * @param {Uint8Array} props.hash
     * @param {KeyList} props.keys
     * @param {Duration} props.duration
     */
    private constructor();
    /** @readonly */
    readonly accountId: AccountId;
    /** @readonly */
    readonly hash: Uint8Array;
    /** @readonly */
    readonly keys: KeyList;
    /** @readonly */
    readonly duration: Duration;
    /**
     * @internal
     * @returns {HashgraphProto.proto.ILiveHash}
     */
    _toProtobuf(): HashgraphProto.proto.ILiveHash;
}
export namespace HashgraphProto {
    namespace proto {
        type IAccountID = import("@hashgraph/proto").proto.IAccountID;
        type ILiveHash = import("@hashgraph/proto").proto.ILiveHash;
        type IDuration = import("@hashgraph/proto").proto.IDuration;
    }
}
import AccountId from "./AccountId.js";
import KeyList from "../KeyList.js";
import Duration from "../Duration.js";
