import { fetchWithAuth, setAccessToken } from "/js/fetchWithAuth.js";

document.addEventListener("DOMContentLoaded", async function() {
	checkViewport();
	window.addEventListener('resize', checkViewport);
	try {
		const res = await fetch("/token", {
			method: "POST",
			credentials: "include"
		});

		if (res.ok) {
			const data = await res.json();
			setAccessToken(data.token);
			// 2. 이제 사용자 정보 요청
			const response = await fetchWithAuth("/users/me", {
				method: "GET"
			});

			if (response.ok) {
				const user = await response.json();
				document.getElementById("userName").textContent = user.name + " 님";
				document.getElementById("loginMenu").style.display = "none";
				document.getElementById("userMenu").style.display = "block";
			} else {
				document.getElementById("userName").textContent = "";
				document.getElementById("loginMenu").style.display = "block";
				document.getElementById("userMenu").style.display = "none";
			}
		} else {
			throw new Error("토큰 재발급 실패");
		}
	} catch (e) {
		console.error("자동 로그인 실패:", e);
	}
});
document.getElementById("logoutLink").addEventListener("click", async function(event) {
	event.preventDefault();
	try {
		const res = await fetchWithAuth("/logout", {
			method: "POST"
		});

		if (!res.ok) {
			throw new Error("서버 오류 발생");
		}

		setAccessToken(null);

		location.href = "/";
	} catch (e) {
		console.error(e.message);
	}
});

function checkViewport() {
	const content = document.querySelector('[data-gap-target]');
	if (!content) return;

	if (window.innerWidth <= 1440) {
		content.classList.add('gap-4');
		content.classList.remove('gap-5');
	} else {
		content.classList.add('gap-5');
		content.classList.remove('gap-4');
	}
}
