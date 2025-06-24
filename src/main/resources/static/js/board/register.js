import { fetchWithAuth } from "../fetchWithAuth.js";

// 등록버튼 누를 시
document.getElementById("btn-save-register").addEventListener("click", async () => {
	const category = document.getElementById("categorySelect");
	const title = document.getElementById("titleInput");
	const content = document.getElementById("contentTextarea");
	const writer = document.getElementById("writer");
	console.log(category.value)
	// 하나라도 메시지가 있다면 토스트 출력하고 요청 중단
	if (category.value === "" || title.value === "" || content.value === "" || writer.value === "") {
		Toastify({
			text: "❗ 입력한 정보를 다시 확인해주세요.",
			duration: 2000,
			close: true,
			gravity: "bottom",
			position: "center",
			escapeMarkup: false,
			style: {
				background: "rgb(249, 226, 230)",
				color: "rgb(83, 14, 26)",
				fontSize: "15px",
				borderRadius: "8px",
				border: "none",
				boxShadow: "none",
				padding: "12px 18px",
				display: "flex",
				alignItems: "center",
			}
		}).showToast();
		return;
	}

	const formData = new FormData();
	formData.append("category", category.value);
	formData.append("title", title.value);
	formData.append("content", content.value);
	formData.append("userId", writer.value);
	formData.append("writer", "이승헌");
	await fetchBoardRegister(formData);
});

// 게시글 등록하기
async function fetchBoardRegister(formData) {
	try {
		const res = await fetchWithAuth("/api/boards", {
			method: "POST",
			body: formData
		});

		if (!res.ok) {
			showToast("❗ 게시글 등록에 실패했습니다. 다시 시도해주세요.", "error");
			return;
		}

		location.href = "/board/list?register=true";
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
			whiteSpace: "nowrap"
		}
	}).showToast();
}