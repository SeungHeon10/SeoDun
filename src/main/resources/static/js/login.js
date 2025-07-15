import { setAccessToken, fetchWithAuth } from "./fetchWithAuth.js";

document.getElementById("login").addEventListener("click", async function(event) {
	event.preventDefault();

	try {
		const username = document.querySelector('input[name="username"]').value;
		const password = document.querySelector('input[name="password"]').value;
		const rememberMe = document.getElementById("remember_login").checked;

		const loginDTO = {
			id: username,
			password: password,
			rememberMe: rememberMe
		}

		const response = await fetchWithAuth("/login", {
			method: "POST",
			body: JSON.stringify(loginDTO)
		});

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}
		const token = await response.json();
		setAccessToken(token.token);

		location.href = "/";
	} catch (e) {
		console.error(e.message);
	}

});

document.getElementById("kakao_login").addEventListener("click", function(e) {
	e.preventDefault();
	
	window.location.href = "/oauth2/authorization/kakao";
});
