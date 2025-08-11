import { setAccessToken, fetchWithAuth } from "/js/core/fetchWithAuth.js";

window.addEventListener("load", async () => {
	try {
		const response = await fetchWithAuth("/token", {
			method: "POST",
			credentials: "include"
		});

		if (response.ok) {
			const data = await response.json();
			setAccessToken(data.token);
			location.href = "/";
		} else {
			throw new Error("토큰 재발급 실패");
		}
	} catch (e) {
		alert("로그인 실패! 다시 로그인해주세요.");
		location.href = "/login";
	}
});