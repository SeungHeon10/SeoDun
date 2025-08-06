import { fetchWithAuth } from "/js/fetchWithAuth.js";

// 아이디 중복여부
async function fetchUserId(id) {
	const response = await fetchWithAuth(`/users/exists/id/${id}`);

	if (!response.ok) {
		throw new Error("서버 오류 발생");
	}

	return response.json();
}

// 이메일 중복여부
async function fetchUserEmail(email) {
	const response = await fetchWithAuth(`/users/exists/email/${email}`);

	if (!response.ok) {
		throw new Error("서버 오류 발생");
	}

	return response.text();
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
		return;
	}

	// 정규식 검사
	const isValid = regex.test(id);
	isInvalidId.textContent = isValid ? "" : "아이디는 4자 이상 20자 이내의 영문 또는 숫자여야 합니다.";

	// 유효하지 않으면 중복 검사하지 않음
	if (!isValid) {
		vaildId.textContent = "";
		isDuplicationId.textContent = "";
		return;
	}

	// 중복 검사
	const isDuplicate = await fetchUserId(id);
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
		return;
	}

	//	이메일 유효성 검사
	const isValid = regex.test(email);
	isInvalidEmail.textContent = isValid ? "" : "이메일 형식에 맞게 입력 가능합니다.";

	// 유효하지 않으면 중복검사 x
	if (!isValid) {
		isDuplicationEmail.textContent = "";
		return;
	}

	const message = await fetchUserEmail(email);

	isDuplicationEmail.textContent = message;

	if (message.includes("가능한")) {
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
	const idError = document.getElementById("isInvalidId").textContent.trim();
	const duplicateError = document.getElementById("isDuplicationId").textContent.trim();
	const pwError = document.getElementById("isInvalidPw").textContent.trim();
	const pwMismatch = document.getElementById("isPasswordMismatch").textContent.trim();
	const emailError = document.getElementById("isInvalidEmail").textContent.trim();
	const nameError = document.getElementById("isInvalidName").textContent.trim();
	const phoneError = document.getElementById("isInvalidPhone").textContent.trim();

	// 하나라도 메시지가 있다면 토스트 출력하고 요청 중단
	if (idError || duplicateError || pwError || pwMismatch || emailError || nameError || phoneError) {
		showToast("❗ 입력한 정보를 다시 확인해주세요.", "error");
		return;
	}
	
	if (!window.emailVerified) {
	    showToast("이메일 인증을 먼저 완료해주세요.", "error");
	    return;
	}

	try {
		const id = document.getElementById("id").value;
		const password = document.getElementById("password").value;
		const name = document.getElementById("name").value;
		const phone = document.getElementById("phone").value;
		const email = document.getElementById("email").value;

		let userDTO = {
			id: id,
			password: password,
			name: name,
			pno: phone,
			email: email
		}

		const response = await fetchWithAuth('/users', {
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
