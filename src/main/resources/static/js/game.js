document.addEventListener('DOMContentLoaded', () => {
    const playButton = document.getElementById('playButton');
    const difficulty = document.getElementById('difficulty');
    let game;

    async function start(difficulty) {
        console.log('Starting new game difficulty:', difficulty);
        try {
            const response = await fetch(`/game?difficulty=${difficulty}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            console.log('Response status:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const responseText = await response.text(); // Get the raw response text
            console.log('Raw response text:', responseText);

            try {
                game = JSON.parse(responseText); // Parse the raw response text as JSON
                console.log('Game data received:', game);
            } catch (jsonError) {
                console.error('Error parsing JSON:', jsonError);
                throw jsonError; // Re-throw to catch in the outer catch block
            }
        } catch (error) {
            console.error('Error starting game:', error);
        }
    }


    // Event listeners for buttons
    playButton.addEventListener('click', () => {
        start(difficulty.value).then(r => console.log(r));
    });
});