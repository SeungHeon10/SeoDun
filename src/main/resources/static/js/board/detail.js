import { fetchWithAuth } from "../fetchWithAuth.js";

const bno = window.location.pathname.split("/").pop();
let userId = null;
let name = null;
let currentSize = 5; // 현재 사이즈
let currentPage = 0; // 현재 페이지 번호
let currentSort = "createdAt"; // 현재 정렬 항목
let currentDirection = "desc"; // 현재 정렬 기준

// 페이지 로드 시
document.addEventListener("DOMContentLoaded", async () => {
	await fetchBoardDetail();
	await fetchReplyList();

	// 답글 버튼 누를 시
	document.addEventListener("click", async (e) => {
		if (e.target.classList.contains("btn-reply")) {
			const rno = e.target.dataset.rno;
			const targetComment = e.target.closest(".bb-right"); // 댓글 블록
			const existingForm = targetComment.querySelector(".reply-input-form");

			if (existingForm) {
				existingForm.remove();
			} else {
				const openForm = document.querySelector(".reply-input-form");
				if (openForm) openForm.remove();

				insertReplyInputBox(rno, targetComment); // 입력창 삽입
			}
		} else if (e.target.classList.contains("registerBtn")) { // 답글 등록 시
			const container = e.target.closest(".reply-input-form"); // 부모 div 등 지정
			const writer = container.querySelector(".reply-writer");
			const textarea = container.querySelector(".reply-content");

			const replyDTO = {
				content: textarea.value,
				writer: writer.innerText,
				user_id: writer.dataset.id,
				parent_id: writer.dataset.parent
			};

			await fetchReplyRegister(replyDTO);
		}
	});

});

// 댓글 등록 버튼 누를 시
document.getElementById("registerBtn").addEventListener("click", async (event) => {
	event.preventDefault();

	const writer = document.getElementById("reply-writer");
	const content = document.getElementById("reply-content").value;
	const userId = writer.dataset.id;

	const replyDTO = {
		content: content,
		writer: writer.innerText,
		user_id: userId
	}

	await fetchReplyRegister(replyDTO);
	await fetchBoardDetail();
	document.getElementById("reply-content").value = "";
});

// 댓글 최신순으로
document.getElementById("sort-latest").addEventListener("click", async (event) => {
	event.preventDefault();

	currentDirection = "desc";

	await fetchReplyList(currentSize, currentPage, currentSort, currentDirection);
});

// 댓글 등록순으로
document.getElementById("sort-oldest").addEventListener("click", async (event) => {
	event.preventDefault();

	currentDirection = "asc";

	await fetchReplyList(currentSize, currentPage, currentSort, currentDirection);
});

// 게시글 상세보기
async function fetchBoardDetail() {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}`);
		if (!res.ok) throw new Error("서버 오류 발생");
		const detail = await res.json();
		renderBoardDetail(detail);
	} catch (e) {
		console.error("에러:", e.message);
	}
}

// 게시글 상세정보 화면에 렌더링
function renderBoardDetail(detail) {
	console.log(detail);
	const contentTitle = document.querySelector(".content-title");
	const contentInfo = document.querySelector(".content-meta");
	const contentMain = document.querySelector(".content-main");
	const replyWriter = document.getElementById("reply-writer");
	const replybtn = document.getElementById("reply");

	const formatted = dayjs(detail.createdAt).format("YYYY-MM-DD HH:mm:ss");
	contentTitle.innerHTML = `
		<p class="mb-0">
			<strong class="text-primary">[${detail.category}]</strong>
			${detail.title}
		</p>
	`;
	contentInfo.innerHTML = `
		<li class="list-inline-item me-0">${detail.writer}</li>
		<li class="list-inline-item me-0">댓글수 : ${detail.commentCount}</li>
		<li class="list-inline-item me-0">조회수 : ${detail.viewCount}</li>
		<li class="list-inline-item text-muted">${formatted}</li>
	`;
	contentMain.textContent = `${detail.content}`;
	replybtn.textContent = `댓글 ${detail.commentCount}`;

	replyWriter.dataset.id = `${detail.userId.id}`;
	replyWriter.innerText = `${detail.userId.name}`;

	userId = `${detail.userId.id}`;
	name = `${detail.userId.name}`;
}

// 댓글 조회
async function fetchReplyList(size = currentSize, page = currentPage, sort = currentSort, direction = currentDirection) {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}/replies?size=${size}&page=${page}&sort=${sort},${direction}`);
		if (!res.ok) throw new Error("서버 오류 발생");
		const replies = await res.json();
		renderReplyList(replies.content);
		renderPagination(replies);
	} catch (e) {
		console.error("에러: ", e.message);
	}
}

