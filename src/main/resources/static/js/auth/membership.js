import { fetchWithAuth } from "/js/core/fetchWithAuth.js";

// CheckDone = 중복체크를 했는지 / Taken은 중복여부
let dupState = {
	idCheckDone: false,
	idTaken: false,
	nicknameCheckDone: false,
	nicknameTaken: false,
	emailCheckDone: false,
	emailTaken: false
};

// 아이디 중복여부
async function fetchUserId(id) {
	try {
		const response = await fetchWithAuth(`/api/users/exists/id/${id}`);

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		return response.json();
	} catch (e) {
		showToast(e.message || `확인 중 오류가 발생했습니다.`, error);
	}

}

// 이메일 중복여부
async function fetchUserEmail(email) {
	try {
		const response = await fetchWithAuth(`/api/users/exists/email/${email}`);

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		return response.text();
	} catch (e) {
		showToast(e.message || `확인 중 오류가 발생했습니다.`, error);
	}
}

// 닉네임 중복여부
async function fetchNickname(nickname) {
	// 서버 중복 검사
	try {
		const response = await fetchWithAuth(`/api/users/exists/nickname/${nickname}`);

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		return response.text();
	} catch {
		isDuplicationNickname.textContent = '확인 중 오류가 발생했습니다.';
		isDuplicationEmail.classList.add('text-danger');
		isDuplicationEmail.classList.remove('text-success');
		return false;
	}
}

// 아이디 입력 시 
document.getElementById("id").addEventListener("input", async function() {
	const id = this.value.trim();
	const regex = /^[a-zA-Z0-9]{4,20}$/;

	const isDuplicationId = document.getElementById("isDuplicationId");
	const isInvalidId = document.getElementById("isInvalidId");
	const vaildId = document.getElementById("vaildId");

	// 빈 값일 경우: 모든 메시지 초기화 후 종료
	if (id === "") {
		isDuplicationId.textContent = "";
		isInvalidId.textContent = "";
		vaildId.textContent = "";
		// 상태 초기화
		dupState.idCheckDone = false;
		dupState.idTaken = false;
		return;
	}

	// 정규식 검사
	const isValid = regex.test(id);
	isInvalidId.textContent = isValid ? "" : "아이디는 4자 이상 20자 이내의 영문 또는 숫자여야 합니다.";

	// 유효하지 않으면 중복 검사하지 않음
	if (!isValid) {
		vaildId.textContent = "";
		isDuplicationId.textContent = "";

		dupState.idCheckDone = false;
		dupState.idTaken = false;
		return;
	}

	// 중복 검사
	const isDuplicate = await fetchUserId(id);

	dupState.idCheckDone = true;
	dupState.idTaken = !isDuplicate;

	if (isDuplicate) {
		vaildId.textContent = "사용 가능한 아이디입니다.";
		vaildId.style.color = "green";
		isDuplicationId.textContent = "";
	} else {
		isDuplicationId.textContent = "이미 존재하는 아이디입니다.";
		isDuplicationId.style.color = "red";
		vaildId.textContent = "";
	}
});

// 비밀번호 유효성 검사
function isValidPassword(password) {
	const regex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;
	return regex.test(password);
}

const passwordInput = document.getElementById("password");
const passwordConfirmInput = document.getElementById("passwordConfirm");
const isInvalidPw = document.getElementById("isInvalidPw");
const isPasswordMismatch = document.getElementById("isPasswordMismatch");

// 비밀번호 입력 시
passwordInput.addEventListener("input", function() {
	const password = this.value.trim();

	// 빈 값일 경우: 모든 메시지 초기화 후 종료
	if (password === "") {
		isInvalidPw.textContent = "";
		return;
	}

	isInvalidPw.textContent = isValidPassword(password) ? "" : "비밀번호는 8자 이상 20자 이내로 입력 가능합니다.";

	// 비밀번호 확인도 동시에 비교 (동기화)
	validatePasswordMatch();
});

// 비밀번호 확인 입력 시
passwordConfirmInput.addEventListener("input", validatePasswordMatch);

