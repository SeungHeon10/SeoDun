import { fetchWithAuth, setAccessToken } from "/js/core/fetchWithAuth.js";

const pathParts = window.location.pathname.split('/');
const id = pathParts.pop();
let isAdmin = false;

// 페이지 로드 시
document.addEventListener("DOMContentLoaded", async () => {
	await fetchLoginUser();
	await fetchUserDetail();

	const urlParams = new URLSearchParams(window.location.search);
	if (urlParams.get("updated") === "true") {
		showToast("✔️ 회원님의 정보가 변경되었습니다.", "success");
		window.history.replaceState({}, document.title, window.location.pathname);
	}

	const titleElement = document.getElementById("category-title");
	const anchorEl = titleElement.closest("a");
	window.history.replaceState({}, document.title, window.location.pathname);

	if (!isAdmin) {
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
	onIf('#btn-back-to-list', 'click', function() {
		location.href = `/user/list/admin`;
	});

	// 본인확인 인증하기 버튼 누를 시
	onIf('#emailVerifyButton', 'click', function() {
		location.href = `/user/${id}/email/verify`;
	})
});

// 로그인한 회원 정보
async function fetchLoginUser() {
	try {
		const res = await fetchWithAuth('/api/users/me', { skipRefresh: true });
		if (!res.ok) throw new Error("서버 오류 발생");

		const user = await res.json();

		if (user.role === "ROLE_ADMIN") {
			isAdmin = true;
		}
	} catch (e) {
		console.error("에러:", e.message);
	}
}

// 사용자 정보 상세보기
async function fetchUserDetail() {
	const apiSuffix = isAdmin ? `/api/users/admin/${id}` : `/api/users/username/${id}`;

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
	const cardNickname = document.getElementById("card-nickname"); // 상단 카드 닉네임
	const cardId = document.getElementById("card-id"); // 상단 카드 아이디
	const profileId = document.getElementById("profile-id"); // 프로필 섹션 아이디
	const profileName = document.getElementById("profile-name"); // 프로필 섹션 이름
	const profileNickname = document.getElementById("profile-nickname"); // 프로필 섹션 닉네임
	const profilePhone = document.getElementById("profile-phone"); // 프로필 섹션 휴대폰번호
	const profileEmail = document.getElementById("profile-email"); // 프로필 섹션 이메일
	const profileRole = document.getElementById("profile-role"); // 프로필 섹션 권한
	const profileCreatedAt = document.getElementById("profile-createdAt"); // 프로필 섹션 가입일
	const roleEditBox = document.getElementById("editRoleLink").closest("div"); // 프로필 섹션 Role 변경 div
	const profileVerification = document.getElementById("profile-verification"); // 본인확인
	const userActionButtons = document.getElementById("userActionButtons"); // 목록/탈퇴 버튼바
	const verifyButtonWrapper = document.getElementById("verifyButtonWrapper"); // 본인확인 인증하기 버튼 div
	const formatted = dayjs(detail.createdAt).format("YYYY-MM-DD HH:mm:ss"); // 날짜 포맷

	// 값 넣어주기
	cardNickname.textContent = detail.nickname;
	cardId.textContent = detail.id;
	profileId.textContent = detail.id;
	profileName.textContent = detail.name;
	profileNickname.textContent = detail.nickname;
	profilePhone.textContent = detail.pno;
	profileEmail.textContent = detail.email;
	profileRole.textContent = detail.role;
	profileCreatedAt.textContent = formatted;

	// 탈퇴버튼 누를 시 이벤트
	userActionButtons.addEventListener("click", async (e) => {
		const btn = e.target.closest("#btn-delete");
		if (!btn) return;

		e.preventDefault();
		if (!confirm("정말 탈퇴하시겠습니까?")) return;
		await fetchUserDelete();
	});

	// 변경 버튼 링크 설정
	const links = document.querySelectorAll('a[href^="/user/profile/edit/"]');
	if (isAdmin) {
		links.forEach(a => {
			const url = new URL(a.getAttribute("href"), location.origin);
			const field = url.pathname.split('/').pop();

			a.setAttribute(
				"href",
				`/user/profile/edit/admin/${encodeURIComponent(detail.id)}/${encodeURIComponent(field)}`
			);
		});
	} else {
		links.forEach(a => {
			const url = new URL(a.getAttribute("href"), location.origin);
			const field = url.pathname.split('/').pop();

			a.setAttribute(
				"href",
				`/user/profile/edit/${encodeURIComponent(detail.id)}/${encodeURIComponent(field)}`
			);
		});
	}

	// 본인확인 완료여부에 따라 ✓/✗ 표시
	if (detail.emailVerified) {
		profileVerification.textContent = "✓";
		verifyButtonWrapper.style.display = "none";
	} else {
		profileVerification.classList.remove("bg-success");
		profileVerification.classList.add("bg-danger");
		profileVerification.textContent = "✗";
		verifyButtonWrapper.style.display = "block";
	}

	// 관리자(admin)페이지가 아닐 시 
	if (!isAdmin) {
		// 목록 버튼 삭제
		userActionButtons.innerHTML = "";
		userActionButtons.innerHTML = `
			<button type="button" class="btn btn-outline-secondary" id="btn-delete">탈퇴</button>
		`
		// 권한 변경 불가능하도록
		roleEditBox.classList.remove("d-flex");
		roleEditBox.classList.add("d-none");
	} else {
		// 권한 변경 가능하도록 변경 버튼 보여줌
		roleEditBox.classList.remove("d-none");
		roleEditBox.classList.add("d-flex");
		// 본인인증은 본인만 가능하도록 
		verifyButtonWrapper.style.display = "none";
	}

	// 비활성화된 회원일 때 UI
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

		if (isAdmin) {
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

// 요소가 있을 때만 이벤트 등록
function onIf(selector, event, handler) {
	const el = document.querySelector(selector);
	if (el) el.addEventListener(event, handler);
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