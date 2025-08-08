import { fetchWithAuth, setAccessToken } from "./fetchWithAuth.js";

let userRole = null;
let username;

document.addEventListener("DOMContentLoaded", () => {
	const loginMenu = document.getElementById("loginMenu");
	const userInfoBox = document.getElementById("userInfo");
	const userNameSpan = document.getElementById("userName");
	const userStats = document.getElementById("userStats");
	const adminMenu = document.getElementById("admin-menu");

	autoLogin();

	async function autoLogin() {
		try {
			const res = await fetchWithAuth("/token", {
				method: "POST",
				credentials: "include",
			});

			if (res.ok) {
				const data = await res.json();
				setAccessToken(data.token);

				const response = await fetchWithAuth("/api/users/me", {
					method: "GET",
				});

				if (response.ok) {
					const user = await response.json();
					username = user.name;
					userRole = user.role;
					
					// 사용자 정보 표시
					if (userNameSpan) userNameSpan.textContent = `${user.name} 님`;
					if (userStats) userStats.textContent = `게시글 ${user.postCount}개 / 댓글 ${user.commentCount}개`;

					if (userInfoBox) userInfoBox.style.display = "flex";
					if (loginMenu) loginMenu.style.display = "none";
					
					if(userRole === "ROLE_ADMIN"){
						adminMenu.classList.remove("d-none");
						adminMenu.classList.add("d-block");
					}
				} else {
					resetLoginUI();
				}
			} else {
				throw new Error("토큰 재발급 실패");
			}
		} catch (e) {
			console.error("자동 로그인 실패:", e.message);
			resetLoginUI();
		}
	}

	function resetLoginUI() {
		if (userInfoBox) userInfoBox.style.display = "none";
		if (loginMenu) loginMenu.style.display = "block";
		if (userNameSpan) userNameSpan.textContent = "";
		if (userStats) userStats.textContent = "";
	}
});

// 로그아웃 버튼 누를 시
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

document.getElementById("searchBtn").addEventListener("click", (event) => {
	event.preventDefault();

	const keyword = document.getElementById("topSearchInput").value.trim();
	if (keyword) {
		location.href = `/board/list/all?keyword=${encodeURIComponent(keyword)}`;
	}
});

document.getElementById("topSearchInput").addEventListener("keydown", async (e) => {
	if (e.key === "Enter") {
		e.preventDefault();

		const keyword = document.getElementById("topSearchInput").value.trim();

		if (keyword) {
			location.href = `/board/list/all?keyword=${encodeURIComponent(keyword)}`;
		}
	}
});