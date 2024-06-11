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

    async function startGame(userId, difficultyId) {
        try {
            const response = await fetch(`/api/games?userId=${userId}&difficultyId=${difficultyId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json'
                }
            });
            const game = await response.json();
            currentGame = game;
            setupGame();
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

    async function handleCanvasClick(event) {
        const {offsetX, offsetY} = event;
        const row = Math.floor(offsetY / cellSize);
        const col = Math.floor(offsetX / cellSize);

        try {
            await fetch(`/api/games/${currentGame.id}/board/open-cell?row=${row}&col=${col}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json'
                }
            });
            const response = await fetch(`/api/games/${currentGame.id}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            currentGame = await response.json();
            updateGameStatus();
            drawBoard();
            if (currentGame.gameOver) {
                endGame();
                if (currentGame.won) {
                    displayGameOverModal("Congratulations!", "You've cleared the minefield.");
                } else {
                    displayGameOverModal("Game Over!", "You hit a mine.");
                }
            }
        } catch (error) {
            console.error('Error opening cell:', error);
        }
    }

    async function handleCanvasRightClick(event) {
        event.preventDefault();
        const {offsetX, offsetY} = event;
        const row = Math.floor(offsetY / cellSize);
        const col = Math.floor(offsetX / cellSize);

        try {
            await fetch(`/api/games/${currentGame.id}/board/toggle-flag?row=${row}&col=${col}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json'
                }
            });
            const response = await fetch(`/api/games/${currentGame.id}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            currentGame = await response.json();
            updateGameStatus();
            drawBoard();
        } catch (error) {
            console.error('Error toggling flag:', error);
        }
    }

    async function saveCurrentGame() {
        try {
            await fetch(`/api/games/${currentGame.id}/save`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json'
                }
            });
            alert("Game saved successfully!");
        } catch (error) {
            console.error('Error saving game:', error);
        }
    }

    function drawBoard() {
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.font = `${cellSize / 2}px 'Roboto'`;
        context.textAlign = 'center';
        context.textBaseline = 'middle';

        currentGame.board.cells.forEach(cell => {
            const x = cell.col * cellSize;
            const y = cell.row * cellSize;

            context.strokeStyle = '#f9a826';
            context.lineWidth = 2;
            context.strokeRect(x, y, cellSize, cellSize);

            if (cell.isOpened) {
                context.fillStyle = '#333';
                context.fillRect(x, y, cellSize, cellSize);
                if (cell.isMine) {
                    drawImage(context, '/images/mine.svg', x + 2, y + 2, cellSize - 4);
                } else if (cell.adjacentMines > 0) {
                    context.fillStyle = getNumberColor(cell.adjacentMines);
                    context.fillText(cell.adjacentMines, x + cellSize / 2, y + cellSize / 2);
                }
            } else if (cell.isFlagged) {
                let img = '/images/flag.svg';
                if (currentGame.gameOver && !cell.isMine) {
                    img = '/images/invalid.svg';
                }
                context.fillStyle = '#333';
                context.fillRect(x, y, cellSize, cellSize);
                drawImage(context, img, x + 2, y + 2, cellSize - 4);
            } else {
                context.fillStyle = '#444';
                context.fillRect(x, y, cellSize, cellSize);
            }
        });
    }

    function drawImage(context, src, x, y, size) {
        const img = new Image();
        img.src = src;
        img.onload = () => {
            context.drawImage(img, x, y, size, size);
        };
    }

    function getNumberColor(number) {
        switch (number) {
            case 1:
                return '#1abc9c';
            case 2:
                return '#3498db';
            case 3:
                return '#e74c3c';
            case 4:
                return '#9b59b6';
            case 5:
                return '#f39c12';
            case 6:
                return '#2ecc71';
            case 7:
                return '#e67e22';
            case 8:
                return '#e84393';
            default:
                return '#ecf0f1';
        }
    }

    function updateGameStatus() {
        gameStatus.innerHTML = `
            <div>Cells Opened: ${currentGame.board.cells.filter(c => c.isOpened).length}</div>
            <div>Flags Placed: ${currentGame.board.cells.filter(c => c.isFlagged).length}</div>
            <div>Mines Remaining: ${currentGame.board.mines - currentGame.board.cells.filter(c => c.isFlagged).length}</div>
        `;
    }

    function displayGameOverModal(header, message) {
        gameOverHeader.textContent = header;
        gameOverMessage.textContent = message;
        gameOverTime.textContent = `Time: ${currentGame.elapsedTime}ms`;
        gameOverModal.classList.remove('hidden');
    }

    function endGame() {
        canvas.removeEventListener('click', handleCanvasClick);
        canvas.removeEventListener('contextmenu', handleCanvasRightClick);
    }

    // Event listeners for buttons
    playButton.addEventListener('click', () => {
        const userId = getUserId(); // Implement a function to get the user ID
        const difficultyId = difficultySelect.value;
        startGame(userId, difficultyId);
    });

    newGameButton.addEventListener('click', () => {
        gameOverModal.classList.add('hidden');
        startScreen.classList.remove('hidden');
        gameScreen.classList.add('hidden');
    });

    restartButton.addEventListener('click', () => {
        const userId = getUserId(); // Implement a function to get the user ID
        const difficultyId = difficultySelect.value;
        startGame(userId, difficultyId);
    });

    newLevelButton.addEventListener('click', () => {
        gameScreen.classList.add('hidden');
        startScreen.classList.remove('hidden');
    });

    saveGameButton.addEventListener('click', () => {
        saveCurrentGame();
    });

    // Function to get user ID from JWT token
    function getUserId() {
        const token = localStorage.getItem('token');
        if (!token) return null;

        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.userId;
    }

    // Example: Load user games on startup
    function loadUserGames() {
        const userId = getUserId();
        fetch(`/api/games/user/${userId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
            .then(response => response.json())
            .then(games => {
                // Populate saved games and won games lists
                const savedGamesList = document.getElementById('savedGamesList');
                const wonGamesList = document.getElementById('wonGamesList');
                savedGamesList.innerHTML = '';
                wonGamesList.innerHTML = '';

                games.forEach(game => {
                    const gameItem = document.createElement('li');
                    gameItem.textContent = `Game ${game.id} - ${game.difficulty.name}`;
                    gameItem.addEventListener('click', () => {
                        currentGame = game;
                        setupGame();
                    });
                    if (game.won) {
                        wonGamesList.appendChild(gameItem);
                    } else {
                        savedGamesList.appendChild(gameItem);
                    }
                });
            });
    }

    // Call loadUserGames on page load or after user login
    loadUserGames();
});