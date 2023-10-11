export default class SubscriptionHandle {
    /** @type {{(): void} | null} */
    _call: (() => void) | null;
    /**
     * @param {() => void} call
     * @returns {void}
     */
    _setCall(call: () => void): void;
    unsubscribe(): void;
}
