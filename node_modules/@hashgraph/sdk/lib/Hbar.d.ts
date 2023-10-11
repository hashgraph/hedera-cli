/**
 * @typedef {import("./long.js").LongObject} LongObject
 */
export default class Hbar {
    /**
     * @param {number | Long | BigNumber} amount
     * @param {HbarUnit} unit
     * @returns {Hbar}
     */
    static from(amount: number | Long | BigNumber, unit: HbarUnit): Hbar;
    /**
     * @param {number | Long | string | BigNumber} amount
     * @returns {Hbar}
     */
    static fromTinybars(amount: number | Long | string | BigNumber): Hbar;
    /**
     * @param {string} str
     * @param {HbarUnit=} unit
     * @returns {Hbar}
     */
    static fromString(str: string, unit?: HbarUnit | undefined): Hbar;
    /**
     * @param {number | string | Long | LongObject | BigNumber} amount
     * @param {HbarUnit=} unit
     */
    constructor(amount: number | string | Long | LongObject | BigNumber, unit?: HbarUnit | undefined);
    _valueInTinybar: BigNumber;
    /**
     * @param {HbarUnit} unit
     * @returns {BigNumber}
     */
    to(unit: HbarUnit): BigNumber;
    /**
     * @returns {BigNumber}
     */
    toBigNumber(): BigNumber;
    /**
     * @returns {Long}
     */
    toTinybars(): Long;
    /**
     * @returns {Hbar}
     */
    negated(): Hbar;
    /**
     * @returns {boolean}
     */
    isNegative(): boolean;
    /**
     * @param {HbarUnit=} unit
     * @returns {string}
     */
    toString(unit?: HbarUnit | undefined): string;
}
export type LongObject = import("./long.js").LongObject;
import BigNumber from "bignumber.js";
import HbarUnit from "./HbarUnit.js";
import Long from "long";
