## Contributing

### Development Setup

Install dependencies:

```
npm install
```

Run unit tests:

```
npm run test:unit
```

### Configuration & State Layering

The CLI layers state in three tiers (lowest precedence first):

1. Base defaults (`src/state/config.ts`)
2. Optional user overrides (cosmiconfig – `hedera-cli.config.*` or `HCLI_CONFIG_FILE`)
3. Mutable runtime state persisted to JSON (accounts, tokens, scripts, topics, etc.)

`src/state/store.ts` merges these using a deep merge only for the `networks` map to preserve shipped networks while allowing partial overrides.

### Test Environment Strategy

To keep tests deterministic and isolated while still exercising the real layering logic:

- A single global Jest setup file (`__tests__/setup/jestSetup.ts`) sets `HCLI_CONFIG_FILE` to the fixture `__tests__/fixtures/hedera-cli.config.test.json` for every worker process.
- Each Jest worker is assigned a unique temporary state file via `HCLI_STATE_FILE` (per-worker path in the OS temp directory). This prevents cross-test interference when tests run in parallel and mutate runtime state.
- The store dynamically re-loads user config on hydration, so changes to the user config file appear without restarting the process.

### resetStore Helper

`resetStore(opts?)` (exported from `src/state/store.ts`) rebuilds the underlying Zustand store and re-layers configuration. Use it when you need to:

- Override `HCLI_CONFIG_FILE` within a specific test (e.g. simulate missing / malformed / empty config).
- Point to a custom temporary state file (`opts.stateFile`) for fine‑grained isolation inside a single test file.

Example:

```ts
import { resetStore, getState } from '../../src/state/store';
import * as os from 'os';
import * as path from 'path';

test('missing user config falls back to defaults', () => {
  const tmpConfigPath = path.join(
    os.tmpdir(),
    `hcli-missing-${Date.now()}.json`,
  );
  process.env.HCLI_CONFIG_FILE = tmpConfigPath; // no file created
  resetStore({
    stateFile: path.join(os.tmpdir(), `hcli-state-${Date.now()}.json`),
  });
  const state = getState();
  expect(state.telemetry).toBe(0);
  expect(state.networks['fixture-extra']).toBeUndefined();
});
```

### Logging & Silencing Strategy in Tests

The unified setup file also sets `HCLI_LOG_MODE=silent` by default so test output stays clean. Override early in a test (before importing the logger) if you need visible logs.

Per‑test scoped control helpers live in `__tests__/helpers/loggerHelper.ts`:

```ts
import {
  withSilencedLogs,
  withVerboseLogs,
  withNormalLogs,
} from '../helpers/loggerHelper';

test('quiet block', async () => {
  await withSilencedLogs(async () => {
    // code that would normally log
  });
});

test('verbose tracing', async () => {
  await withVerboseLogs(async () => {
    // code executed with level = verbose
  });
});
```

Prefer these scoped helpers over imperative setters; they automatically restore the previous level.

If you need raw console output in a specific test file, set the mode early (before any logger import):

```ts
process.env.HCLI_LOG_MODE = 'normal'; // or 'verbose'
```

### Command Action Wrapping (Required)

All Commander `.action` handlers must be wrapped by either:

- `wrapAction` (preferred) – adds dynamic variable replacement, optional verbose pre-log, and standardized error handling via `exitOnError`.
- `exitOnError` – if you only need the error mapping behavior.

Why: This enforces consistent DomainError handling (setting `process.exitCode` instead of exiting), guarantees telemetry flushing, and keeps logging noise predictable. A test `wrappingConsistency.test.ts` fails the build if any unwrapped `.action(` is introduced.

When adding a new command:

```ts
import { wrapAction } from '../shared/wrapAction';

program.command('do-thing').action(
  wrapAction(
    async (opts) => {
      // implement logic
    },
    { log: 'Doing the thing' },
  ),
);
```

Throw `new DomainError(message, code?)` for user-facing failures instead of calling `process.exit`.

### Logger Modes

The logger supports modes: `normal`, `verbose`, `quiet`, `silent` (set via `--verbose`, `--quiet`, or `--log-mode <mode>` / `HCLI_LOG_MODE`). Avoid adding bespoke silencing flags; extend the existing mode enum if new behavior is required.

### Adding New Config Edge-Case Tests

Patterns:

- Missing config: point `HCLI_CONFIG_FILE` to a non-existent path then `resetStore()`.
- Empty config: create a temp file with empty contents.
- Malformed config: already covered by `invalidConfig.test.ts` (bad JSON falls back gracefully).

Always restore or reassign `HCLI_CONFIG_FILE` if the test file later relies on the standard fixture.

### Commit Guidelines

- Keep patches focused; unrelated formatting churn makes review harder.
- Prefer small, composable utility helpers when test logic starts repeating (e.g., temp file creation, logger control).
- Run the full unit suite before opening a PR.

## Issues

