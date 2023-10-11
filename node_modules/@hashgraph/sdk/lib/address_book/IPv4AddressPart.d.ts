export default class IPv4AddressPart {
    /**
     * @param {object} props
     * @param {number} [props.left]
     * @param {number} [props.right]
     */
    constructor(props?: {
        left?: number | undefined;
        right?: number | undefined;
    });
    /**
     * @type {number | null}
     */
    _left: number | null;
    /**
     * @type {number | null}
     */
    _right: number | null;
    /**
     * @returns {?number}
     */
    get left(): number | null;
    /**
     * @param {number} part
     * @returns {this}
     */
    setLeft(part: number): this;
    /**
     * @returns {?number}
     */
    get right(): number | null;
    /**
     * @param {number} part
     * @returns {this}
     */
    setRight(part: number): this;
    /**
     * @returns {string}
     */
    toString(): string;
}
