declare class FeeAssessmentMethod {
    /**
     * @internal
     * @param {boolean} value
     * @returns {FeeAssessmentMethod}
     */
    static _fromValue(value: boolean): FeeAssessmentMethod;
    /**
     * @hideconstructor
     * @internal
     * @param {boolean} value
     */
    constructor(value: boolean);
    /** @readonly */
    readonly _value: boolean;
    /**
     * @returns {string}
     */
    toString(): string;
    /**
     * @returns {boolean}
     */
    valueOf(): boolean;
}
declare namespace FeeAssessmentMethod {
    const Inclusive: FeeAssessmentMethod;
    const Exclusive: FeeAssessmentMethod;
}
export default FeeAssessmentMethod;
