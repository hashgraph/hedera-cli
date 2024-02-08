export class Logger {
  private static instance: Logger;

  level: 'verbose' | 'quiet' | 'normal' = 'normal';

  public static getInstance(): Logger {
    if (!Logger.instance) {
      Logger.instance = new Logger();
    }
    return Logger.instance;
  }

  setLevel(level: 'verbose' | 'quiet' | 'normal') {
    this.level = level;
  }

  log(message: string | object) {
    if (this.level !== 'quiet') {
      if (typeof message === 'object') {
        message = this._convertObjectToString(message);
      }
      console.log(message);
    }
  }

  verbose(message: string | object) {
    if (this.level === 'verbose') {
      if (typeof message === 'object') {
        message = this._convertObjectToString(message);
      }
      console.log(message);
    }
  }

  // Overload signatures
  error(message: string | object): void;
  error(message: string, error: object): void;

  // Unified implementation
  error(message: string, error?: object) {
    if (typeof message === 'object') {
      message = this._convertObjectToString(message);
    }
    if (error) {
      console.error(message, error);
    } else {
      console.error('Error:', message);
    }
  }

  _convertObjectToString(object: object) {
    return JSON.stringify(object, null, 2);
  }
}
