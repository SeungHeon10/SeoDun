//import { fetchWithAuth } from "/js/core/fetchWithAuth.js";
let isCodeVerified = false; // 이메일 인증 코드 확인여부

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

			const id = idEl.value.trim() || "";
			const email = emailEl.value.trim() || "";

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

			const name = nameEl.value.trim() || "";
			const email = emailEl.value.trim() || "";

			if (!name) { nameEl.focus(); return; }
			if (!email) { emailEl.focus(); return; }

			if (!isCodeVerified) { showToast("이메일 인증을 완료한 후 다시 시도해주세요.", "error"); return; }

			const idRecoveryDTO = {
				name: name,
				email: email
			}
			btn.disabled = true;
			try {
				await fetchFindId(idRecoveryDTO);
			} finally {
				btn.disabled = false;
			}
		}
	});

	onIf('#sendEmailBtn', "click", async function() {
		const emailEl = document.querySelector('input[name="email"]');
		const email = emailEl.value.trim() || "";

		if (!email) { emailEl.focus(); return; }

		await fetchRequestIdLookupCode(email);
	});

	onIf('#verifyCodeBtn', "click", async function() {
		const emailEl = document.querySelector('input[name="email"]');
		const email = emailEl.value.trim() || "";
		const codeEl = document.querySelector('#verifyCode');
		const code = codeEl.value.trim() || "";

		if (!email) { emailEl.focus(); return; }
		if (!code) { codeEl.focus(); return; }

		const idLookupVerifyDTO = {
			email: email,
			code: code
		}

		await fetchVerificationCodeConfirm(idLookupVerifyDTO);
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
			const msg = await res.text();
			const duration = 2000; // 토스트 표시 시간
			showToast(msg || "입력하신 주소로 재설정 메일이 전송되었습니다.", "success");
			// 토스트가 사라진 직후 로그인 페이지로 이동
			setTimeout(() => { location.href = "/login"; }, duration + 100);
		} else {
			const errmsg = await res.text().catch(() => "");
			showToast(errmsg || "요청 처리 중 오류가 발생했습니다.", "error");
		}
	} catch (err) {
		console.error(err);
		showToast("네트워크 오류가 발생했습니다.", "error");
	}
}

// 아이디 찾기 인증 메일 요청
async function fetchRequestIdLookupCode(email) {
	try {
		const res = await fetch(`/api/account/id/recovery/${email}`, {
			method: "POST",
			headers: { "Content-Type": "application/json" }
		});

		if (res.ok) {
			const msg = await res.text();
			showToast(msg || "입력하신 주소로 인증코드를 보냈습니다.", "success");
		} else {
			const errmsg = await res.text().catch(() => "");
			showToast(errmsg || "요청 처리 중 오류가 발생했습니다.", "error");
		}
	} catch (err) {
		console.error(err);
		showToast("네트워크 오류가 발생했습니다.", "error");
	}
}

// 아이디 찾기 인증 코드 확인
async function fetchVerificationCodeConfirm(DTO) {
	const confirmMsg = document.getElementById("idLookupConfirmMessage");

	try {
		const res = await fetch(`/api/account/id/recovery/confirm`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(DTO)
		});

		if (res.ok) {
			const msg = await res.text();
			confirmMsg.textContent = msg || "인증번호가 일치합니다.";
			confirmMsg.classList.remove("text-danger");
			confirmMsg.classList.add("text-success");
			isCodeVerified = true;
			return;
		}

		// 예외 발생 시
		const errText = await res.text();
		if (res.status === 400) {
			confirmMsg.textContent = errText || "요청이 올바르지 않습니다. 다시 시도해주세요.";
			confirmMsg.classList.remove("text-success");
			confirmMsg.classList.add("text-danger");
		} else {
			confirmMsg.textContent = errText || "요청 처리 중 오류가 발생했습니다. 다시 시도해주세요.";
			confirmMsg.classList.remove("text-success");
			confirmMsg.classList.add("text-danger");
		}
	} catch (err) {
		console.error(err);
		showToast("네트워크 오류가 발생했습니다.", "error");
	}
}

// 아이디 찾기
async function fetchFindId(DTO) {
	const res = await fetch("/api/account/id/recovery", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(DTO)
	});

	if (!res.ok) {
		const msg = await res.text();
		showToast(msg || "요청 처리 중 오류가 발생했습니다. 다시 시도해주세요.", "error");
		return;
	}

	const data = await res.text();

	renderFindId(data);
}

function renderFindId(data) {
	const panel = document.querySelector(".recovery-method");

	// 패널 내용 초기화
	panel.innerHTML = "";

	panel.innerHTML = `
		<div class="text-center py-5">
	    	<p class="fs-5 mb-5">회원님의 아이디는 <strong>${data}</strong> 입니다.</p>
			<div class="d-flex justify-content-center gap-3">
	    		<button type="button" class="btn btn-dark px-4" onclick="location.href='/login'">로그인</button>
	    		<button type="button" class="btn btn-dark px-4" onclick="location.href='/'">메인으로</button>
			<div>
		</div>
	`;
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