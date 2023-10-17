export interface DisplayBalanceOptions {
  onlyHbar: boolean;
  tokenId: string | undefined;
}

export interface DisplayTokenOptions {
  short: boolean;
}

export type DisplayOptions = DisplayBalanceOptions | DisplayTokenOptions;
