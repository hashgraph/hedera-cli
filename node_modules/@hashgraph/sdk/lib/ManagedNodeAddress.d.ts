/**
 * @typedef {import("./account/AccountId.js").default} AccountId
 * @typedef {import("./channel/Channel.js").default} Channel
 * @typedef {import("./channel/MirrorChannel.js").default} MirrorChannel
 * @typedef {import("./address_book/NodeAddress.js").default} NodeAddress
 */
export const HOST_AND_PORT: RegExp;
export default class ManagedNodeAddress {
    /**
     * @param {string} address
     * @returns {ManagedNodeAddress};
     */
    static fromString(address: string): ManagedNodeAddress;
    /**
     * @param {object} props
     * @param {string} [props.address]
     * @param {string} [props.host]
     * @param {number | null} [props.port]
     */
    constructor(props?: {
        address?: string | undefined;
        host?: string | undefined;
        port?: number | null | undefined;
    });
    /** @type {string} */
    _address: string;
    /** @type {number | null} */
    _port: number | null;
    toInsecure(): ManagedNodeAddress;
    toSecure(): ManagedNodeAddress;
    /**
     * @returns {string}
     */
    get address(): string;
    /**
     * @returns {number | null}
     */
    get port(): number | null;
    /**
     * @returns {boolean}
     */
    isTransportSecurity(): boolean;
    /**
     * @returns {string}
     */
    toString(): string;
}
export type AccountId = import("./account/AccountId.js").default;
export type Channel = import("./channel/Channel.js").default;
export type MirrorChannel = import("./channel/MirrorChannel.js").default;
export type NodeAddress = import("./address_book/NodeAddress.js").default;
