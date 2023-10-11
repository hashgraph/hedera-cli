/**
 * Describes how the http request failed.
 */
export default class HttpError extends Error {
    /**
     * @param {HttpStatus} status
     */
    constructor(status: HttpStatus);
    /**
     * @readonly
     */
    readonly status: HttpStatus;
}
import HttpStatus from "./HttpStatus.js";