// 비밀번호 재확인 함수
function validatePasswordMatch() {
	const pw = passwordInput.value.trim();
	const pwConfirm = passwordConfirmInput.value.trim();

	isPasswordMismatch.textContent = pw && pwConfirm && pw !== pwConfirm ? "입력하신 비밀번호와 다릅니다." : "";
}

// 이름 입력 시
document.getElementById("name").addEventListener("input", function() {
	const name = this.value.trim();
	const regex = /^[a-zA-Z가-힣]{1,20}$/;
	const isInvalidName = document.getElementById("isInvalidName");

	// 빈 값일 경우: 모든 메시지 초기화 후 종료
	if (name === "") {
		isInvalidName.textContent = "";
		return;
	}

	//	이름 유효성 검사
	const isValid = regex.test(name);
	isInvalidName.textContent = isValid ? "" : "이름은 1자이상 20자이내의 문자로만 입력 가능합니다.";
});

// 닉네임 입력 시
document.getElementById("nickname").addEventListener("input", async function() {
	const nickname = this.value.trim();
	const isInvalidNickname = document.getElementById('isInvalidNickname');
	const isDuplicationNickname = document.getElementById('isDuplicationNickname');
	const regex = /^[\p{L}\p{N}]{2,12}$/u;

	// 빈 값
	if (nickname === '') {
		isInvalidNickname.textContent = '';
		isDuplicationNickname.textContent = '';
		// 상태 초기화
		dupState.nicknameCheckDone = false;
		dupState.nicknameTaken = false;
		return;
	}

	// 형식 검사
	const okFormat = regex.test(nickname);
	isInvalidNickname.textContent = okFormat ? '' : '닉네임 형식에 맞게 입력 가능합니다.';
	if (!okFormat) {
		dupState.nicknameCheckDone = false;
		dupState.nicknameTaken = false;
		return;
	}

	// 서버에서 중복검사
	const message = await fetchNickname(nickname);

	const available = message.includes('가능한');

	dupState.nicknameCheckDone = true;
	dupState.nicknameTaken = !available;

	isDuplicationNickname.textContent = message;
	if (available) {
		isDuplicationNickname.classList.remove('text-danger');
		isDuplicationNickname.classList.add('text-success');
	} else {
		isDuplicationNickname.classList.add('text-danger');
		isDuplicationNickname.classList.remove('text-success');
	}
});

// 번호 입력 시
document.getElementById("phone").addEventListener("input", function() {
	const phone = this.value.replace(/[^0-9]/g, "");
	const regex = /^[0-9]{3}-[0-9]{4}-[0-9]{4}$/;
	const isInvalidPhone = document.getElementById("isInvalidPhone");

	// 빈 값일 경우: 모든 메시지 초기화 후 종료
	if (phone === "") {
		isInvalidPhone.textContent = "";
		return;
	}

	// 2. 하이픈 자동 삽입
	if (phone.length <= 3) {
		this.value = phone;
	} else if (phone.length <= 7) {
		this.value = `${phone.slice(0, 3)}-${phone.slice(3)}`;
	} else {
		this.value = `${phone.slice(0, 3)}-${phone.slice(3, 7)}-${phone.slice(7, 11)}`;
	}

	// 3. 유효성 검사 (하이픈 포함 값 기준)
	const formatted = this.value;

	if (formatted === "") {
		isInvalidPhone.textContent = "";
		return;
	}

	isInvalidPhone.textContent = regex.test(formatted) ? "" : "휴대폰 번호 형식에 맞게 숫자로 작성해주세요. (예: 010-1234-5678)";
});

