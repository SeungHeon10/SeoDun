document.addEventListener("DOMContentLoaded", async () => {
	const params = new URLSearchParams(window.location.search);
	const token = params.get("token");
	const btn = document.querySelector("button.btn.btn-dark");

	if (!token) {
		alert("유효하지 않은 접근입니다. 메일의 링크를 다시 확인해주세요.");
		if (btn) btn.disabled = true;
		return;
	}

	// 새 비밀번호 입력 시
	document.querySelector("#newPassword").addEventListener("input", (e) => {
		const password = e.target.value.trim();
		const isInvalidPw = document.getElementById('isInvalidPw');
		if (!isInvalidPw) return;

		if (password === '') { isInvalidPw.textContent = ''; return; }
		isInvalidPw.textContent = isValidPassword(password) ? '' : '비밀번호는 8~20자, 영문자/숫자/특수문자를 모두 포함하여 입력 가능합니다.';
		validatePasswordMatch();
	});

	// 새 비밀번호 확인 입력 시
	document.querySelector("#newPasswordConfirm").addEventListener("input", validatePasswordMatch);

	// 확인 버튼 클릭 시
	btn.addEventListener("click", async () => {
		const pwInput = document.querySelector("#newPassword");
		const pwConfirmInput = document.querySelector("#newPasswordConfirm");
		const newPassword = pwInput.value.trim();
		const newPasswordConfirm = pwConfirmInput.value.trim();

		// 클라이언트 유효성 검사
		if (!isValidPassword(newPassword)) {
			document.querySelector("#isInvalidPw").textContent =
				"비밀번호는 8~20자, 영문자/숫자/특수문자를 모두 포함하여 입력 가능합니다.";
			pwInput.focus();
			return;
		}
		if (newPassword !== newPasswordConfirm) {
			document.querySelector("#isPasswordMismatch").textContent = "입력하신 비밀번호와 다릅니다.";
			pwConfirmInput.focus();
			return;
		}

		const passwordResetConfirmDTO = {
			token: token,
			newPassword: newPassword
		}

		await fetchPasswordReset(passwordResetConfirmDTO);
	});
});

// 비밀번호 유효성 검사
function isValidPassword(password) {
	const regex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;
	return regex.test(password);
}

// 비밀번호 재확인 함수
function validatePasswordMatch() {
	const newPassword = document.getElementById("newPassword").value.trim();
	const newPasswordConfirm = document.getElementById("newPasswordConfirm").value.trim();
	const isPasswordMismatch = document.getElementById("isPasswordMismatch");

	isPasswordMismatch.textContent = newPassword && newPasswordConfirm && newPassword !== newPasswordConfirm ? "입력하신 비밀번호와 다릅니다." : "";
}

// 비밀번호 변경 함수
async function fetchPasswordReset(DTO) {
	try {
		const res = await fetch("/api/account/password/reset/confirm", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(DTO)
		});

		// 정상처리 시
		if (res.ok) {
			const msg = await res.text();
			showToast(msg || "비밀번호가 변경되었습니다.", "success");

			setTimeout(() => { location.href = "/login"; }, 1000);
			return;
		}

		// 예외 발생 시
		const errText = await res.text();
		if (res.status === 400) {
			showToast(errText || "요청이 올바르지 않습니다. 다시 시도해주세요.", "error");
		} else if (res.status === 404) {
			showToast(errText || "사용자를 찾을 수 없습니다.", "error");
		} else {
			showToast(errText || `요청 처리 중 오류가 발생했습니다.`, "error");
		}
	} catch (e) {
		console.error(e);
		showToast("네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", "error");
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