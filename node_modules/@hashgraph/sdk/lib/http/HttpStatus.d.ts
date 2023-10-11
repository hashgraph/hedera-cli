export default class HttpStatus {
    /**
     * @internal
     * @param {number} code
     * @returns {HttpStatus}
     */
    static _fromValue(code: number): HttpStatus;
    /**
     * @hideconstructor
     * @internal
     * @param {number} code
     */
    constructor(code: number);
    /** @readonly */
    readonly _code: number;
    /**
     * @returns {string}
     */
    toString(): string;
    /**
     * @returns {number}
     */
    valueOf(): number;
}
