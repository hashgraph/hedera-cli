/**
 * @typedef {import("../channel/Channel.js").default} Channel
 * @typedef {import("./MirrorChannel.js").MirrorError} MirrorError
 */
/**
 * @internal
 */
export default class NodeMirrorChannel extends MirrorChannel {
    /**
     * @internal
     * @param {string} address
     */
    constructor(address: string);
    /**
     * @type {grpc.Client}
     * @private
     */
    private _client;
}
export type Channel = import("../channel/Channel.js").default;
export type MirrorError = import("./MirrorChannel.js").MirrorError;
import MirrorChannel from "./MirrorChannel.js";
