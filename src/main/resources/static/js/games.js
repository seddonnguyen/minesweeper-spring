document.addEventListener("DOMContentLoaded", function () {
    // Add any JavaScript functionality here
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