// 댓글 목록을 렌더링
function renderReplyList(replies) {
	const commentList = document.querySelector(".comment-list");
	commentList.innerHTML = "";
	if (replies.length === 0) {
		commentList.innerHTML = `
			<div class="border round p-5 mb-2">
				<div class="d-flex justify-content-center">
					<p>등록된 댓글이 없습니다.</p>
				</div>
			</div>
		`;
		return;
	}

	replies.forEach(reply => {
		const divEl = document.createElement("div");
		const formatDate = dayjs(reply.createAt).format("YYYY-MM-DD HH:mm:ss");
		divEl.classList.add("p-3", "mb-2", "bb-right");
		divEl.innerHTML = `
			<div class="d-flex justify-content-between mb-3 px-2">
				<strong>${reply.writer}</strong>
				<span>${formatDate}</span>
			</div>
			<div class="d-flex justify-content-between align-items-start px-2 pb-3">
				<div class="flex-grow-1">
					<p class="mb-0">${reply.content}</p>
				</div>
				<div class="dropdown">
					<button class="btn btn-sm" type="button" data-bs-toggle="dropdown" aria-expanded="false">
						<i class="fa-solid fa-ellipsis-vertical"></i>
					</button>
					<ul class="dropdown-menu dropdown-menu-end">
						<li><a class="dropdown-item" href="#">수정</a></li>
						<li><a class="dropdown-item text-danger" href="#">삭제</a></li>
					</ul>
				</div>
			</div>
			<div class="d-flex justify-content-between align-items-center mt-2 px-2">
				<button type="button" class="btn btn-outline-secondary btn-reply" data-rno=${reply.rno}>답글</button>
				<div class="d-flex gap-2">
					<button type="button" class="btn btn-outline-light border btn-sm d-flex align-items-center gap-1">
						<i class="fa-regular fa-thumbs-up text-danger"></i>
						<span class="text-danger">0</span>
					</button>
					<button type="button" class="btn btn-outline-light border btn-sm d-flex align-items-center gap-1">
						<i class="fa-regular fa-thumbs-down text-primary"></i>
						<span class="text-primary">0</span>
					</button>
				</div>
			</div>
		`;
		commentList.appendChild(divEl);
	});
}

// 댓글 등록
async function fetchReplyRegister(replyDTO) {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}/replies`, {
			method: "POST",
			body: JSON.stringify(replyDTO)
		});
		if (!res.ok) {
			showToast("❗ 댓글등록에 실패했습니다. 다시 시도해주세요.", "error");
			return;
		}
		const result = await res.text();
		showToast("✔️ " + result, "success");
		await fetchReplyList();
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// Toastify 알림 호출
function showToast(message, type) {
	const minWidth = type === "success" ? "340px" : "530px";
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
			gap: "31%",
			minWidth: minWidth,
			whiteSpace: "nowrap"
		}
	}).showToast();
}

// 페이지네이션 렌더링
function renderPagination(pageInfo) {
	const pagination = document.getElementById("pagination");
	pagination.innerHTML = "";

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
				await fetchReplyList(currentSize, parseInt(targetPage)); // 페이지 이동
			}
		});
	});
}

// 답글창 생성
function insertReplyInputBox(rno, commentBox) {
	const divEl = document.createElement("div");
	divEl.classList.add("pt-3", "mt-3", "border-top", "reply-input-form");
	divEl.innerHTML = `
		<div class="mb-3 input-reply">
			<label class="form-label mb-2 text-muted ms-cus"><strong class="reply-writer"
					data-id="${userId}" data-parent="${rno}">${name}</strong></label>
			<textarea class="form-control reply-content" rows="3"
				placeholder="댓글을 입력하세요..."></textarea>
		</div>
	
		<div class="d-flex justify-content-end">
			<button type="button" class="btn btn-outline-secondary registerBtn">등록</button>
		</div>
	`;

	commentBox.appendChild(divEl);
}
