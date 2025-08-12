import * as fs from 'fs';
import * as path from 'path';

describe('Command action wrapping consistency', () => {
  const commandsDir = path.join(__dirname, '..', '..', 'src', 'commands');

  function collectTsFiles(dir: string): string[] {
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    return entries.flatMap((entry) => {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) return collectTsFiles(full);
      if (entry.isFile() && entry.name.endsWith('.ts')) return [full];
      return [];
    });
  }

  const files = collectTsFiles(commandsDir);

  it('every .action usage is wrapped with wrapAction or exitOnError', () => {
    const offenders: { file: string; line: number; snippet: string }[] = [];

    files.forEach((file) => {
      const content = fs.readFileSync(file, 'utf8');
      const lines = content.split(/\r?\n/);
      lines.forEach((line, idx) => {
        if (line.includes('.action(')) {
          // Capture next few lines to inspect the call expression
          const window = lines.slice(idx, idx + 8).join('\n');
          const hasWrapper =
            /wrapAction\s*</.test(window) ||
            /wrapAction\s*\(/.test(window) ||
            /exitOnError\s*\(/.test(window);
          if (!hasWrapper) {
            offenders.push({ file, line: idx + 1, snippet: window });
          }
        }
      });
    });

    if (offenders.length > 0) {
      const detail = offenders
        .map((o) => `${o.file}:${o.line}\n${o.snippet}`)
        .join('\n---\n');
      fail(`Found unwrapped .action handlers:\n${detail}`);
    }
  });
});
