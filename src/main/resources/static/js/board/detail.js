import { fetchWithAuth } from "../fetchWithAuth.js";

document.addEventListener("DOMContentLoaded" , async () => {
	await fetchBoardDetail();
});

// 게시글 상세보기
async function fetchBoardDetail() {
	const bno = window.location.pathname.split("/").pop();
	const contentTitle = document.querySelector(".content-title");
	const contentInfo = document.querySelector(".content-meta");
	const contentMain = document.querySelector(".content-main");
	const replybtn = document.getElementById("reply");

	try {
		const res = await fetchWithAuth(`/api/boards/${bno}`);

		if (!res.ok) {
			throw new Error("서버 오류 발생");
		}

		const detail = await res.json();
		
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
	} catch (e) {
		console.error("에러:", e.message);
	}
}