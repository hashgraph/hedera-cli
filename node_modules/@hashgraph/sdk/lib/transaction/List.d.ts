/**
 * A custom list type which round robins, supports locking, and as additional
 * QoL improvements.
 *
 * @template {any} T
 */
export default class List<T extends unknown> {
    /** @type {T[]} */
    list: T[];
    locked: boolean;
    index: number;
    /**
     * Overwrite the entire list.
     *
     * @throws if the list is locked
     * @param {T[]} list
     * @returns {this}
     */
    setList(list: T[]): this;
    /**
     * Push items to the end of the list.
     *
     * @throws if the list is locked
     * @param {T[]} items
     * @returns {this}
     */
    push(...items: T[]): this;
    /**
     * Locks the list.
     *
     * @returns {this}
     */
    setLocked(): this;
    /**
     * Clear the list
     */
    clear(): void;
    /**
     * The get value at a particular index.
     *
     * @param {number} index
     * @returns {T}
     */
    get(index: number): T;
    /**
     * Set value at index
     *
     * @param {number} index
     * @param {T} item
     * @returns {this}
     */
    set(index: number, item: T): this;
    /**
     * Set value at index if it's not already set
     *
     * @throws if the list is locked
     * @param {number} index
     * @param {() => T} lambda
     * @returns {this}
     */
    setIfAbsent(index: number, lambda: () => T): this;
    /**
     * Get the current value, and advance the index
     *
     * @returns {T}
     */
    get next(): T;
    /**
     * Get the current value.
     *
     * @returns {T}
     */
    get current(): T;
    /**
     * Advance the index to the next element in a round robin fashion
     *
     * @returns {number}
     */
    advance(): number;
    /**
     * Is the list empty
     *
     * @returns {boolean}
     */
    get isEmpty(): boolean;
    /**
     * Get the length of the list
     *
     * @returns {number}
     */
    get length(): number;
    /**
     * Shallow clone this list.
     * Perhaps we should explicitly call this `shallowClone()` since it doesn't
     * clone the list inside?
     *
     * @returns {List<T>}
     */
    clone(): List<T>;
}
