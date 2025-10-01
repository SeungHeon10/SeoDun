import { fetchWithAuth } from "/js/core/fetchWithAuth.js";

// CheckDone = 중복체크를 했는지 / Taken은 중복여부
let dupState = {
	nicknameCheckDone: false,
	nicknameTaken: false,
};

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

// 회원 등록 (가입하기)
async function fetchUserRegister() {
	const nameEl = document.getElementById('name');
	const nameError = document.getElementById("isInvalidName").textContent.trim();

	const nickEl = document.getElementById("nickname");
	const nicknameError = document.getElementById('isInvalidNickname').textContent.trim();

	const phoneEl = document.getElementById('phone');
	const phoneError = document.getElementById("isInvalidPhone").textContent.trim();

	// 필드별 형식오류 처리
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

	// 중복된 값 사용 처리
	if (dupState.nicknameTaken) {
		showToast("❗ 이미 사용 중인 닉네임입니다.", "error");
		nickEl.focus();
		return;
	}

	try {
		let userDTO = {
			name: nameEl.value,
			nickname: nickEl.value,
			pno: phoneEl.value
		}

		const response = await fetchWithAuth('/users/social', {
			method: "POST",
			body: JSON.stringify(userDTO)
		});

		// 실패 토스트
		if (!response.ok) {
			Toastify({
				text: "❗ 회원가입에 실패했습니다. 다시 시도해주세요.",
				duration: 2000,
				gravity: "bottom",
				position: "center",
				backgroundColor: "#f44336",
				close: true,
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
					gap: "31%",
				}
			}).showToast();
			return;
		}

		window.location.href = "/succ-member";

	} catch (e) {
		Toastify({
			text: "❗ 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
			duration: 2000,
			gravity: "bottom",
			position: "center",
			backgroundColor: "#f44336",
			close: true,
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
				gap: "18%",
			}
		}).showToast();
		console.error("에러:", e);
	}
}

// form 서밋 시 
document.getElementById("sign_up").addEventListener("submit", async (event) => {
	event.preventDefault();

	await fetchUserRegister();
})