export interface DisplayBalanceOptions {
  onlyHbar: boolean;
  tokenId: string | undefined;
}

// export type DisplayOptions = DisplayBalanceOptions | DisplayTokenOptions;
export type DisplayOptions = DisplayBalanceOptions;
