/**
 * A simple "map" type that allows indexing by objects other than
 * strings, numbers, or booleans, and doesn't use the object pointer.
 *
 * @abstract
 * @template {{ toString(): string }} KeyT
 * @template {any} ValueT
 */
export default class ObjectMap<KeyT extends {
    toString(): string;
}, ValueT extends unknown> {
    /**
     * @param {(s: string) => KeyT} fromString
     */
    constructor(fromString: (s: string) => KeyT);
    /**
     * This map is from the stringified version of the key, to the value
     *
     * @type {Map<string, ValueT>}
     */
    _map: Map<string, ValueT>;
    /**
     * This map is from the key, to the value
     *
     * @type {Map<KeyT, ValueT>}
     */
    __map: Map<KeyT, ValueT>;
    /**
     * A function pointer to convert a key into a string. So we can set each
     * value in both maps.
     */
    _fromString: (s: string) => KeyT;
    /**
     * Get a value by key or string.
     *
     * This is the main benefit of this class. If a user provides a `KeyT` we
     * implicitly serialize it to a string and use the string version. Otherwise
     * the user will get `undefined` even for a key that exists in the map since
     * the `KeyT` the provided has a different pointer than the one we have stored.
     * The string version doesn't have this issue since JS hashes the string and
     * that would result in both `KeyT` hitting the same value even if they're
     * different pointers.
     *
     * @param {KeyT | string} key
     * @returns {?ValueT}
     */
    get(key: KeyT | string): ValueT | null;
    /**
     * Set the key to a value in both maps
     *
     * @internal
     * @param {KeyT} key
     * @param {ValueT} value
     */
    _set(key: KeyT, value: ValueT): void;
    /**
     * Create iterator of values
     *
     * @returns {IterableIterator<ValueT>}
     */
    values(): IterableIterator<ValueT>;
    /**
     * Get the size of the map
     *
     * @returns {number}
     */
    get size(): number;
    /**
     * Get the keys of the map.
     *
     * @returns {IterableIterator<KeyT>}
     */
    keys(): IterableIterator<KeyT>;
    /**
     * Stringify the map into _something_ readable.
     * **NOTE**: This implementation is not stable and can change.
     *
     * @returns {string}
     */
    toString(): string;
    toJSON(): {};
    /**
     * Create an iterator over key, value pairs
     *
     * @returns {IterableIterator<[KeyT, ValueT]>}
     */
    [Symbol.iterator](): IterableIterator<[KeyT, ValueT]>;
}
