import { fetchWithAuth, isAuthenticated } from "/js/core/fetchWithAuth.js";

let activeTab = document.querySelector('[data-category="전체"]'); // 선택된 탭 
let nickname = null;

const categoryNames = {
	자유: "free",
	학습: "study",
	질문답변: "qna",
	정보공유: "share",
};

// 페이지 로드 시 
document.addEventListener("DOMContentLoaded", async function() {
	await loadLoginUser();
	checkViewport();
	window.addEventListener('resize', checkViewport);
	await popularBoard();
	await loadInitialBoards();
	loadBoardsByCategory();
	await recentBoards();
	await loadTopTags();
	await loadReadBasedRecommendations();

	document.addEventListener('click', async (e) => {
		const a = e.target.closest('a.board-title-link');
		if (!a) return;
		e.preventDefault();

		try {
			const res = await fetchWithAuth('/api/users/me', {
				skipRefresh: true,
			});

			if (res.status === 200) {
				location.href = a.href;
			} else if (res.status === 401) {
				alert('로그인이 필요합니다.');
				const loginUrl = '/login';
				location.href = loginUrl;
			} else {
				showToast(`일시적 오류가 발생했습니다. (${res.status})`, 'error');
			}
		} catch (err) {
			showToast('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');
		}
	});
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
		popularDiv.innerHTML = "";

		boards.forEach(board => {
			const divEl = document.createElement("div");
			divEl.innerHTML = `
			<a href="board/detail/${categoryNames[board.category]}/${board.bno}" class="text-decoration-none text-dark board-title-link">
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
		console.error("에러:", e.message);
	}
}

// 메인 화면 게시판 항목에 따른 게시글 보여주기
function loadBoardsByCategory() {
	const tabList = document.querySelectorAll('[data-category]');
	const boardList = document.getElementById("boardList");

	tabList.forEach(tab => {
		tab.addEventListener("mouseenter", async () => {
			const category = tab.dataset.category;

			if (activeTab) {
				activeTab.classList.remove("tab-selected");
			}

			tab.classList.add("tab-selected");
			activeTab = tab;

			try {
				const response = await fetchWithAuth(`api/boards/category/${category}`);

				if (!response.ok) {
					throw new Error("서버 오류 발생");
				}

				const boards = await response.json();
				boardList.innerHTML = "";

				boards.forEach(board => {
					const hasImage = board.content.includes("<img");
					const iconHTML = hasImage ? '<i class="bi bi-card-image text-muted"></i>' : '';
					const li = document.createElement("li");
					li.classList.add(
						"list-group-item",
						"d-flex",
						"justify-content-between",
						"align-items-center",
						"back-color-light10"
					);

					li.innerHTML = `
						<a href="board/detail/${categoryNames[board.category]}/${board.bno}" class="text-decoration-none text-dark board-title-link">
						${board.title} ${iconHTML} [${board.commentCount}]
						</a>
						<span class="badge bg-secondary rounded-pill back-color-light30">${board.viewCount}</span>
					`;

					boardList.appendChild(li);
				});
			} catch (e) {
				console.error("에러:", e.message);
			}
		});
	});
}

// 초기 전체 게시글 가져오기
async function loadInitialBoards() {
	const category = "전체";
	const boardList = document.getElementById("boardList");

	// 초기 "전체" 탭에 select 효과주기
	activeTab.classList.add("tab-selected");

	try {
		const response = await fetchWithAuth(`api/boards/category/${category}`);

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		const boards = await response.json();
		boardList.innerHTML = "";

		boards.forEach(board => {
			const hasImage = board.content.includes("<img");
			const iconHTML = hasImage ? '<i class="bi bi-card-image text-muted"></i>' : '';
			const li = document.createElement("li");
			li.classList.add(
				"list-group-item",
				"d-flex",
				"justify-content-between",
				"align-items-center",
				"back-color-light10"
			);

			li.innerHTML = `
				<a href="board/detail/${categoryNames[board.category]}/${board.bno}" class="text-decoration-none text-dark board-title-link">
				${board.title} ${iconHTML} [${board.commentCount}]
				</a>
				<span class="badge bg-secondary rounded-pill back-color-light30">${board.viewCount}</span>
			`;

			boardList.appendChild(li);
		});
	} catch (e) {
		console.error("초기 로딩 에러:", e.message);
	}
}

// 최신 글 가져오기
async function recentBoards() {
	const recentBoard = document.getElementById("recentBoard");
	try {
		const response = await fetchWithAuth("api/boards/recent");

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		const boards = await response.json();

		boards.forEach(board => {
			const divEl = document.createElement("div");
			divEl.innerHTML = `
						<a href="board/detail/${categoryNames[board.category]}/${board.bno}" class="text-decoration-none text-dark board-title-link">
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

			recentBoard.appendChild(divEl);
		});
	} catch (e) {
		console.error("에러:", e.message);
	}
}

