export interface Command {
    parent: {
      action: () => { name: () => string };
      args: string[];
    };
}

export interface PromptResponse {
  selection: string;
}