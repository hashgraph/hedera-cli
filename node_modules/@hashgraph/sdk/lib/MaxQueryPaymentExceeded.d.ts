/**
 * @typedef {import("./Hbar.js").default} Hbar
 */
export default class MaxQueryPaymentExceeded extends Error {
    /**
     * @param {Hbar} queryCost
     * @param {Hbar} maxQueryPayment
     */
    constructor(queryCost: Hbar, maxQueryPayment: Hbar);
    queryCost: import("./Hbar.js").default;
    maxQueryPayment: import("./Hbar.js").default;
}
export type Hbar = import("./Hbar.js").default;
