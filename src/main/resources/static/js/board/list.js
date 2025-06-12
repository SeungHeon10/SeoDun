import { fetchWithAuth } from "../fetchWithAuth.js";

const tbody = document.getElementById("list");
const searchModePanel = document.getElementById("searchModePanel");
let currentSize = 10; // 현재 사이즈
let currentPage = 0; // 현재 페이지 번호
let currentSort = "createdAt"; // 현재 정렬 항목
let currentDirection = "desc"; // 현재 정렬 기준
let currentSearchMode = "title"; // 현재 검색 모드
let currentKeyword = "";

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
		writer: "작성자 이름으로 검색"
	};

	await fetchBoardList();

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
			await fetchBoardList(currentSize, currentPage);
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
			await fetchBoardList(currentSize, 0, currentSort, currentDirection);
		});
	});

	//	페이지 번호 변경 시
	pagination.forEach(page => {
		page.addEventListener("click", async (e) => {
			e.preventDefault();

			const targetPage = parseInt(e.target.dataset.page);

			if (!isNaN(targetPage)) {
				currentPage = targetPage;
				await fetchBoardList(currentSize, currentPage);
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
	document.getElementById("search-btn").addEventListener("click", async () => {
		currentKeyword = searchInput.value.trim();
		currentPage = 0;

		await fetchBoardList(currentSize, currentPage, currentSort, currentDirection, currentSearchMode, currentKeyword);
	});
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

async function fetchBoardList(size = currentSize, page = 0, sort = currentSort, direction = currentDirection, mode = "title", keyword = "") {
	// 서버에 pageSize 전달하여 fetch
	const result = await doFetch(size, page, sort, direction, mode, keyword);
	// 새 게시글 렌더링
	renderBoardList(result.content);
	renderPagination(result);
}

async function doFetch(size, page, sort, direction, mode, keyword) {
	try {
		const response = await fetchWithAuth(`/api/boards?size=${size}&page=${page}&sort=${sort},${direction}&mode=${mode}&keyword=${encodeURIComponent(keyword || "")}`);

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		return await response.json();
	} catch (e) {
		console.error(e.message);
	}

}

function renderBoardList(boards) {
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
		const tr = document.createElement("tr");
		const formatDate = new Date(board.createdAt).toLocaleDateString("ko-KR");

		tr.innerHTML = `
							<td style="text-align: center;">${board.category}</td>
							<td>
								<a href="detail" class="board-title-link">
									${board.title}
									<p class="commentCount">[${board.commentCount}]</p>
								</a>
							</td>
							<td style="text-align: center;">${board.writer}</td>
							<td style="text-align: center;">${formatDate}</td>
							<td style="text-align: center;">${board.viewCount}</td>
						`;

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
      <a class="page-link" href="#" data-page="${number - 1}">&laquo;</a>
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
      <a class="page-link" href="#" data-page="${number + 1}">&raquo;</a>
    </li>
  `;

	// 페이지 클릭 이벤트 등록
	document.querySelectorAll(".page-link").forEach(link => {
		link.addEventListener("click", async e => {
			e.preventDefault();
			const targetPage = e.target.dataset.page;
			if (targetPage !== undefined) {
				await fetchBoardList(currentSize, parseInt(targetPage)); // 페이지 이동
			}
		});
	});
}