GitHub [issues](https://docs.github.com/en/issues) are used as the primary method for tracking project changes. Issues should track actionable items that will result in changes to the codebase. As a result, support inquiries should be directed to one of the aforementioned [support channels](https://github.com/hashgraph/stablecoin-studio?tab=contributing-ov-file#support-channels).

### Vulnerability Disclosure

Most of the time, when you find a bug, it should be reported the GitHub issue tracker for the project. However, if you are reporting a security vulnerability, please see our [Hedera bug bounty program](https://hedera.com/bounty).

### Issue Types

There are three types of issues each with their own corresponding label:

- Bug: These track issues with the code
- Documentation: These track problems or insufficient coverage with the documentation
- Enhancement: These track specific feature requests and ideas until they are complete. This should only be for trivial or minor enhancements. If the feature is sufficiently large, complex or requires coordination among multiple Hedera projects, it should first go through the [Hedera Improvement Proposal](https://github.com/hiero-ledger/hiero-improvement-proposals) process.

### Issue Lifecycle

The issue lifecycle is mainly driven by the core maintainers, but is still useful to know for those wanting to contribute. All issue types follow the same general lifecycle. Differences will be noted below.

Creation

- The user will open a ticket in the GitHub project and apply one of the [issue type](#issue-types) labels.

## Pull Requests

Like most open source projects, we use [pull requests](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests) ( PRs) to track code changes.

### Forking

1. [Fork](https://docs.github.com/en/get-started/exploring-projects-on-github/contributing-to-a-project) the repository. Go to the project page then hit the `Fork` button to fork your own copy of the repository to your GitHub account.
2. Clone the forked repository to your local working directory.

```
$ git clone https://github.com/${owner}/${repo}.git
```

3. Add an `upstream` remote to keep your fork in sync with the main repo.

```
$ git remote add upstream https://github.com/hashgraph/${repo}.git
```

4. Sync your local `main` branch.

```
$ git pull upstream main
```

> **Note**: Some repositories may still be using `master` for their default branch. 5. Create a branch to add a new feature or fix issues.

```
$ git checkout -b new-feature
```

6. Make any change on the branch `new-feature` then build and test your code locally.

7. Add files that you want to be committed.

```
$ git add <file>
```

8. Enable [GPG signing](https://docs.github.com/en/authentication/managing-commit-signature-verification/signing-commits) of your commits within the repo. Signing all your commits with your public key allows the comunity to verify it's really you. If you forgot to sign some commits that are part of the contribution, you can ask [git to rewrite your commit history](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History).

```
$ git config commit.gpgsign true
```

9. Use [sign-off](https://github.com/hashgraph/stablecoin-studio?tab=contributing-ov-file#sign-off) when making each of your commits. Additionally, please [GPG sign](https://docs.github.com/en/authentication/managing-commit-signature-verification/signing-commits) all your commits with your public key so we can verify it's really you. If you forgot to sign-off some commits that are part of the contribution, you can ask git to rewrite your commit history.

```
$ git commit --signoff -S -m "Your commit message"
```

10. [Submit](https://github.com/hashgraph/stablecoin-studio?tab=contributing-ov-file#pr-lifecycle) a pull request.

### Sign Off

The sign-off is a simple line at the end of a commit message. All commits needs to be signed. Your signature certifies that you wrote the code or otherwise have the right to contribute the material. First, read the [Developer Certificate of Origin](https://developercertificate.org/) (DCO) to fully understand its terms.

Contributors sign-off that they adhere to these requirements by adding a Signed-off-by line to commit messages (as seen via git log):

```
Author: Joe Smith <joe.smith@example.com>
Date:   Thu Feb 2 11:41:15 2018 -0800

    Update README

    Signed-off-by: Joe Smith <joe.smith@example.com>
```

Use your real name and email (sorry, no pseudonyms or anonymous contributions). Notice the `Author` and `Signed-off-by` lines match. If they don't your PR will be rejected by the automated DCO check.

If you set your `user.name` and `user.email` git configs, you can sign your commit automatically with `-s` or `--sign-off` command line option:

```
$ git config --global user.name "Joe Smith"
$ git config --global user.email "joe.smith@example.com"
$ git commit -s -m 'Update README'
```

### PR Lifecycle

Now that you've got your forked branch, you can proceed to submit it.

Submitting

- It is preferred, but not required, to have a PR tied to a specific issue. There can be circumstances where if it is a quick fix then an issue might be overkill. The details provided in the PR description would suffice in this case.
- The PR description or commit message should contain a [keyword](https://docs.github.com/en/issues/tracking-your-work-with-issues/using-issues/linking-a-pull-request-to-an-issue) to automatically close the related issue.
- Commits should be as small as possible, while ensuring that each commit is correct independently (i.e., each commit should compile and pass tests).
- Add tests and documentation relevant to the fixed bug or new feature. Code coverage should stay the same or increase for the PR to be approved.
- We more than welcome PRs that are currently in progress. If a PR is a work in progress, it should be opened as a [Draft PR](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests#draft-pull-requests). Once the PR is ready for review, mark it as ready to convert it to a regular PR.
- After submitting, ensure all GitHub checks pass before requesting a review. Also double-check all static analysis and coverage tools show a sufficient coverage and quality level.

Triage

- The maintainer in charge of triaging will apply the proper labels for the PR.
- Add the PR to the correct milestone. This should be the same as the issue the PR closes.
- The maintainer can assign a reviewer, or a reviewer can assign themselves.

Reviewing

- All reviews will be completed using the GitHub review tool.
- A "Comment" review should be used when there are questions about the code that should be answered, but that don't involve code changes. This type of review does not count as approval.
- A "Changes Requested" review indicates that changes to the code need to be made before they will be merged.
- For documentation, special attention will be paid to spelling, grammar, and clarity (whereas those things don't matter as much for comments in code).
- Reviews are also welcome from others in the community. In the code review, a message can be added, as well as `LGTM` if the PR is good to merge. It’s also possible to add comments to specific lines in a file, for giving context to the comment.
- PR owner should try to be responsive to comments by answering questions or changing code. If the owner is unsure of any comment, please ask for clarification in the PR comments.
- Once all comments have been addressed and all reviewers have approved, the PR is ready to be merged.

Merge or Close

- PRs should stay open until they are merged or closed. The issue may be closed if the submitter has not been responsive for more than 30 days. This will help keep the PR queue to a manageable size and reduce noise.

### Questions / Improvements

Open an issue or PR if you see an opportunity to simplify store layering, reduce I/O in tests, or extend configuration validation.
