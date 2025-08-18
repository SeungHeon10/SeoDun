import { fetchWithAuth } from "/js/core/fetchWithAuth.js";

const pathParts = window.location.pathname.split('/');
const id = pathParts[2];

document.addEventListener("DOMContentLoaded", async () => {
	await fetchUserDetail();

	// 이메일 전송 로직
	document.getElementById("sendEmailBtn").addEventListener("click", async function() {
		const email = document.getElementById("email").value;
		const feedbackEl = document.getElementById("emailFeedback");

		if (!email) {
			showToast("❗ 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", "error");
			return;
		}

		try {
			const response = await fetchWithAuth(`/auth/emails?email=${encodeURIComponent(email)}`, {
				method: "POST"
			});

			if (!response.ok) {
				throw new Error("서버 오류 발생");
			}

			const message = await response.text();

			feedbackEl.textContent = message;
			feedbackEl.classList.remove("text-danger", "text-success");
			feedbackEl.classList.add(message.includes("실패") ? "text-danger" : "text-success");

		} catch (error) {
			console.error("전송 오류:", error);
			showToast("❗ 인증 메일 전송 중 오류가 발생했습니다.", "error");
		}
	});

	// 이메일 인증번호 확인 로직
	document.getElementById("verifyCodeBtn").addEventListener("click", async () => {
		const email = document.getElementById("email").value;
		const verifyCodeInput = document.getElementById("verifyCode");
		const code = verifyCodeInput.value;
		const feedback = document.getElementById("verificationCodeFeedback");

		if (!code) {
			verifyCodeInput.focus();
			return;
		}

		try {
			const res = await fetchWithAuth(`/auth/emails/verify?email=${encodeURIComponent(email)}&code=${code}`, {
				method: "POST"
			});

			if (!res.ok) {
				const msg = await res.text();
				throw new Error(msg || "서버 오류가 발생했습니다.");
			}

			const message = await res.text();
			feedback.textContent = message;
			feedback.classList.remove("text-danger", "text-success");
			feedback.classList.add(message.includes("완료") ? "text-success" : "text-danger");

			location.href = `/user/detail/${id}`;
		} catch (error) {
			console.error("인증 오류:", error);
			feedback.textContent = error.message;
			feedback.classList.add("text-danger");
		}
	});
});

// 회원 상세정보
async function fetchUserDetail() {
	try {
		const res = await fetchWithAuth(`/api/users/${id}`);
		if (!res.ok) throw new Error("서버 오류 발생");
		const detail = await res.json();

		const emailBox = document.getElementById("email");
		emailBox.value = detail.email;
	} catch (e) {
		console.error("에러:", e.message);
	}
}
