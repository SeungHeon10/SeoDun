import { fetchWithAuth, setAccessToken } from "/js/fetchWithAuth.js";

// 페이지 로드 시 
document.addEventListener("DOMContentLoaded", async function() {
	checkViewport();
	window.addEventListener('resize', checkViewport);
	await popularBoard();
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

// 해상도 체크 
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

// 인기글 검색 함수
async function popularBoard() {
	const popularDiv = document.getElementById("popularBox");

	try {
		const response = await fetchWithAuth("/api/boards/popular");

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		const boards = await response.json();

		boards.forEach(board => {
			const divEl = document.createElement("div");
			divEl.innerHTML = `
			<a href="#" class="text-decoration-none text-dark">
				<div class="post-card">
					<div class="post-title">${board.title}</div>
					<div class="post-preview">${board.content}</div>
					<div class="post-meta">
						<i class="fas fa-eye"></i>
						<span>${board.viewCount}</span>
					</div>
				</div>
			</a>
			`;

			popularDiv.appendChild(divEl);
		});

	} catch (e) {
		console.error(e.message);
	}
}

// 메인 화면 게시판 항목에 따른 게시글 보여주기
function loadBoardsByCategory() {
	const tabList = document.querySelectorAll('[data-category]');
	const boardList = document.getElementById("boardList");

	tabList.forEach(tab => {
		tab.addEventListener("mouseenter", async () => {
			const category = tab.dataset.category;
			try {
				const response = await fetchWithAuth("api/boards/category/{category}");

				if (!response.ok) {
					throw new Error("서버 오류 발생");
				}
				
				const boards = await response.json();
				
				boards.forEach(board => {
					const li = document.createElement("li");
					li.classList.add(
					  "list-group-item",
					  "d-flex",
					  "justify-content-between",
					  "align-items-center",
					  "back-color-light10"
					);
					
					li.innerHTML = `
					${board.title} [${board.commentCount}]
					<span class="badge bg-secondary rounded-pill back-color-light30">${board.viewCount}</span>
					`;
					
					boardList.appendChild(li);
				});
			} catch (e) {
				console.error(e.message);
			}
		});
	});
}