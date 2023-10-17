export interface Command {
    parent: {
      action: () => { name: () => string };
      args: string[];
    };
}
