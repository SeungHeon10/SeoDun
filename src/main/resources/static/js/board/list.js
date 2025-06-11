import { fetchWithAuth } from "../fetchWithAuth.js";

const tbody = document.getElementById("list");
let currentSize = 10;
let currentPage = 0;

document.addEventListener("DOMContentLoaded", async () => {
	const dropdownButton = document.getElementById("pageSizeDropdown");
	const dropdownItems = document.querySelectorAll(".dropdown-item");
	const pagination = document.querySelectorAll(".page-link");

	await fetchBoardList();

	dropdownItems.forEach(item => {
		item.addEventListener("click", async (e) => {
			e.preventDefault();

			const selectedText = item.textContent;
			const selectedValue = item.dataset.value;

			// 드롭다운 버튼 텍스트 변경
			dropdownButton.textContent = selectedText;

			// 드롭다운 active 클래스 처리
			dropdownItems.forEach(i => i.classList.remove("active", "text-success"));
			item.classList.add("active", "text-success");

			currentSize = selectedValue;
			await fetchBoardList(currentSize, currentPage);
		});
	});
	
	pagination.forEach(page => {
		page.addEventListener("click", async (e) => {
			e.preventDefault();
			const targetPage = parseInt(e.target.dataset.page);

			if (!isNaN(targetPage)) {
				currentPage = targetPage; // 전역 페이지 업데이트
				await fetchBoardList(currentSize, currentPage);
			}
		});
	});
});

async function fetchBoardList(size = currentSize, page = 0) {
	// 서버에 pageSize 전달하여 fetch
	const result = await doFetch(size, page);
	// 새 게시글 렌더링
	renderBoardList(result.content);
	renderPagination(result);
}

async function doFetch(size, page) {
	try {
		const response = await fetchWithAuth(`/api/boards?size=${size}&page=${page}`);

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
  const pagination = document.getElementById("pagination");
  pagination.innerHTML = "";

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
