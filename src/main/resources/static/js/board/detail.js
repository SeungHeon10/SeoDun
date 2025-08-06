import { fetchWithAuth } from "../fetchWithAuth.js";

const bno = window.location.pathname.split("/").pop();
const pathParts = window.location.pathname.split('/');
const category = pathParts[3];
const isAdminPage = location.pathname.includes("/admin");
const apiSuffix = isAdminPage ? `/api/boards/admin/${bno}` : `/api/boards/${bno}`;
let name = null;
let isAdmin = false;
let currentUser = null;
let currentSize = 5; // 현재 사이즈
let currentPage = 0; // 현재 페이지 번호
let currentSort = "createdAt"; // 현재 정렬 항목
let currentDirection = "desc"; // 현재 정렬 기준
let currentMode = "view";
let editor;
let enterTime;
let interactionCount = 0;
let currentBoardId = pathParts[4];

// 페이지 로드 시
document.addEventListener("DOMContentLoaded", async () => {
	await loadLoginUser();
	await fetchBoardDetail();
	await fetchReplyList();
	startDwellTracking();

	const categoryNames = {
		free: "자유",
		study: "학습",
		qna: "질문답변",
		share: "정보공유",
	};

	const titleElement = document.getElementById("category-title");
	const anchorEl = titleElement.closest("a");
	window.history.replaceState({}, document.title, window.location.pathname);

	if (!isAdminPage) {
		if (category && categoryNames[category]) {
			titleElement.textContent = categoryNames[category];
			if (anchorEl) {
				anchorEl.href = `/board/list/${category}`;
			}
		} else {
			titleElement.textContent = "전체글보기";
			if (anchorEl) {
				anchorEl.href = `/board/list/all`;
			}
		}
	} else {
		titleElement.textContent = "게시글 관리";
		if (anchorEl) {
			anchorEl.href = `/board/list/admin`;
		}
	}


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

			if (textarea.value === "") {
				showToast("내용을 입력해주세요.", "error");
				return;
			}

			const replyDTO = {
				content: textarea.value,
				writer: writer.innerText,
				user_id: currentUser,
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

	if (content === "") {
		showToast("내용을 입력해주세요.", "error");
		return;
	}

	const replyDTO = {
		content: content,
		writer: writer.innerText,
		user_id: currentUser
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

// 상세보기에서 목록버튼 누를 시
document.getElementById("btn-list").addEventListener("click", async (event) => {
	event.preventDefault();

	if (!isAdminPage) {
		location.href = `/board/list/${category}`;
	} else {
		location.href = `/board/list/admin`;
	}
});

// 상세보기에서 수정버튼 누를 시
document.getElementById("btn-edit").addEventListener("click", async (event) => {
	event.preventDefault();

	currentMode = "edit";

	await fetchBoardDetail();
});

// 상세보기에서 삭제 버튼 누를 시
document.getElementById("btn-delete").addEventListener("click", async (event) => {
	event.preventDefault();

	const isConfirmed = confirm("정말 삭제하시겠습니까?");
	if (!isConfirmed) return;

	await fetchBoardDelete();
});

// 수정모드에서 목록 버튼 누를 시
document.getElementById("btn-back-to-detail").addEventListener("click", async (event) => {
	event.preventDefault();

	currentMode = "view";

	await fetchBoardDetail();
});

// 수정모드에서 수정버튼 누를 시
document.getElementById("btn-save-edit").addEventListener("click", async (event) => {
	event.preventDefault();

	const title = document.getElementById("titleInput");
	const content = editor.getHTML();
	const category = document.getElementById("categorySelect");
	const tagContainer = document.getElementById("tagContainer");
	const tags = Array.from(tagContainer.querySelectorAll(".badge"))
		.map(tagEl => tagEl.querySelector("span")?.textContent.replace(/^#/, "").trim());

	const formData = new FormData();
	formData.append("title", title.value);
	formData.append("content", content);
	formData.append("category", category.value);
	tags.forEach(tag => formData.append("tags", tag));
	if (fileInput.files.length > 0) {
		formData.append("file", fileInput.files[0]);
	}

	await fetchBoardEdit(formData);
});

// 댓글 메뉴 버튼 누를 시
document.addEventListener("click", async (event) => {
	if (event.target.id === "reply-edit") { // 수정버튼 누를 시
		event.preventDefault();

		const commentDiv = event.target.closest("div.reply-block");
		renderCommentEdit(commentDiv);
	} else if (event.target.id === "reply-delete") { // 삭제버튼 누를 시
		event.preventDefault();

		const isConfirmed = confirm("정말 삭제하시겠습니까?");
		if (!isConfirmed) return;

		const commentDiv = event.target.closest("div.reply-block");
		const rno = commentDiv.querySelector(".btn-reply") !== null ? commentDiv.querySelector(".btn-reply").dataset.rno : commentDiv.dataset.rno;

		await fetchReplyDelete(rno);
	}
});

// 댓글 수정화면에서 버튼 누를 시
document.addEventListener("click", async (event) => {
	if (event.target.id === "btn-cancel-reply") { // 취소 버튼 누를 시
		event.preventDefault();

		await fetchReplyList();
	} else if (event.target.id === "btn-save-reply") { // 저장 버튼 누를 시
		event.preventDefault();

		const container = event.target.closest(".reply-input-form");
		const rno = container.querySelector(".reply-writer").dataset.rno;
		const textarea = container.querySelector(".reply-content");

		if (textarea.value === "") {
			showToast("내용을 입력해주세요.", "error");
			return;
		}

		const replyDTO = {
			rno: rno,
			content: textarea.value,
		};

		await fetchReplyEdit(replyDTO);
	}
});

// 게시글 상세보기
async function fetchBoardDetail() {
	try {
		const res = await fetchWithAuth(apiSuffix);
		if (!res.ok) throw new Error("서버 오류 발생");
		const detail = await res.json();

		if (currentMode === "view") {
			renderDetailView(detail);
		} else if (currentMode === "edit") {
			renderEditView(detail);
		}
	} catch (e) {
		console.error("에러:", e.message);
	}
}

// 게시글 상세정보 화면에 렌더링
function renderDetailView(detail) {
	const contentTitle = document.querySelector(".content-title");
	const contentInfo = document.querySelector(".content-meta");
	const contentMain = document.querySelector(".content-main");
	const replyWriter = document.getElementById("reply-writer");
	const replybtn = document.getElementById("reply");
	const formatted = dayjs(detail.createdAt).format("YYYY-MM-DD HH:mm:ss");
	const replyBox = document.querySelector(".reply-box");
	const editMenu = document.querySelector(".edit-menu");
	const ownerBtns = document.getElementById("owner-action-box");
	const filePath = detail.filePath || null;
	let fileName;


	replyBox.style.display = "block";
	editMenu.style.display = "none";
	contentTitle.classList.remove("w-100");

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

	if (filePath !== null) {
		fileName = filePath.substring(filePath.indexOf('_') + 1);
		contentMain.innerHTML = `
			<div class="post-body">
				${detail.content}
			</div>
			<div class="attached-file-box p-3">
		    	<div class="file-label mb-2">첨부파일</div>
			    <a th:href="${detail.filePath}" class="file-link" target="_blank" download>
			        <i class="bi bi-download"></i>
			        <span>${fileName}</span>
			    </a>
			</div>
			<div id="tagContainer" class="mt-3"></div>
		`;
	} else {
		contentMain.innerHTML = `
			<div class="post-body">
				${detail.content}
			</div>
			<div id="tagContainer" class="mt-2"></div>
		`;
	}

	const tagContainer = document.getElementById("tagContainer");
	tagContainer.innerHTML = "";

	detail.tags.forEach(tag => {
		const tagEl = document.createElement("span");
		tagEl.className = "badge bg-secondary me-1 mb-1";
		tagEl.textContent = "#" + tag;
		tagContainer.appendChild(tagEl);
	});

	replybtn.textContent = `댓글 ${detail.commentCount}`;

	replyWriter.innerText = name;

	const writerId = `${detail.userId.id}`;

	if (currentUser !== writerId && !(isAdmin && isAdminPage)) {
		ownerBtns.classList.add("d-none");
	}

	if (detail.deleted) {
		ownerBtns.innerHTML = "";
		ownerBtns.innerHTML = `
			<button type="button" class="btn btn-outline-secondary"
						id="btn-restore">복원</button>
		`

		// 상세보기에서 복원 버튼 누를 시
		document.getElementById("btn-restore").addEventListener("click", async (event) => {
			event.preventDefault();

			const isConfirmed = confirm("정말 복원하시겠습니까?");
			if (!isConfirmed) return;

			await fetchBoardRestore();
		});
	}

	if (isAdminPage) {
		document.querySelector(".comment-form")?.classList.add("comment-form-disabled");
	}
}

// 게시글 수정화면 렌더링
function renderEditView(detail) {
	const contentTitle = document.querySelector(".content-title");
	const contentInfo = document.querySelector(".content-meta");
	const contentMain = document.querySelector(".content-main");
	const replyBox = document.querySelector(".reply-box");
	const editMenu = document.querySelector(".edit-menu");
	const filePath = detail.filePath || null;
	const tags = detail.tags;
	let fileName;

	if (filePath !== null) {
		fileName = filePath.substring(filePath.indexOf('_') + 1);
	}

	contentTitle.innerHTML = "";
	contentInfo.innerHTML = "";
	contentMain.innerHTML = "";
	contentTitle.innerHTML = `
		<div class="mb-0">
			<select class="form-select" id="categorySelect">
				<option value="자유" ${detail.category == '자유' ? 'selected' : ''}>자유</option>
				<option value="공지" ${detail.category == '공지' ? 'selected' : ''}>공지</option>
				<option value="질문답변" ${detail.category == '질문답변' ? 'selected' : ''}>질문답변</option>
				<option value="정보공유" ${detail.category == '정보공유' ? 'selected' : ''}>정보공유</option>
			</select>
		</div>
		<div class="edit-title">
			<input type="text" class="form-control" id="titleInput" value="${detail.title}" placeholder="제목을 입력하세요">
		</div>
	`;

	contentMain.innerHTML = `
		<div id="editor"></div>
		<div class="mt-3 mb-3">
			<input class="form-control" type="file" name="file" id="fileInput">
			${filePath !== null ? `
						<div class="d-flex align-items-center gap-3 attached-file-box p-3 mt-2" id="file-box">
							<div>
								<div class="file-label mb-2">기존 첨부파일</div>
								<span>${fileName}</span>
							</div>
							<button type="button" class="btn-close" id="deleteFile" aria-label="삭제"></button>
						</div>
			` : ``}
			<input type="hidden" id="deleteFileInput" value="false">
		</div>
		<div class="mb-3">
			<label for="tagInput" class="form-label">태그 추가</label>
			<input type="text" id="tagInput" class="form-control"
				placeholder="태그를 입력하고 Enter를 누르세요">
			<div id="tagContainer" class="mt-2"></div>
		</div>
	`;

	const tagContainer = document.getElementById("tagContainer");
	tagContainer.innerHTML = "";

	tags.forEach(tag => {
		const tagEl = document.createElement("span");
		tagEl.className = "badge bg-secondary me-1 mb-1 d-inline-flex align-items-center";

		// 태그 텍스트
		const tagText = document.createElement("span");
		tagText.textContent = "#" + tag;
		tagEl.appendChild(tagText);

		// 삭제 버튼
		const closeBtn = document.createElement("button");
		closeBtn.type = "button";
		closeBtn.className = "btn-close btn-close-white ms-2";
		closeBtn.style.fontSize = "0.6rem";
		closeBtn.setAttribute("aria-label", "Remove");
		closeBtn.onclick = () => tagEl.remove();

		tagEl.appendChild(closeBtn);
		tagContainer.appendChild(tagEl);
		tagInput.value = "";
	});

	setTimeout(() => {
		editerInit(detail.content);
		tagAdd();

		const deleteFileBtn = document.getElementById("deleteFile");
		if (deleteFileBtn) {
			deleteFileBtn.addEventListener("click", () => {
				deleteExistingFile();
			});
		}
	}, 0);

	replyBox.style.display = "none";
	editMenu.style.display = "flex";
	editMenu.style.justifyContent = "flex-end";
	editMenu.style.gap = "10px";
	contentTitle.classList.add("w-100");
}

// 댓글 조회
async function fetchReplyList(size = currentSize, page = currentPage, sort = currentSort, direction = currentDirection) {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}/replies?size=${size}&page=${page}&sort=${sort},${direction}`);
		if (!res.ok) throw new Error("서버 오류 발생");
		const replies = await res.json();
		renderReplyList(replies.parentReplies, replies.childReplies);
		renderPagination(replies.pageInfo);
	} catch (e) {
		console.error("에러: ", e.message);
	}
}

// 댓글 목록을 렌더링
function renderReplyList(parentReplies, childReplies) {
	const commentList = document.querySelector(".comment-list");
	commentList.innerHTML = "";

	if (parentReplies.length === 0) {
		commentList.innerHTML = `
				<div class="border round p-5 mb-2 ms-3">
					<div class="d-flex justify-content-center">
						<p>등록된 댓글이 없습니다.</p>
					</div>
				</div>
			`;
		return;
	}

	// childReplies를 map 형태로 구성: { 부모 rno: [자식 댓글 리스트] }
	const childMap = {};
	childReplies.forEach(child => {
		if (!childMap[child.parent_id]) {
			childMap[child.parent_id] = [];
		}
		childMap[child.parent_id].push(child);
	});

	// 부모 댓글 순회
	parentReplies.forEach(parent => {
		// 자식 댓글 배열을 연결
		parent.children = childMap[parent.rno] || [];

		const element = renderReplyItem(parent); // 자식 포함해서 렌더링
		commentList.appendChild(element);
	});
}

// 댓글(or 답글)을 트리 구조로 렌더링
function renderReplyItem(reply, depth = 0) {
	const divEl = document.createElement("div");
	const formatDate = dayjs(reply.createAt).format("YYYY-MM-DD HH:mm:ss");
	divEl.classList.add("p-3", "pe-0", "mb-2", "bb-right");

	const isOwner = reply.userId.id === currentUser;

	const actionMenuHtml = isOwner
		? `
			<div class="dropdown">
				<button class="btn btn-sm" type="button" data-bs-toggle="dropdown" aria-expanded="false">
					<i class="fa-solid fa-ellipsis-vertical"></i>
				</button>
				<ul class="dropdown-menu dropdown-menu-end">
					<li><a class="dropdown-item" id="reply-edit" href="#">수정</a></li>
					<li><a class="dropdown-item text-danger" id="reply-delete" href="#">삭제</a></li>
				</ul>
			</div>
		`
		: "";

	if (depth > 0) {
		divEl.classList.add("ms-5", "border-top", "mt-3"); // 답글 들여쓰기
		divEl.innerHTML = `
			<div class="reply-block" data-rno=${reply.rno}>
				<div class="d-flex justify-content-between mb-3 px-2">
					<strong>${reply.writer}</strong>
					<span>${formatDate}</span>
				</div>
				<div class="d-flex justify-content-between align-items-start px-2 pb-3">
					<div class="flex-grow-1">
						<p class="mb-0">${reply.content}</p>
					</div>
					${actionMenuHtml}
				</div>
				<div class="d-flex justify-content-end align-items-center mt-2 px-2">
					<div class="d-flex gap-2">
						<button type="button" class="btn btn-outline-light border btn-sm d-flex align-items-center gap-1">
							<i class="fa-regular fa-thumbs-up text-danger"></i>
							<span class="text-danger">0</span>
						</button>
					</div>
				</div>
			</div>
		`;
	} else {
		if (!isAdminPage) {
			divEl.innerHTML = `
					<div class="reply-block">
						<div class="d-flex justify-content-between mb-3 px-2">
							<strong>${reply.writer}</strong>
							<span>${formatDate}</span>
						</div>
						<div class="d-flex justify-content-between align-items-start px-2 pb-3">
							<div class="flex-grow-1">
								<p class="mb-0">${reply.content}</p>
							</div>
							${actionMenuHtml}
						</div>
						<div class="d-flex justify-content-between align-items-center mt-2 px-2">
							<button type="button" class="btn btn-outline-secondary btn-reply" data-rno=${reply.rno}>답글</button>
							<div class="d-flex gap-2">
								<button type="button" class="btn btn-outline-light border btn-sm d-flex align-items-center gap-1">
									<i class="fa-regular fa-thumbs-up text-danger"></i>
									<span class="text-danger">0</span>
								</button>
							</div>
						</div>
					</div>
				`;
		} else {
			divEl.innerHTML = `
					<div class="reply-block">
						<div class="d-flex justify-content-between mb-3 px-2">
							<strong>${reply.writer}</strong>
							<span>${formatDate}</span>
						</div>
						<div class="d-flex justify-content-between align-items-start px-2 pb-3">
							<div class="flex-grow-1">
								<p class="mb-0">${reply.content}</p>
							</div>
							${actionMenuHtml}
						</div>
						<div class="d-flex justify-content-end align-items-center mt-2 px-2">
							<div class="d-flex gap-2">
								<button type="button" class="btn btn-outline-light border btn-sm d-flex align-items-center gap-1">
									<i class="fa-regular fa-thumbs-up text-danger"></i>
									<span class="text-danger">0</span>
								</button>
							</div>
						</div>
					</div>
				`;
		}
		

	}

	if (Array.isArray(reply.children)) {
		reply.children.forEach(child => {
			const childEl = renderReplyItem(child, depth + 1);
			divEl.appendChild(childEl);
		});
	}

	return divEl;
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
		await fetchBoardDetail();
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// 댓글 수정
async function fetchReplyEdit(replyDTO) {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}/replies/${replyDTO.rno}`, {
			method: "PUT",
			body: JSON.stringify(replyDTO)
		});
		if (!res.ok) {
			showToast("❗ 댓글수정에 실패했습니다. 다시 시도해주세요.", "error");
			return;
		}
		const result = await res.text();
		showToast("✔️ " + result, "success");
		await fetchReplyList();
		await fetchBoardDetail();
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// 댓글 삭제
async function fetchReplyDelete(rno) {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}/replies/${rno}`, {
			method: "DELETE"
		});

		if (!res.ok) {
			showToast("❗ 댓글삭제에 실패했습니다. 다시 시도해주세요.", "error");
			return;
		}

		const result = await res.text();
		showToast("✔️ " + result, "success");
		await fetchReplyList();
		await fetchBoardDetail();
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
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

// 페이지네이션 렌더링
function renderPagination(pageInfo) {
	const pagination = document.getElementById("pagination");
	pagination.innerHTML = "";

	const number = pageInfo.number;
	const totalPages = pageInfo.totalPages;
	const first = pageInfo.first;
	const last = pageInfo.last;

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
					data-parent="${rno}">${name}</strong></label>
			<textarea class="form-control reply-content" rows="3"
				placeholder="댓글을 입력하세요..."></textarea>
		</div>
	
		<div class="d-flex justify-content-end">
			<button type="button" class="btn btn-outline-secondary registerBtn">등록</button>
		</div>
	`;

	commentBox.appendChild(divEl);
}

// 댓글 수정화면 렌더링
function renderCommentEdit(commentDiv) {
	const content = commentDiv.querySelector("p.mb-0").textContent;
	const writer = commentDiv.querySelector("strong");
	const rno = commentDiv.querySelector(".btn-reply") !== null ? commentDiv.querySelector(".btn-reply").dataset.rno : commentDiv.dataset.rno;

	const divEl = document.createElement("div");
	divEl.classList.add("pt-3", "pb-3", "mt-3", "border-top", "border-bottom", "reply-input-form");
	divEl.innerHTML = `
		<div class="mb-3 input-reply">
			<label class="form-label mb-2 text-muted ms-cus"><strong class="reply-writer"
					data-rno="${rno}">${writer.textContent}</strong></label>
			<textarea class="form-control reply-content" rows="3"
				placeholder="댓글을 입력하세요...">${content}</textarea>
		</div>

		<div class="d-flex justify-content-end gap-2">
			<button type="button" class="btn btn-outline-secondary" id="btn-cancel-reply">취소</button>
			<button type="button" class="btn btn-outline-secondary" id="btn-save-reply">저장</button>
		</div>
	`;

	commentDiv.replaceWith(divEl);
}

// 게시글 수정
async function fetchBoardEdit(formData) {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}/edit`, {
			method: "POST",
			body: formData
		});

		if (!res.ok) {
			const text = await res.text();
			const errorMsg = text?.trim() ? text : "❗ 댓글 수정에 실패했습니다. 다시 시도해주세요.";
			showToast(errorMsg, "error");
			return;
		}

		const result = await res.text();
		showToast("✔️ " + result, "success");

		currentMode = "view";
		await fetchBoardDetail();
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// 게시글 삭제
async function fetchBoardDelete() {
	try {
		const res = await fetchWithAuth(`/api/boards/${bno}`, {
			method: "DELETE",
		});

		if (!res.ok) {
			const text = await res.text();
			const errorMsg = text?.trim() ? text : "❗ 게시글 삭제에 실패했습니다. 다시 시도해주세요.";
			showToast(errorMsg, "error");
			return;
		}

		if (!isAdminPage) {
			location.href = `/board/list/${category}?deleted=true`;
		} else {
			location.href = `/board/list/admin?deleted=true`;
		}
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// 게시글 복원
async function fetchBoardRestore() {
	try {
		const res = await fetchWithAuth(`/api/boards/admin/${bno}`, {
			method: "PATCH",
		});

		if (!res.ok) {
			const text = await res.text();
			const errorMsg = text?.trim() ? text : "❗ 게시글 복원에 실패했습니다. 다시 시도해주세요.";
			showToast(errorMsg, "error");
			return;
		}

		location.href = `/board/list/admin?restore=true`;
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// 에디터 초기값
function editerInit(content) {
	editor = new toastui.Editor({
		el: document.querySelector('#editor'),
		height: '500px',
		initialEditType: 'wysiwyg',
		previewStyle: 'vertical',
		initialValue: content,
		hooks: {
			addImageBlobHook: (blob, callback) => {
				// 이미지 업로드 → 서버 전송
				const formData = new FormData();
				formData.append("image", blob);

				fetchWithAuth('/api/boards/upload/image', {
					method: 'POST',
					body: formData
				})
					.then(res => res.text())
					.then(imageUrl => {
						callback(imageUrl, '업로드된 이미지');
					});
			}
		}
	});
}

// 태그 추가
function tagAdd() {
	const tagInput = document.getElementById("tagInput");
	const tagContainer = document.getElementById("tagContainer");

	tagInput.addEventListener("keypress", function(e) {
		if (e.key === "Enter") {
			e.preventDefault();
			const value = tagInput.value.trim();
			if (value) {
				const tagEl = document.createElement("span");
				tagEl.className = "badge bg-secondary me-1 mb-1 d-inline-flex align-items-center";

				// 태그 텍스트
				const tagText = document.createElement("span");
				tagText.textContent = "#" + value;
				tagEl.appendChild(tagText);

				// 삭제 버튼
				const closeBtn = document.createElement("button");
				closeBtn.type = "button";
				closeBtn.className = "btn-close btn-close-white ms-2";
				closeBtn.style.fontSize = "0.6rem";
				closeBtn.setAttribute("aria-label", "Remove");
				closeBtn.onclick = () => tagEl.remove();

				tagEl.appendChild(closeBtn);
				tagContainer.appendChild(tagEl);
				tagInput.value = "";
			} else {
				showToast("❗ 태그를 입력 후 다시 시도해주세요.", "error");
			}
		}
	});
}

// 게시글 수정화면에서 기존 첨부파일 삭제
function deleteExistingFile() {
	document.getElementById("file-box").classList.add("d-none");
	document.getElementById("deleteFileInput").value = "true";
}

// 로그인 사용자 정보 가져오기
async function loadLoginUser() {
	try {
		const res = await fetchWithAuth("/users/me", {
			method: "GET",
			credentials: "include"
		});

		if (!res.ok) {
			const msg = await res.text();
			throw new Error(`(${res.status}) 사용자 정보를 가져올 수 없습니다. → ${msg}`);
		}

		const data = await res.json();

		currentUser = data.id;
		name = data.name;
		isAdmin = data.role === 'ROLE_ADMIN';
	} catch (e) {
		console.error("로그인 사용자 확인 실패:", e);
	}
}

// 체류 시간 측정 시작
function startDwellTracking() {
	enterTime = Date.now();
	interactionCount = 0;

	// 상호작용 카운트 추적
	window.addEventListener('scroll', trackInteraction);
	window.addEventListener('click', trackInteraction);

	// 종료 시점 전송
	window.addEventListener("beforeunload", sendDwellTimeLog);
}

// 상호작용 이벤트 카운터
function trackInteraction() {
	interactionCount++;
}

// 체류 시간 계산 후 전송
function sendDwellTimeLog() {
	const leaveTime = Date.now();
	const dwellTime = Math.round((leaveTime - enterTime) / 1000);

	if (!currentUser || !currentBoardId) return;

	const data = {
		userId: currentUser,
		boardId: currentBoardId,
		dwellTime: dwellTime,
		interactionCount: interactionCount
	};

	const blob = new Blob([JSON.stringify(data)], { type: "application/json" });

	const sent = navigator.sendBeacon("/api/log/dwell-time", blob);

	if (!sent) {
		console.warn("❗ sendBeacon 실패 → fetch로 전송");
		fetchWithAuth("/api/log/dwell-time", {
			method: "POST",
			body: JSON.stringify(data),
			keepalive: true
		});
	}
}