// 사용자 맞춤 컨텐츠
async function loadReadBasedRecommendations() {
	const readBasedRecommend = document.getElementById("readBasedRecommend");
	const recommendTitle = document.getElementById("recommendTitle");

	if (!isAuthenticated()) {
		await loadPublic();
		return;
	}

	try {
		const response = await fetchWithAuth("/api/recommend/read-based");

		if (!response.ok) {
			throw new Error(`서버 오류: ${response.status}`);
		}

		const boards = await response.json();

		recommendTitle.innerHTML = "";
		// 추천 메시지 설정
		if (nickname !== null) {
			recommendTitle.innerHTML += `🧠 <span class="fw-bold">${nickname}</span> 님이 읽은 글과 비슷한 게시글`;
		} else {
			recommendTitle.textContent += "🌟 회원님을 위한 오늘의 스몰 큐레이션";
		}

		if (boards.length < 2) {
			await loadPublic();
			return;
		}

		readBasedRecommend.innerHTML = "";

		boards.forEach(board => {
			const divEl = document.createElement("div");
			divEl.innerHTML = `
						<a href="board/detail/${categoryNames[board.category]}/${board.bno}" class="text-decoration-none text-dark board-title-link">
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

			readBasedRecommend.appendChild(divEl);
		});
	} catch (err) {
		console.error("❗ 추천 API 호출 실패:", err);
		readBasedRecommend.innerHTML = "<p class='text-danger'>추천 게시글을 불러오지 못했습니다.</p>";
	}
}

// 인기 태그 가져오기
async function loadTopTags() {
	try {
		const res = await fetchWithAuth("/api/boards/tags/popular");

		if (!res.ok) {
			throw new Error("서버 오류 발생");
		}

		const tags = await res.json();
		const container = document.getElementById("topTags");

		tags.forEach(tag => {
			const span = document.createElement("span");
			span.className = "badge me-4 mb-3 fs-1 custom-tag";
			span.textContent = `#${tag.tag}`;
			container.appendChild(span);
		});
	} catch (error) {
		console.error("인기 태그를 불러오는 중 오류 발생:", error);
	}
}

// 로그인 사용자 정보 가져오기
async function loadLoginUser() {
	if (!isAuthenticated()) return;

	try {
		const res = await fetchWithAuth("/api/users/me", {
			method: "GET",
			credentials: "include"
		});

		if (!res.ok) {
			const msg = await res.text();
			throw new Error(`(${res.status}) 사용자 정보를 가져올 수 없습니다. → ${msg}`);
		}

		const data = await res.json();

		nickname = data.nickname;
	} catch (e) {
		console.error("로그인 사용자 확인 실패:", e);
	}
}

async function loadPublic() {
	const readBasedRecommend = document.getElementById("readBasedRecommend");
	const recommendTitle = document.getElementById("recommendTitle");

	try {
		const response = await fetchWithAuth("/api/recommend/public");

		if (!response.ok) {
			throw new Error(`서버 오류: ${response.status}`);
		}

		const boards = await response.json();

		if (boards.length === 0) {
			readBasedRecommend.innerHTML = "<p>아직 추천할 게시글이 없습니다.</p>";
			return;
		}

		recommendTitle.innerHTML = "";
		// 추천 메시지 설정
		if (nickname !== null) {
			recommendTitle.innerHTML += `🧠 <span class="fw-bold">${nickname}</span> 님을 위한 오늘의 스몰 큐레이션`;
		} else {
			recommendTitle.textContent += "🌟 회원님을 위한 오늘의 스몰 큐레이션";
		}

		readBasedRecommend.innerHTML = "";

		boards.forEach(board => {
			const divEl = document.createElement("div");
			divEl.innerHTML = `
						<a href="board/detail/${categoryNames[board.category]}/${board.bno}" class="text-decoration-none text-dark board-title-link">
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

			readBasedRecommend.appendChild(divEl);
		});
	} catch (err) {
		console.error("❗ 추천 API 호출 실패:", err);
		readBasedRecommend.innerHTML = "<p class='text-danger'>추천 게시글을 불러오지 못했습니다.</p>";
	}
}