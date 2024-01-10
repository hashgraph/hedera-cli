import { prompt } from 'enquirer';
import { PromptResponse } from '../../types';

async function createPrompt(choices: string[], message: string): Promise<string> {
  const response: PromptResponse = await prompt({
    type: 'select',
    name: 'selection',
    message,
    choices,
  });

  return response.selection;
}

export default { createPrompt };