// 이메일 입력 시
document.getElementsByName("email")[0].addEventListener("input", async function() {
	const email = this.value.trim();
	const regex = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
	const isInvalidEmail = document.getElementById("isInvalidEmail");
	const isDuplicationEmail = document.getElementById("isDuplicationEmail");

	// 빈 값일 경우: 모든 메시지 초기화 후 종료
	if (email === "") {
		isInvalidEmail.textContent = "";
		// 상태 초기화
		dupState.emailCheckDone = false;
		dupState.emailTaken = false;
		return;
	}

	//	이메일 유효성 검사
	const isValid = regex.test(email);
	isInvalidEmail.textContent = isValid ? "" : "이메일 형식에 맞게 입력 가능합니다.";

	// 유효하지 않으면 중복검사 x
	if (!isValid) {
		isDuplicationEmail.textContent = "";
		dupState.emailCheckDone = false;
		dupState.emailTaken = false;
		return;
	}

	const message = await fetchUserEmail(email);
	const available = message.includes("가능");

	dupState.emailCheckDone = true;
	dupState.emailTaken = !available;

	isDuplicationEmail.textContent = message;

	if (available) {
		isDuplicationEmail.style.color = "green";
	} else {
		isDuplicationEmail.style.color = "red";
	}
});

// 이메일 전송 로직
document.getElementById("sendEmailBtn").addEventListener("click", async function() {
	const email = document.getElementById("email").value;
	const invalidEl = document.getElementById("isInvalidEmail");
	const dupCheckEl = document.getElementById("isDuplicationEmail");
	const feedbackEl = document.getElementById("emailFeedback");
	const codeBox = document.getElementById("codeBox");
	const verifyCodeInput = document.getElementById("verifyCode");
	const verifyCodeBtn = document.getElementById("verifyCodeBtn");

	if (!email) {
		showToast("❗ 이메일을 입력해주세요.", "error");
		return;
	}

	// 이메일 형식 오류 존재 시
	const isInvalidText = invalidEl.textContent.trim() !== "";
	const isInvalidColor = window.getComputedStyle(invalidEl).color === "rgb(255, 0, 0)";
	if (isInvalidText && isInvalidColor) {
		showToast("❗ 올바른 이메일 형식을 입력해주세요.", "error");
		return;
	}

	// 이메일 중복 오류 존재 시
	const isDupText = dupCheckEl.textContent.includes("이미");
	const isDupColor = window.getComputedStyle(dupCheckEl).color === "rgb(255, 0, 0)";
	if (isDupText && isDupColor) {
		showToast("❗ 사용할 수 없는 이메일입니다.", "error");
		return;
	}

	dupCheckEl.textContent = "";

	try {
		const response = await fetchWithAuth(`/auth/emails?email=${encodeURIComponent(email)}`, {
			method: "POST"
		});

		if (!response.ok) {
			throw new Error("서버 오류 발생");
		}

		const message = await response.text();

		codeBox.style.display = "flex";
		feedbackEl.textContent = message;
		feedbackEl.classList.remove("text-danger", "text-success");
		feedbackEl.classList.add(message.includes("실패") ? "text-danger" : "text-success");

		verifyCodeBtn.disabled = false;
		verifyCodeInput.readOnly = false;

	} catch (error) {
		console.error("전송 오류:", error);
		showToast("❗ 인증 메일 전송 중 오류가 발생했습니다.", "error");
	}
});

// 이메일 인증번호 확인 로직
document.getElementById("verifyCodeBtn").addEventListener("click", async () => {
	const email = document.getElementById("email").value;
	const code = document.getElementById("verifyCode").value;
	const feedback = document.getElementById("emailFeedback");
	const verifyCodeInput = document.getElementById("verifyCode");
	const verifyCodeBtn = document.getElementById("verifyCodeBtn");

	try {
		const res = await fetchWithAuth(`/auth/emails/verify?email=${encodeURIComponent(email)}&code=${code}`, {
			method: "POST"
		});

		const message = await res.text();
		feedback.textContent = message;
		feedback.classList.remove("text-danger", "text-success");

		if (res.ok) {
			feedback.classList.add("text-success");
			// 인증 성공 flag 기억 (예: 전역 변수 또는 hidden input)
			window.emailVerified = true;
			verifyCodeBtn.disabled = true;
			verifyCodeInput.readOnly = true;
		} else {
			feedback.classList.add("text-danger");
		}
	} catch (error) {
		console.error("인증 오류:", error);
		feedback.textContent = "인증 중 오류가 발생했습니다.";
		feedback.classList.add("text-danger");
	}
});


