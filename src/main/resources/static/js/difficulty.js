document.addEventListener('DOMContentLoaded', () => {

    const playButton = document.getElementById('playButton');
    const difficulty = document.getElementById('difficulty');
    const loadGameButton = document.getElementById('loadGameButton');

    async function createGame(difficulty) {
        const response = await fetch(`/game?difficulty=${difficulty}`, {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    }

    playButton.addEventListener('click', async () => {
        try {
            playButton.disabled = true;
            playButton.textContent = 'Loading...';
            const game = await createGame(difficulty.value);
            window.location.replace(`/game/${game.id}`);
        } catch (error) {
            alert('Failed to start the game. Please try again.');
            console.error('Error:', error);
        } finally {
            playButton.disabled = false;
            playButton.textContent = 'Play';
        }
    });


    function redirect(eventButton, buttonText, path) {
        try {
            eventButton.disabled = true;
            eventButton.textContent = 'Loading...';
            window.location.replace(path);
        } catch (error) {
            console.error('Error redirecting:', error);
        } finally {
            eventButton.disabled = false;
            eventButton.textContent = buttonText;
        }
    }

    loadGameButton.addEventListener('click', () => {
        redirect(loadGameButton, 'Load Game', '/game');
    });
});