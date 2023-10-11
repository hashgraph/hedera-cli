export default class WebChannel extends Channel {
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
