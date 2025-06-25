import { fetchWithAuth } from "../fetchWithAuth.js";

document.addEventListener("DOMContentLoaded", () => {
	tagAdd();
	editerInit();
});

// 등록버튼 누를 시
document.getElementById("btn-save-register").addEventListener("click", async () => {
	const category = document.getElementById("categorySelect");
	const title = document.getElementById("titleInput");
	const content = document.getElementById("contentTextarea");
	const userId = document.getElementById("userId");
	const tag = document.getElementById("tagInput");
	const file = document.getElementById("fileInput");

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
	formData.append("userId", userId.value);
	formData.append("writer", "이승헌");
	formData.append("tag", tag.value);
	formData.append("file", file.value);
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

function tagAdd() {
	const tagInput = document.getElementById("tagInput");
	const tagContainer = document.getElementById("tagContainer");

	tagInput.addEventListener("keypress", function(e) {
		if (e.key === "Enter") {
			e.preventDefault();
			const value = tagInput.value.trim();
			if (value) {
				const tagEl = document.createElement("span");
				tagEl.className = "badge bg-secondary me-1 mb-1";
				tagEl.textContent = "#" + value;
				tagContainer.appendChild(tagEl);
				tagInput.value = "";
			} else {
				showToast("❗ 태그를 입력 후 다시 시도해주세요.", "error");
			}
		}
	});
}

function editerInit() {
	const editor = new toastui.Editor({
		el: document.querySelector('#editor'),
		height: '500px',
		initialEditType: 'wysiwyg',  // wysiwyg or markdown
		previewStyle: 'vertical',
		hooks: {
			addImageBlobHook: (blob, callback) => {
				// 이미지 업로드 → 서버 전송
				const formData = new FormData();
				formData.append("image", blob);

				fetchWithAuth('/upload/image', {
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