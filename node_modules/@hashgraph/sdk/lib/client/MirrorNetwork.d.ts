/**
 * @typedef {import("../channel/MirrorChannel.js").default} MirrorChannel
 */
/**
 * @augments {ManagedNetwork<MirrorChannel, MirrorNode, string>}
 */
export default class MirrorNetwork extends ManagedNetwork<import("../channel/MirrorChannel.js").default, MirrorNode, string> {
    /**
     * @param {(address: string) => MirrorChannel} channelInitFunction
     */
    constructor(channelInitFunction: (address: string) => MirrorChannel);
    /**
     * @param {string[]} network
     */
    setNetwork(network: string[]): void;
    /**
     * @returns {string[]}
     */
    get network(): string[];
    /**
     * @returns {MirrorNode}
     */
    getNextMirrorNode(): MirrorNode;
}
export type MirrorChannel = import("../channel/MirrorChannel.js").default;
import MirrorNode from "../MirrorNode.js";
import ManagedNetwork from "./ManagedNetwork.js";
