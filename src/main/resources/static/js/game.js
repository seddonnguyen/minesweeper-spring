document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('gameCanvas');
    const context = canvas.getContext('2d');

    const gameScreen = document.getElementById('gameScreen');
    const gameStatus = document.getElementById('gameStatus');
    const gameOverModal = document.getElementById('gameOverModal');
    const closeGameOverModal = document.getElementById('closeGameOverModal');
    const gameOverHeader = document.getElementById('gameOverHeader');
    const gameOverMessage = document.getElementById('gameOverMessage');
    const gameOverTime = document.getElementById('gameOverTime');
    const menuButton = document.getElementById('menuButton');
    const menu2Button = document.getElementById('menu2Button');
    const newGameButton = document.getElementById('newGameButton');
    const restartButton = document.getElementById('restartButton');
    const mineImg = new Image();
    const flagImg = new Image();
    const invalidImg = new Image();

    mineImg.src = mineImgSrc;
    flagImg.src = flagImgSrc;
    invalidImg.src = invalidImgSrc;

    let game = window.gameData;
    let board = game ? game.board : null;
    let cells = board ? board.cells : [];
    let cellSize;
    let startTime;
    let timerInterval;
    let elapsedTimeString;
    let elapsedTimeInSeconds;

    // Setup Web Audio API
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const sounds = {};

    const loadSound = (url) => {
        return fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Failed to fetch ${url}: ${response.statusText}`);
                }
                return response.arrayBuffer();
            })
            .then(data => audioContext.decodeAudioData(data));
    };

    const playSound = (buffer) => {
        const source = audioContext.createBufferSource();
        source.buffer = buffer;
        source.connect(audioContext.destination);
        source.start(0);
    };

    const initSounds = async () => {
        try {
            sounds.startGame = await loadSound(startGameSoundSrc);
            sounds.explosion = await loadSound(explosionSoundSrc);
            sounds.smallExplosion = await loadSound(smallExplosionSoundSrc);
            sounds.victory = await loadSound(victorySoundSrc);
            sounds.flag = await loadSound(flagSoundSrc);
            sounds.open = await loadSound(openSoundSrc);
            sounds.select = await loadSound(selectSoundSrc);
        } catch (error) {
            console.error('Error loading sounds:', error);
        }
    };

    initSounds().then(() => {
        if (game) {
            playSound(sounds.startGame);
            setupGame();
        }
    });

    async function newGame(difficulty) {
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

    async function removeGame(id) {
        const response = await fetch(`/game/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
    }

    async function revealPosition(id, row, col) {
        const response = await fetch(`/game/${id}/reveal?row=${row}&col=${col}`, {
            method: 'PUT', headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    }

    async function toggleFlag(id, row, col) {
        const response = await fetch(`/game/${id}/flag?row=${row}&col=${col}`, {
            method: 'PUT', headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    }


    function handleCanvasClick(event) {
        if (game && game.gameOver) {return;}

        const {
            offsetX, offsetY
        } = event;
        const row = Math.floor(offsetY / cellSize);
        const col = Math.floor(offsetX / cellSize);

        if (!validatePosition(row, col)) {return;}

        let cell = findCell(row, col);
        if (cell && !cell.hidden) {return;}

        try {
            revealPosition(game.id, row, col).then(gameData => {
                if (gameData === null) {return;}
                game = gameData;
                board = game.board;
                cells = board.cells;

                playSound(findCell(row, col).exploded ? sounds.smallExplosion : sounds.open);
                updateGameStatus();
                drawBoard();

                if (game.gameLost) {
                    endGame();
                    drawBoard();
                    playSound(sounds.explosion);
                    setTimeout(() => displayGameOverModal("Game Over!", "You hit a mine."), 500);
                } else if (game.gameWon) {
                    endGame();
                    playSound(sounds.victory);
                    setTimeout(() => displayGameOverModal("Congratulations!", "You've cleared the minefield."), 500);
                }
            });
        } catch (error) {
            console.error('Error revealing cell:', error);
        }
    }

    function handleCanvasRightClick(event) {
        event.preventDefault();
        if (game && !game.gameInProgress) { return; }

        const {
            offsetX, offsetY
        } = event;
        const row = Math.floor(offsetY / cellSize);
        const col = Math.floor(offsetX / cellSize);

        if (!validatePosition(row, col)) {return;}

        let cell = findCell(row, col);
        if (cell && !cell.hidden && !cell.flagged) {return;}

        try {
            toggleFlag(game.id, row, col).then(gameData => {
                if (gameData === null) {return;}
                game = gameData;
                board = game.board;
                cells = board.cells;

                playSound(sounds.flag);
                updateGameStatus();
                drawBoard();
            });
        } catch (error) {
            console.error('Error toggling flag:', error);
        }
    }

    function findCell(row, col) {
        return cells.find(cell => cell.row === row && cell.col === col);
    }

    function validatePosition(row, col) {
        return row >= 0 && row < board.rows && col >= 0 && col < board.cols;
    }

    function setupGame() {
        const {rows, cols} = board;
        const availableWidth = window.innerWidth - 40;
        const availableHeight = window.innerHeight - 200;
        const maxCellWidth = Math.max(availableWidth / cols, 25);
        const maxCellHeight = Math.max(availableHeight / rows, 25);
        cellSize = Math.min(maxCellWidth, maxCellHeight);

        canvas.width = cellSize * cols;
        canvas.height = cellSize * rows;
        startTime = new Date().getTime();
        elapsedTimeInSeconds = game.elapsedTimeInSeconds;

        gameScreen.classList.remove('hidden');
        gameOverModal.classList.add('hidden');

        updateGameStatus();
        drawBoard();

        canvas.addEventListener('click', handleCanvasClick);
        canvas.addEventListener('contextmenu', handleCanvasRightClick);
        if (!game.gameOver) {startTimer();}
    }

    function updateGameStatus() {
        elapsedTimeString = getElapsedTime();
        gameStatus.innerHTML = `
            <div>Life: ${game.life}</div>
            <div>Cells Opened: ${board.revealed}</div>
            <div>Flags Placed: ${board.flags}</div>
            <div>Mines Remaining: ${board.remainingMines}</div>
            <div>Elapsed Time: ${elapsedTimeString}</div>
        `;
    }

    function drawImage(context, image, x, y, size) {
        image.onload = () => {
            context.drawImage(image, x, y, size, size);
        };
        if (image.complete) {
            context.drawImage(image, x, y, size, size);
        }
    }

    function drawBoard() {
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.font = `${cellSize / 2}px 'Roboto'`;
        context.textAlign = 'center';
        context.textBaseline = 'middle';

        cells.forEach(cell => {
            const x = cell.col * cellSize;
            const y = cell.row * cellSize;

            context.strokeStyle = '#f9a826';
            context.lineWidth = 2;
            context.strokeRect(x, y, cellSize, cellSize);

            if (cell.hidden) {
                context.fillStyle = '#444';
                context.fillRect(x, y, cellSize, cellSize);
                return;
            }

            context.fillStyle = '#333';
            context.fillRect(x, y, cellSize, cellSize);

            if (cell.exploded) {
                drawImage(context, mineImg, x + 2, y + 2, cellSize - 4);
            } else if (cell.flagged) {
                drawImage(context, flagImg, x + 2, y + 2, cellSize - 4);
            } else if (cell.incorrectlyFlagged) {
                drawImage(context, invalidImg, x + 2, y + 2, cellSize - 4);
            } else if (cell.adjacentMine > 0) {
                context.fillStyle = getNumberColor(cell.adjacentMine);
                context.fillText(cell.adjacentMine, x + cellSize / 2, y + cellSize / 2);
            }
        });
    }

    async function updateElapsedTime(id, elapsedTimeInSeconds) {
        const response = await fetch(`/game/${id}/mark`, {
            method: 'PUT', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify({elapsedTimeInSeconds})
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
    }

    function getElapsedTime() {
        if (game.gameOver) {return formatElapsedTime(game.elapsedTimeInSeconds);}

        const timeDiff = new Date().getTime() - startTime;
        const totalSeconds = Math.floor(timeDiff / 1000) + elapsedTimeInSeconds;

        updateElapsedTime(game.id, totalSeconds).catch(console.error)
        return formatElapsedTime(totalSeconds);
    }

    function formatElapsedTime(elapsedTimeInSeconds) {
        const minutes = Math.floor(elapsedTimeInSeconds / 60);
        const seconds = elapsedTimeInSeconds % 60;

        return `${minutes.toString()
                         .padStart(2, '0')}:${seconds.toString()
                                                     .padStart(2, '0')}`;
    }

    function startTimer() {
        clearInterval(timerInterval);
        timerInterval = setInterval(updateGameStatus, 1000);
    }

    function stopTimer() {
        clearInterval(timerInterval);
    }

    function displayGameOverModal(header, message) {
        gameOverHeader.textContent = header;
        gameOverMessage.textContent = message;
        gameOverTime.textContent = `Time: ${elapsedTimeString}`;
        gameOverModal.classList.remove('hidden');
    }

    function endGame() {
        stopTimer();
        canvas.removeEventListener('click', handleCanvasClick);
        canvas.removeEventListener('contextmenu', handleCanvasRightClick);
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

    menuButton.addEventListener('click', () => {
        redirect(menuButton, 'Back to Games', '/game');
    });

    menu2Button.addEventListener('click', () => {
        redirect(menuButton, 'Back to Games', '/game');
    });

    newGameButton.addEventListener('click', () => {
        redirect(menuButton, 'New Game', '/game/');
    });

    closeGameOverModal.addEventListener('click', () => {
        gameOverModal.classList.add('hidden');
    });

    restartButton.addEventListener('click', () => {
        try {
            restartButton.disabled = true;
            restartButton.textContent = 'Loading...';
            removeGame(game.id).then(() => {
                newGame(game.difficulty).then((game) => {
                    window.location.replace('/game/' + game.id);
                });
            });
        } catch (error) {
            alert('Failed to restart the game. Please try again.');
            console.error('Error restarting game:', error);
        } finally {
            restartButton.disabled = false;
            restartButton.textContent = 'Restart';
        }
    });
});