/**
 * @typedef {import("./channel/MirrorChannel.js").default} MirrorChannel
 * @typedef {import("./ManagedNodeAddress.js").default} ManagedNodeAddress
 */
/**
 * @typedef {object} NewNode
 * @property {string} address
 * @property {(address: string, cert?: string) => MirrorChannel} channelInitFunction
 */
/**
 * @typedef {object} CloneNode
 * @property {MirrorNode} node
 * @property {ManagedNodeAddress} address
 */
/**
 * @augments {ManagedNode<MirrorChannel>}
 */
export default class MirrorNode extends ManagedNode<import("./channel/MirrorChannel.js").default> {
    /**
     * @param {object} props
     * @param {NewNode=} [props.newNode]
     * @param {CloneNode=} [props.cloneNode]
     */
    constructor(props?: {
        newNode?: NewNode | undefined;
        cloneNode?: CloneNode | undefined;
    });
}
export type MirrorChannel = import("./channel/MirrorChannel.js").default;
export type ManagedNodeAddress = import("./ManagedNodeAddress.js").default;
export type NewNode = {
    address: string;
    channelInitFunction: (address: string, cert?: string) => MirrorChannel;
};
export type CloneNode = {
    node: MirrorNode;
    address: ManagedNodeAddress;
};
import ManagedNode from "./ManagedNode.js";
