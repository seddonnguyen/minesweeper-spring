document.addEventListener('DOMContentLoaded', () => {
    const tblContent = document.querySelector('.tbl-content');
    const tblHeader = document.querySelector('.tbl-header');
    const newGameButton = document.getElementById('newGameButton');

    const adjustTableHeaderPadding = () => {
        const scrollWidth = tblContent.offsetWidth - tblContent.querySelector('table').offsetWidth;
        tblHeader.style.paddingRight = `${scrollWidth}px`;
    };

    window.addEventListener('resize', adjustTableHeaderPadding);
    adjustTableHeaderPadding();


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

    newGameButton.addEventListener('click', () => {
        redirect(newGameButton, 'New Game', '/game/');
    });
});

function deleteGame(gameId) {
    if (confirm("Are you sure you want to delete this game?")) {
        fetch(`/game/${gameId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert("Failed to delete the game.");
                }
            })
            .catch(error => console.error('Error:', error));
    }
}