declare class HbarUnit {
    /**
     * @param {string} unit
     * @returns {HbarUnit}
     */
    static fromString(unit: string): HbarUnit;
    /**
     * @internal
     * @param {string} name
     * @param {string} symbol
     * @param {BigNumber} tinybar
     */
    constructor(name: string, symbol: string, tinybar: BigNumber);
    /**
     * @internal
     * @readonly
     */
    readonly _name: string;
    /**
     * @internal
     * @readonly
     */
    readonly _symbol: string;
    /**
     * @internal
     * @readonly
     */
    readonly _tinybar: BigNumber;
}
declare namespace HbarUnit {
    const Tinybar: HbarUnit;
    const Microbar: HbarUnit;
    const Millibar: HbarUnit;
    const Hbar: HbarUnit;
    const Kilobar: HbarUnit;
    const Megabar: HbarUnit;
    const Gigabar: HbarUnit;
}
export default HbarUnit;
import BigNumber from "bignumber.js";
