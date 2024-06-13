document.addEventListener('DOMContentLoaded', () => {

    const registerButton = document.getElementById('register');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const firstname = document.getElementById('firstname');
    const lastname = document.getElementById('lastname');
    const email = document.getElementById('email');

    async function Login() {
        const response = await fetch(`/register`, {

            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify({
                                        username: `${username.value}`,
                                        password: `${password.value}`,
                                        firstname: `${firstname.value}`,
                                        lastname: `${lastname.value}`,
                                        email: `${email.value}`
                                    })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }

    registerButton.addEventListener('click', () => {
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