/**
 * @typedef {import("long").Long} Long
 * @typedef {import("../client/Client.js").default<*, *>} Client
 */
/**
 * Unique identifier for a topic (used by the consensus service).
 */
export default class TopicId {
    /**
     * @param {string} text
     * @returns {TopicId}
     */
    static fromString(text: string): TopicId;
    /**
     * @internal
     * @param {HashgraphProto.proto.ITopicID} id
     * @returns {TopicId}
     */
    static _fromProtobuf(id: HashgraphProto.proto.ITopicID): TopicId;
    /**
     * @param {Uint8Array} bytes
     * @returns {TopicId}
     */
    static fromBytes(bytes: Uint8Array): TopicId;
    /**
     * @param {string} address
     * @returns {TopicId}
     */
    static fromSolidityAddress(address: string): TopicId;
    /**
     * @param {number | Long | import("../EntityIdHelper").IEntityId} props
     * @param {(number | Long)=} realm
     * @param {(number | Long)=} num
     */
    constructor(props: number | Long | import("../EntityIdHelper").IEntityId, realm?: (number | Long) | undefined, num?: (number | Long) | undefined);
    shard: import("long").Long;
    realm: import("long").Long;
    num: import("long").Long;
    /**
     * @type {string | null}
     */
    _checksum: string | null;
    /**
     * @returns {string | null}
     */
    get checksum(): string | null;
    /**
     * @deprecated - Use `validateChecksum` instead
     * @param {Client} client
     */
    validate(client: import("../client/Client.js").default<any, any>): void;
    /**
     * @param {Client} client
     */
    validateChecksum(client: import("../client/Client.js").default<any, any>): void;
    /**
     * @returns {string}
     */
    toSolidityAddress(): string;
    /**
     * @returns {HashgraphProto.proto.ITopicID}
     */
    _toProtobuf(): HashgraphProto.proto.ITopicID;
    /**
     * @returns {string}
     */
    toString(): string;
    /**
     * @param {Client} client
     * @returns {string}
     */
    toStringWithChecksum(client: import("../client/Client.js").default<any, any>): string;
    /**
     * @returns {Uint8Array}
     */
    toBytes(): Uint8Array;
    /**
     * @returns {TopicId}
     */
    clone(): TopicId;
    /**
     * @param {TopicId} other
     * @returns {number}
     */
    compare(other: TopicId): number;
}
export type Long = import("long").Long;
export type Client = import("../client/Client.js").default<any, any>;
import * as HashgraphProto from "@hashgraph/proto";
