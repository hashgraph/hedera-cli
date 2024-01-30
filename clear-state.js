const fs = require('fs');

async function main() {
    console.log('Start clearing state/state.json file');

    const baseState = await fs.readFileSync('./src/state/base_state.json', 'utf8');
    fs.writeFileSync('./src/state/state.json', baseState);
}

main();