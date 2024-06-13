document.addEventListener('DOMContentLoaded', () => {

    const loginButton = document.getElementById('login');
    const username = document.getElementById('username');
    const password = document.getElementById('password');

    async function Login() {
        const response = await fetch(`/login`, {

            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify({
                                        username: `${username.value}`, password: `${password.value}`
                                    })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }

    loginButton.addEventListener('click', () => {
        try {
            Login().then(data => {
                console.log(data);
                window.location.replace('/');
            });
        } catch (error) {
            console.error('Error:', error);
        }
    });
});