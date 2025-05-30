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

// 회원 등록 (가입하기)
async function fetchUserRegister() {
	const nameError = document.getElementById("isInvalidName").textContent.trim();
	const phoneError = document.getElementById("isInvalidPhone").textContent.trim();

	// 하나라도 메시지가 있다면 토스트 출력하고 요청 중단
	if (nameError || phoneError) {
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
				gap: "46%",
			}
		}).showToast();
		return;
	}

	try {
		const name = document.getElementById("name").value;
		const phone = document.getElementById("phone").value;

		let userDTO = {
			name: name,
			pno: phone
		}

		const response = await fetch('/users/social', {
			method: "POST",
			headers: { "Content-Type": "application/json" },
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