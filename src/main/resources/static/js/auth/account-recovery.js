//import { fetchWithAuth } from "/js/core/fetchWithAuth.js";

document.addEventListener("DOMContentLoaded", () => {
	// 아이디 찾기 / 비밀번호 찾기 버튼 누를 시
	document.addEventListener("click", async (e) => {
		const btn = e.target.closest(".recovery_submit");
		if (!btn) return;

		const action = btn.dataset.action; // find-password || find-id
		// 버튼이 속한 패널을 기준으로 입력값을 찾음
		const panel = btn.closest('[class*="panel-"]') || document;

		if (action === "find-password") {
			const idEl = panel.querySelector('input[name="id"]');
			const emailEl = panel.querySelector('input[name="email"]');

			const id = idEl?.value.trim() || "";
			const email = emailEl?.value.trim() || "";

			if (!id) { idEl.focus(); return; }
			if (!email) { emailEl.focus(); return; }

			btn.disabled = true;
			try {
				await fetchFindPassword(id, email);
			} finally {
				btn.disabled = false;
			}

		} else if (action === "find-id") {
			const nameEl = panel.querySelector('input[name="name"]');
			const emailEl = panel.querySelector('input[name="email"]');

			const name = nameEl?.value.trim() || "";
			const email = emailEl?.value.trim() || "";

			if (!name) { nameEl.focus(); return; }
			if (!email) { emailEl.focus(); return; }

			btn.disabled = true;
			try {
				await fetchFindId(name, email);
			} finally {
				btn.disabled = false;
			}
		}
	});
});

// 비밀번호 찾기
async function fetchFindPassword(id, email) {
	try {
		const res = await fetch("/api/account/password/reset/request", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ id, email })
		});

		if (res.ok) {
			const duration = 2000; // 토스트 표시 시간
			showToast("입력하신 주소로 메일이 전송되었습니다. 수신함/스팸함을 확인해주세요.", "success");
			// 토스트가 사라진 직후 로그인 페이지로 이동
			setTimeout(() => { location.href = "/login"; }, duration + 100);
		} else {
			const msg = await res.text().catch(() => "");
			showToast(msg || "요청 처리 중 오류가 발생했습니다.", "error");
		}
	} catch (err) {
		console.error(err);
		showToast("네트워크 오류가 발생했습니다.","error");
	}
}

async function fetchFindId(name, email) {
	const res = await fetch("/api/account/id/recovery", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ name, email }) // 이름+이메일로 아이디 찾기 (서버에서 마스킹 반환 권장)
	});
	if (!res.ok) {
		const msg = await res.text().catch(() => "");
		(window.showToast || alert)(msg || "요청 처리 중 오류가 발생했습니다.");
		return;
	}
	const data = await res.json().catch(() => ({}));
	// 예: { username: "se***un" } 또는 { message: "..." }
	if (data.username) {
		(window.showToast || alert)(`아이디: ${data.username}`);
	} else {
		(window.showToast || alert)(data.message || "입력하신 정보로 일치하는 계정을 찾을 수 없습니다.");
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