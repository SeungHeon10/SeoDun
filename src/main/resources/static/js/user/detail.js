import { fetchWithAuth, setAccessToken } from "../fetchWithAuth.js";

const pathParts = window.location.pathname.split('/');
const id = pathParts.pop();
const isAdminPage = location.pathname.includes("/admin");
const apiSuffix = isAdminPage ? `/api/users/admin/${id}` : `/api/users/${id}`;

// 페이지 로드 시
document.addEventListener("DOMContentLoaded", async () => {
	await fetchUserDetail();

	const titleElement = document.getElementById("category-title");
	const anchorEl = titleElement.closest("a");
	window.history.replaceState({}, document.title, window.location.pathname);

	if (!isAdminPage) {
		titleElement.textContent = "내정보";
		if (anchorEl) {
			anchorEl.href = `/user/detail/${id}`;
		}
	} else {
		titleElement.textContent = "회원정보";
		if (anchorEl) {
			anchorEl.href = `/user/list/admin`;
		}
	}

	// 상세보기에서 목록버튼 누를 시(admin)
	document.getElementById("btn-back-to-list").addEventListener("click", async (event) => {
		event.preventDefault();

		location.href = `/user/list/admin`;
	});

	// 상세보기에서 탈퇴 버튼 누를 시
	document.getElementById("btn-delete").addEventListener("click", async (event) => {
		event.preventDefault();

		const isConfirmed = confirm("정말 탈퇴하시겠습니까?");
		if (!isConfirmed) return;

		await fetchUserDelete();
	});

});

// 사용자 정보 상세보기
async function fetchUserDetail() {
	try {
		const res = await fetchWithAuth(apiSuffix);
		if (!res.ok) throw new Error("서버 오류 발생");
		const detail = await res.json();

		renderDetailView(detail);
	} catch (e) {
		console.error("에러:", e.message);
	}
}

// 사용자 상세정보 화면에 렌더링
function renderDetailView(detail) {
	const cardNickname = document.getElementById("card-nickname");
	const cardId = document.getElementById("card-id");
	const profileId = document.getElementById("profile-id");
	const profileName = document.getElementById("profile-name");
	const profileNickname = document.getElementById("profile-nickname");
	const profilePhone = document.getElementById("profile-phone");
	const profileEmail = document.getElementById("profile-email");
	const profileRole = document.getElementById("profile-role");
	const profileCreatedAt = document.getElementById("profile-createdAt");
	const profileVerification = document.getElementById("profile-verification");
	const userActionButtons = document.getElementById("userActionButtons");
	const formatted = dayjs(detail.createdAt).format("YYYY-MM-DD HH:mm:ss");

	cardNickname.textContent = detail.nickname;
	cardId.textContent = detail.id;
	profileId.textContent = detail.id;
	profileName.textContent = detail.name;
	profileNickname.textContent = detail.nickname;
	profilePhone.textContent = detail.pno;
	profileEmail.textContent = detail.email;
	profileRole.textContent = detail.role;
	profileCreatedAt.textContent = formatted;

	if (detail.emailVerified) {
		profileVerification.textContent = "✓";
	} else {
		profileVerification.classList.remove("bg-success");
		profileVerification.classList.add("bg-danger");
		profileVerification.textContent = "✗";
	}

	if (detail.deleted) {
		userActionButtons.innerHTML = "";
		userActionButtons.innerHTML = `
			<button type="button" class="btn btn-outline-secondary" id="btn-back-to-list">목록</button>
			<button type="button" class="btn btn-outline-secondary" id="btn-restore">복원</button>
		`

		// 상세보기에서 복원 버튼 누를 시
		document.getElementById("btn-restore").addEventListener("click", async (event) => {
			event.preventDefault();

			const isConfirmed = confirm("정말 복원하시겠습니까?");
			if (!isConfirmed) return;

			await fetchUserRestore();
		});
	}
}

// 사용자 정보 수정
//async function fetchUserEdit(formData) {
//	try {
//		const res = await fetchWithAuth(`/api/users/${id}/edit`, {
//			method: "POST",
//			body: formData
//		});
//
//		if (!res.ok) {
//			const text = await res.text();
//			const errorMsg = text?.trim() ? text : "❗ 댓글 수정에 실패했습니다. 다시 시도해주세요.";
//			showToast(errorMsg, "error");
//			return;
//		}
//
//		const result = await res.text();
//		showToast("✔️ " + result, "success");
//
//		currentMode = "view";
//		await fetchBoardDetail();
//	} catch (e) {
//		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
//		console.error("에러:", e);
//	}
//}

// 사용자 계정 비활성화
async function fetchUserDelete() {
	try {
		const res = await fetchWithAuth(`/api/users/${id}`, {
			method: "DELETE",
		});

		if (!res.ok) {
			const text = await res.text();
			const errorMsg = text?.trim() ? text : "❗ 계정 비활성화에 실패했습니다. 다시 시도해주세요.";
			showToast(errorMsg, "error");
			return;
		}

		if (isAdminPage) {
			location.href = "/user/list/admin?deleted=true";
		} else {
			const logout = await fetchWithAuth("/logout", {
				method: "POST"
			});

			if (!logout.ok) {
				throw new Error("서버 오류 발생");
			}

			setAccessToken(null);

			location.href = "/user/withdrawal";

		}
	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// 사용자 계정 복원
async function fetchUserRestore() {
	try {
		const res = await fetchWithAuth(`/api/users/admin/${id}`, {
			method: "PATCH",
		});

		if (!res.ok) {
			const text = await res.text();
			const errorMsg = text?.trim() ? text : "❗ 계정 복원에 실패했습니다. 다시 시도해주세요.";
			showToast(errorMsg, "error");
			return;
		}

		location.href = `/user/list/admin?restore=true`;
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