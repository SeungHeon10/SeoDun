import { setAccessToken } from "/js/core/fetchWithAuth.js";

document.getElementById("login").addEventListener("click", async function(event) {
	event.preventDefault();

	try {
		const username = document.querySelector('input[name="username"]');
		const password = document.querySelector('input[name="password"]');
		const rememberMe = document.getElementById("remember_login").checked;

		if (!username.value) { username.focus(); return }
		if (!password.value) { password.focus(); return }

		const loginDTO = {
			id: username.value,
			password: password.value,
			rememberMe: rememberMe
		}

		const response = await fetch("/login", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(loginDTO)
		});

		if (!response.ok) {
			if (response.status === 401) {
				const msg = await response.text();
				showToast(msg || "아이디/비밀번호를 확인해주세요.", "error");
				username.classList.add("is-invalid");
				password.classList.add("is-invalid");
				return;
			} else {
				throw new Error("서버 오류 발생");
			}
		}
		console.log("ok뜸")

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

// Toastify 알림 호출
function showToast(message, type) {
	Toastify({
		text: message,
		duration: 2000,
		gravity: "bottom",
		position: "center",
		close: true,
		escapeMarkup: false,
		style: {
			background: type === "success" ? "#d4edda" : "rgb(249, 226, 230)",
			color: type === "success" ? "#155724" : "rgb(83, 14, 26)",
			fontSize: "15px",
			borderRadius: "8px",
			border: "none",
			boxShadow: "none",
			padding: "12px 18px",
			display: "flex",
			alignItems: "center",
			whiteSpace: "nowrap",
			gap: "50px"
		}
	}).showToast();
}