// 회원 등록 (가입하기)
async function fetchUserRegister() {
	const idEl = document.getElementById("id");
	const idError = document.getElementById("isInvalidId").textContent.trim();

	const pwEl = document.getElementById('password');
	const pw2El = document.getElementById('passwordConfirm');
	const pwError = document.getElementById("isInvalidPw").textContent.trim();
	const pwMismatch = document.getElementById("isPasswordMismatch").textContent.trim();

	const emailEl = document.getElementById("email");
	const emailError = document.getElementById("isInvalidEmail").textContent.trim();

	const nameEl = document.getElementById('name');
	const nameError = document.getElementById("isInvalidName").textContent.trim();

	const nickEl = document.getElementById("nickname");
	const nicknameError = document.getElementById('isInvalidNickname').textContent.trim();

	const phoneEl = document.getElementById('phone');
	const phoneError = document.getElementById("isInvalidPhone").textContent.trim();

	// 필드별 형식오류 처리
	if (idError) {
		showToast("❗ 아이디 형식이 올바르지 않습니다.", "error");
		idEl.focus();
		return;
	}
	if (pwError) {
		showToast("❗ 비밀번호 형식이 올바르지 않습니다.", "error");
		pwEl.focus();
		return;
	}
	if (pwMismatch) {
		showToast("❗ 비밀번호가 서로 일치하지 않습니다.", "error");
		pw2El.focus();
		return;
	}
	if (emailError) {
		showToast("❗ 이메일 형식이 올바르지 않습니다.", "error");
		emailEl.focus();
		return;
	}
	if (nameError) {
		showToast("❗ 이름을 올바르게 입력해 주세요.", "error");
		nameEl.focus();
		return;
	}
	if (nicknameError) {
		showToast("❗ 닉네임 형식이 올바르지 않습니다.", "error");
		nickEl.focus();
		return;
	}
	if (phoneError) {
		showToast("❗ 휴대폰 번호 형식이 올바르지 않습니다.", "error");
		phoneEl.focus();
		return;
	}

	// 중복확인 미실시 처리
	if (!dupState.idCheckDone) {
		showToast("❗ 아이디 중복확인을 완료해주세요.", "error");
		idEl.focus();
		return;
	}
	if (!dupState.nicknameCheckDone) {
		showToast("❗ 닉네임 중복확인을 완료해주세요.", "error");
		nickEl.focus();
		return;
	}
	if (!dupState.emailCheckDone) {
		showToast("❗ 이메일 중복확인을 완료해주세요.", "error");
		emailEl.focus();
		return;
	}

	// 중복된 값 사용 처리
	if (dupState.idTaken) {
		showToast("❗ 이미 사용 중인 아이디입니다.", "error");
		idEl.focus();
		return;
	}
	if (dupState.nicknameTaken) {
		showToast("❗ 이미 사용 중인 닉네임입니다.", "error");
		nickEl.focus();
		return;
	}
	if (dupState.emailTaken) {
		showToast("❗ 이미 사용 중인 이메일입니다.", "error");
		emailEl.focus();
		return;
	}

	// 이메일 인증 미실시 처리
	if (!window.emailVerified) {
		showToast("이메일 인증을 먼저 완료해주세요.", "error");
		return;
	}

	try {
		let userDTO = {
			id: idEl.value,
			password: pwEl.value,
			name: nameEl.value,
			nickname: nickEl.value,
			pno: phoneEl.value,
			email: emailEl.value,
			emailVerified: window.emailVerified
		}

		const response = await fetchWithAuth('/api/users', {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(userDTO)
		});

		// 실패 토스트
		if (!response.ok) {
			showToast("❗ 회원가입에 실패했습니다. 다시 시도해주세요.", "error");
			return;
		}

		window.location.href = "/succ-member";

	} catch (e) {
		showToast("❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", "error");
		console.error("에러:", e);
	}
}

// form 서밋 시 
document.getElementById("sign_up").addEventListener("submit", async (event) => {
	event.preventDefault();

	await fetchUserRegister();
});

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
