export default class NativeChannel extends Channel {
    /**
     * @param {string} address
     */
    constructor(address: string);
    /**
     * @type {string}
     * @private
     */
    private _address;
}
import Channel from "./Channel.js";
