import { fetchWithAuth, setAccessToken, isAuthenticated } from "/js/core/fetchWithAuth.js";

document.addEventListener("DOMContentLoaded", async () => {
	const loginMenu = document.getElementById("loginMenu");
	const userInfoBox = document.getElementById("userInfo");
	const userNameSpan = document.getElementById("userName");
	const userStats = document.getElementById("userStats");
	const adminMenu = document.getElementById("admin-menu");

	// 기본 UI
	if (loginMenu) loginMenu.style.display = "block";
	if (userInfoBox) userInfoBox.style.display = "none";
	
	// 토큰 있을 때만 me 호출
	if (!isAuthenticated()) return;
	
	try {
		const res = await fetchWithAuth("/api/users/me", { method: "GET" });
		if (!res.ok) throw new Error("로그인 되어있지 않음");

		const user = await res.json();

		// UI 업데이트
		if (loginMenu) loginMenu.style.display = "none";
		if (userInfoBox) userInfoBox.style.display = "flex";
		if (userNameSpan) userNameSpan.textContent = `${user.nickname} 님`;
		if (userStats) userStats.textContent = `게시글 ${user.postCount}개 / 댓글 ${user.commentCount}개`;

		if (user.role === "ROLE_ADMIN" && adminMenu) {
			adminMenu.classList.remove("d-none");
			adminMenu.classList.add("d-block");
		}
	} catch (err) {
		// 로그인 안 되어 있으면 로그인 UI 표시
		if (loginMenu) loginMenu.style.display = "block";
		if (userInfoBox) userInfoBox.style.display = "none";
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

// 상단바에서 검색버튼 누를 시
document.getElementById("searchBtn").addEventListener("click", (event) => {
	event.preventDefault();

	const keyword = document.getElementById("topSearchInput").value.trim();
	if (keyword) {
		location.href = `/board/list/all?keyword=${encodeURIComponent(keyword)}`;
	}
});

// 상단바에서 검색어 입력 후 엔터 누를 시
document.getElementById("topSearchInput").addEventListener("keydown", async (e) => {
	if (e.key === "Enter") {
		e.preventDefault();

		const keyword = document.getElementById("topSearchInput").value.trim();

		if (keyword) {
			location.href = `/board/list/all?keyword=${encodeURIComponent(keyword)}`;
		}
	}
});