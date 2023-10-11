export default class IPv4Address {
    /**
     * @internal
     * @param {Uint8Array} bytes
     * @returns {IPv4Address}
     */
    static _fromProtobuf(bytes: Uint8Array): IPv4Address;
    /**
     * @param {object} props
     * @param {IPv4AddressPart} [props.network]
     * @param {IPv4AddressPart} [props.host]
     */
    constructor(props?: {
        network?: IPv4AddressPart | undefined;
        host?: IPv4AddressPart | undefined;
    });
    /**
     * @type {IPv4AddressPart | null}
     */
    _network: IPv4AddressPart | null;
    /**
     * @type {IPv4AddressPart | null}
     */
    _host: IPv4AddressPart | null;
    /**
     * @returns {?IPv4AddressPart}
     */
    get newtork(): IPv4AddressPart | null;
    /**
     * @param {IPv4AddressPart} part
     * @returns {this}
     */
    setNetwork(part: IPv4AddressPart): this;
    /**
     * @returns {?IPv4AddressPart}
     */
    get host(): IPv4AddressPart | null;
    /**
     * @param {IPv4AddressPart} part
     * @returns {this}
     */
    setHost(part: IPv4AddressPart): this;
    /**
     * @returns {Uint8Array}
     */
    _toProtobuf(): Uint8Array;
    /**
     * @returns {string}
     */
    toString(): string;
}
import IPv4AddressPart from "./IPv4AddressPart.js";
