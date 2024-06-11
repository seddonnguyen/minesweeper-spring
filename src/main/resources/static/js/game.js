document.addEventListener('DOMContentLoaded', () => {
    const playButton = document.getElementById('playButton');
    const difficultySelect = document.getElementById('difficulty');
    const gameScreen = document.getElementById('gameScreen');
    const startScreen = document.getElementById('startScreen');
    const gameStatus = document.getElementById('gameStatus');
    const gameOverModal = document.getElementById('gameOverModal');
    const gameOverHeader = document.getElementById('gameOverHeader');
    const gameOverMessage = document.getElementById('gameOverMessage');
    const gameOverTime = document.getElementById('gameOverTime');
    const newGameButton = document.getElementById('newGameButton');
    const restartButton = document.getElementById('restartButton');
    const newLevelButton = document.getElementById('newLevelButton');
    const saveGameButton = document.getElementById('saveGameButton');
    const canvas = document.getElementById('gameCanvas');
    const context = canvas.getContext('2d');
    let currentGame;
    let cellSize;

    async function start(difficulty) {
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

            try {
                game = await response.json();
                console.log('Game data received:', game);
            } catch (jsonError) {
                console.error('Error parsing JSON:', jsonError);
                throw jsonError;
            }
        } catch (error) {
            console.error('Error starting game:', error);
        }
    }

    function setupGame() {
        const {rows, cols} = currentGame.board;
        const availableWidth = window.innerWidth - 40;
        const availableHeight = window.innerHeight - 200;
        const maxCellWidth = Math.max(availableWidth / cols, 25);
        const maxCellHeight = Math.max(availableHeight / rows, 25);
        cellSize = Math.min(maxCellWidth, maxCellHeight);

        canvas.width = cellSize * cols;
        canvas.height = cellSize * rows;

        startScreen.classList.add('hidden');
        gameScreen.classList.remove('hidden');
        gameOverModal.classList.add('hidden');
        updateGameStatus();
        drawBoard();
        canvas.addEventListener('click', handleCanvasClick);
        canvas.addEventListener('contextmenu', handleCanvasRightClick);
    }

    function updateGameStatus() {
        gameStatus.innerHTML = `
            <div>Cells Opened: ${currentGame.board.cells.filter(c => c.revealed).length}</div>
            <div>Flags Placed: ${currentGame.board.cells.filter(c => c.flagged).length}</div>
            <div>Mines Remaining: ${currentGame.board.mines - currentGame.board.cells.filter(c => c.isFlagged).length}</div>
        `;
    }


    // Event listeners for buttons
    playButton.addEventListener('click', () => {
        start(difficulty.value).then(r => console.log(r));
    });
});