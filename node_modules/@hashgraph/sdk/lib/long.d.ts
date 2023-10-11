/**
 * @typedef {{low: number, high: number, unsigned: boolean}} LongObject
 * @typedef {import("long")} Long
 */
/**
 * @param {Long | number | string | LongObject | BigNumber} value
 * @returns {BigNumber}
 */
export function valueToLong(value: Long | number | string | LongObject | BigNumber): BigNumber;
export type LongObject = {
    low: number;
    high: number;
    unsigned: boolean;
};
export type Long = import("long");
import BigNumber from "bignumber.js";
