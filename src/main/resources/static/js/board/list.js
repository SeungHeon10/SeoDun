import { fetchWithAuth } from "/js/core/fetchWithAuth.js";

const params = new URLSearchParams(location.search);
const tbody = document.getElementById("list");
const searchModePanel = document.getElementById("searchModePanel");
const pathParts = window.location.pathname.split('/');
const category = pathParts[3];
const isAdminPage = location.pathname.includes("/admin");
const apiSuffix = isAdminPage ? "/api/boards/admin" : "/api/boards";
let currentSize = 10; // 현재 사이즈
let currentPage = 0; // 현재 페이지 번호
let currentSort = "createdAt"; // 현재 정렬 항목
let currentDirection = "desc"; // 현재 정렬 기준
let currentSearchMode = "title"; // 현재 검색 모드
let currentKeyword = params.get("keyword") || "";

// 페이지 로드 시
document.addEventListener("DOMContentLoaded", async () => {
	const dropdownButton = document.getElementById("pageSizeDropdown");
	const dropdownItems = document.querySelectorAll("a[data-value]");
	const pagination = document.querySelectorAll(".page-link");
	const dropdownSortButton = document.getElementById("sortDropdown");
	const dropdownSortItems = document.querySelectorAll("a[data-sort]");
	const modeItems = document.querySelectorAll(".mode-item");
	const searchInput = document.getElementById("searchInput");

	const placeholderMap = {
		title: "제목으로 검색",
		title_content: "제목 + 본문으로 검색",
		content: "본문 내용으로 검색",
		writer: "닉네임으로 검색"
	};

	const categoryNames = {
		free: "자유",
		study: "학습",
		qna: "질문답변",
		share: "정보공유",
	};

	const titleElement = document.getElementById("category-title");
	titleElement.textContent = categoryNames[category] || "전체글보기";

	const urlParams = new URLSearchParams(window.location.search);
	if (urlParams.get("deleted") === "true") {
		showToast("✔️ 게시글이 삭제되었습니다.", "success");
		window.history.replaceState({}, document.title, window.location.pathname);
	} else if (urlParams.get("register") === "true") {
		showToast("✔️ 게시글이 등록되었습니다.", "success");
		window.history.replaceState({}, document.title, window.location.pathname);
	} else if (urlParams.get("restore") === "true") {
		showToast("✔️ 게시글이 복원되었습니다.", "success");
		window.history.replaceState({}, document.title, window.location.pathname);
	}

	await fetchBoardList(currentSize, currentPage, currentSort, currentDirection, currentSearchMode, currentKeyword);
	handleWriteButtonClick();

	//	페이지 사이즈 변경 시
	dropdownItems.forEach(item => {
		item.addEventListener("click", async (e) => {
			e.preventDefault();

			const selectedText = item.textContent;
			const selectedValue = item.dataset.value;

			dropdownButton.textContent = selectedText;

			dropdownItems.forEach(i => i.classList.remove("active", "text-success"));
			item.classList.add("active", "text-success");

			currentSize = selectedValue;
			await fetchBoardList(currentSize, currentPage, currentSort, currentDirection, currentSearchMode, currentKeyword);
		});
	});

	//	정렬 변경 시
	dropdownSortItems.forEach(item => {
		item.addEventListener("click", async (e) => {
			e.preventDefault();

			const selectedText = item.textContent;
			const selectedSort = item.dataset.sort;
			const selectedDirection = item.dataset.direction;

			dropdownSortButton.textContent = selectedText;

			dropdownSortItems.forEach(i => i.classList.remove("active", "text-success"));
			item.classList.add("active", "text-success");

			currentSort = selectedSort;
			currentDirection = selectedDirection;
			await fetchBoardList(currentSize, 0, currentSort, currentDirection, currentSearchMode, currentKeyword);
		});
	});

	//	페이지 번호 변경 시
	pagination.forEach(page => {
		page.addEventListener("click", async (e) => {
			e.preventDefault();

			const targetPage = parseInt(e.target.dataset.page);

			if (!isNaN(targetPage)) {
				currentPage = targetPage;
				await fetchBoardList(currentSize, currentPage, currentSort, currentDirection, currentSearchMode, currentKeyword);
			}
		});
	});

	//	검색 기준 변경 시 
	modeItems.forEach(item => {
		item.addEventListener("click", (e) => {
			e.preventDefault();

			document.querySelectorAll(".mode-item").forEach(i => i.classList.remove("active"));
			item.classList.add("active");

			currentSearchMode = item.dataset.mode;

			const newPlaceholder = placeholderMap[currentSearchMode] || "검색어를 입력하세요";
			searchInput.placeholder = newPlaceholder;

			searchModePanel.classList.remove("show");
		});
	});

	//	검색어 입력 후 엔터 누를 시
	searchInput.addEventListener("keydown", async (e) => {
		if (e.key === "Enter") {
			e.preventDefault();

			currentKeyword = searchInput.value.trim();
			currentPage = 0;

			await fetchBoardList(currentSize, currentPage, currentSort, currentDirection, currentSearchMode, currentKeyword);
		}
	});

	//	검색어 입력 후 검색버튼 누를 시
	document.getElementById("search-btn").addEventListener("click", async (e) => {
		e.preventDefault();

		currentKeyword = searchInput.value.trim();
		currentPage = 0;

		await fetchBoardList(currentSize, currentPage, currentSort, currentDirection, currentSearchMode, currentKeyword);
	});

	// 검색 메뉴 버튼 누를 시
	document.getElementById("search-menu").addEventListener("click", (event) => {
		event.preventDefault();

		searchModePanel.classList.toggle("show");

		document.addEventListener("click", (e) => {
			// 클릭 대상이 toggle 버튼이나 패널 내부가 아니면 닫기
			if (!searchModePanel.contains(e.target) && !document.getElementById("search-menu").contains(e.target)) {
				searchModePanel.classList.remove("show");
			}
		});
	});

	// 제목 클릭해 상세보기 진입 시 로그인 여부 체크
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


async function fetchBoardList(size = currentSize, page = 0, sort = currentSort, direction = currentDirection, mode = "title", keyword = "") {
	// 서버에 pageSize 전달하여 fetch
	const result = await doFetch(size, page, sort, direction, mode, keyword);
	// 새 게시글 렌더링
	renderBoardList(result.content);
	renderPagination(result);
}

async function doFetch(size, page, sort, direction, mode, keyword) {
	const sortParam = isAdminPage ? `&sort=${sort}&direction=${direction}` : `&sort=${sort},${direction}`;
	try {
		const response = await fetchWithAuth(`${apiSuffix}?size=${size}&page=${page}${sortParam}&mode=${mode}&keyword=${encodeURIComponent(keyword || "")}&category=${category}`);

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		return await response.json();
	} catch (e) {
		console.error(e.message);
	}

}

function renderBoardList(boards) {
	const categoryTitleElement = document.getElementById("category-title");

	if (isAdminPage) {
		categoryTitleElement.textContent = "게시글 관리";
	}

	tbody.innerHTML = "";
	if (boards.length === 0) {
		const tr = document.createElement("tr");

		tr.innerHTML = `
							<td colspan="5" class="text-center text-muted py-4"><i class="bi bi-search me-2"></i> 검색된 결과가 없습니다.</td>
						`;
		tbody.appendChild(tr);

		return;
	}

	boards.forEach(board => {
		const hasImage = board.content.includes("<img");

		const iconHTML = hasImage ? '<i class="bi bi-card-image text-muted"></i>' : '';
		const tr = document.createElement("tr");

		if (board.deleted) {
			tr.classList.add("deleted-row");
			tr.title = "삭제된 게시글입니다";
		}

		if (isAdminPage) {
			document.getElementById("writeBtn")?.classList.add("d-none");
		}

		const formatDate = new Date(board.createdAt).toLocaleDateString("ko-KR");

		if (!isAdminPage) {
			tr.innerHTML = `
								<td style="text-align: center;">${board.category}</td>
								<td>
									<a href="/board/detail/${category}/${board.bno}" class="board-title-link">
										${board.title}${iconHTML}
										<span class="commentCount">[${board.commentCount}]</span>
									</a>
								</td>
								<td style="text-align: center;">${board.userId.nickname}</td>
								<td style="text-align: center;">${formatDate}</td>
								<td style="text-align: center;">${board.viewCount}</td>
							`;
		} else {
			tr.innerHTML = `
								<td style="text-align: center;">${board.category}</td>
								<td>
									<a href="/board/detail/admin/${board.bno}" class="board-title-link">
										${board.title}${iconHTML}
										<span class="commentCount">[${board.commentCount}]</span>
									</a>
								</td>
								<td style="text-align: center;">${board.userId.nickname}</td>
								<td style="text-align: center;">${formatDate}</td>
								<td style="text-align: center;">${board.viewCount}</td>
							`;
		}

		tbody.appendChild(tr);
	});
}

function renderPagination(pageInfo) {
	const boardCount = document.getElementById("count-number");
	const pagination = document.getElementById("pagination");
	pagination.innerHTML = "";

	boardCount.textContent = pageInfo.totalElements;

	const { number, totalPages, first, last } = pageInfo;

	// 이전 버튼
	const prevDisabled = first ? "disabled" : "";
	pagination.innerHTML += `
    <li class="page-item ${prevDisabled}">
      <a class="page-link" href="#" data-page="${number - 1}">← Previous</a>
    </li>
  `;

	// 페이지 숫자 버튼들
	for (let i = 0; i < totalPages; i++) {
		const active = i === number ? "active" : "";
		pagination.innerHTML += `
      <li class="page-item ${active}">
        <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
      </li>
    `;
	}

	// 다음 버튼
	const nextDisabled = last ? "disabled" : "";
	pagination.innerHTML += `
    <li class="page-item ${nextDisabled}">
      <a class="page-link" href="#" data-page="${number + 1}">Next →</a>
    </li>
  `;

	// 페이지 클릭 이벤트 등록
	document.querySelectorAll(".page-link").forEach(link => {
		link.addEventListener("click", async e => {
			e.preventDefault();
			const targetPage = e.target.dataset.page;
			if (targetPage !== undefined) {
				await fetchBoardList(currentSize, parseInt(targetPage), currentSort, currentDirection, currentSearchMode, currentKeyword); // 페이지 이동
			}
		});
	});
}

function handleWriteButtonClick() {
	const writeBtn = document.getElementById("writeBtn");

	if (writeBtn) {
		writeBtn.addEventListener("click", async () => {
			const pathParts = window.location.pathname.split('/');
			const category = pathParts[3]; // /board/list/{category}
			let writeUrl = "/";
			const loginUrl = '/login';

			if (category) {
				writeUrl = `/board/register/${category}`;
			} else {
				writeUrl = `/board/register`;
			}

			try {
				const res = await fetchWithAuth('/api/users/me', { skipRefresh: true });

				if (res.status === 200) {
					location.href = writeUrl;
				} else if (res.status === 401) {
					alert('로그인이 필요합니다.');
					location.href = loginUrl;
				} else {
					showToast(`일시적 오류가 발생했습니다. (${res.status})`, 'error');
				}
			} catch (e) {
				showToast('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', "error");
			}

		});
	}
}

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
