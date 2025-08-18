import { fetchWithAuth } from "/js/core/fetchWithAuth.js";

const pathParts = window.location.pathname.split('/');
const isAdminPage = location.pathname.includes("/admin");
const id = isAdminPage ? pathParts[5] : pathParts[4];
const field = isAdminPage ? pathParts[6] : pathParts[5];
document.addEventListener("DOMContentLoaded", async () => {
	await fetchUserDetail();

	const debounce = (fn, ms = 500) => {
		let t; return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
	};
	const debouncedDupEmail = debounce(checkEmailAvailability, 500);
	const debouncedDupNickname = debounce(checkNicknameAvailability, 500);

	// 기존 비밀번호 입력 후 포커스 아웃 했을 시 기존 비밀번호와 일치하는지 확인
	onIf('#password', 'blur', checkCurrentPassword);

	// 새 비밀번호 입력 시
	onIf('#newPassword', 'input', function() {
		const password = this.value.trim();
		const isInvalidPw = document.getElementById('isInvalidPw');
		if (!isInvalidPw) return;

		if (password === '') { isInvalidPw.textContent = ''; return; }
		isInvalidPw.textContent = isValidPassword(password) ? '' : '비밀번호는 8~20자, 영문자/숫자/특수문자를 모두 포함하여 입력 가능합니다.';
		validatePasswordMatch();
	});

	// 새 비밀번호 확인 입력 시
	onIf('#newPasswordConfirm', 'input', validatePasswordMatch);

	// 새 이름 입력 시
	onIf('#newName', 'input', function() {
		const name = this.value.trim();
		const isInvalidName = document.getElementById('isInvalidName');
		if (!isInvalidName) return;

		if (name === '') { isInvalidName.textContent = ''; return; }
		const regex = /^[a-zA-Z가-힣]{1,20}$/;
		isInvalidName.textContent = regex.test(name) ? '' : '이름은 1자이상 20자이내의 문자로만 입력 가능합니다.';
	});

	// 닉네임 입력 시
	onIf('#newNickname', 'input', async function() {
		const nickname = this.value.trim();
		const isInvalidNickname = document.getElementById('isInvalidNickname');
		const isDuplicationNickname = document.getElementById('isDuplicationNickname');

		if (nickname === '') { isInvalidNickname.textContent = ''; isDuplicationNickname.textContent = ''; return; }

		const regex = /^[\p{L}\p{N}]{2,12}$/u;
		const valid = regex.test(nickname);
		isInvalidNickname.textContent = valid ? '' : '닉네임은 2자이상 12자이내의 문자/숫자로 입력 가능합니다.';
		if (!valid) { isDuplicationNickname.textContent = ''; return; }

		debouncedDupNickname();
	});

	// 닉네임 입력 후 포커스 아웃 시
	onIf('#newNickname', 'blur', () => checkNicknameAvailability());

	// 새 번호 입력 시
	onIf('#newPhone', 'input', function() {
		const isInvalidPhone = document.getElementById('isInvalidPhone');
		if (!isInvalidPhone) return;

		const onlyNum = this.value.replace(/[^0-9]/g, '');
		if (onlyNum === '') { isInvalidPhone.textContent = ''; return; }

		if (onlyNum.length <= 3) this.value = onlyNum;
		else if (onlyNum.length <= 7) this.value = `${onlyNum.slice(0, 3)}-${onlyNum.slice(3)}`;
		else this.value = `${onlyNum.slice(0, 3)}-${onlyNum.slice(3, 7)}-${onlyNum.slice(7, 11)}`;

		const valid = /^[0-9]{3}-[0-9]{4}-[0-9]{4}$/.test(this.value);
		isInvalidPhone.textContent = valid ? '' : '휴대폰 번호 형식에 맞게 숫자로 작성해주세요. (예: 010-1234-5678)';
	});

	// 이메일 입력 시
	onIf('#newEmail', 'input', async function() {
		const email = this.value.trim();
		const isInvalidEmail = document.getElementById('isInvalidEmail');
		const isDuplicationEmail = document.getElementById('isDuplicationEmail');

		if (email === '') { isInvalidEmail.textContent = ''; isDuplicationEmail.textContent = ''; return; }

		const regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
		const valid = regex.test(email);
		isInvalidEmail.textContent = valid ? '' : '이메일 형식에 맞게 입력 가능합니다.';
		if (!valid) { isDuplicationEmail.textContent = ''; return; }

		debouncedDupEmail();
	});

	// 이메일 입력 후 포커스 아웃 시
	onIf('#newEmail', 'blur', () => checkEmailAvailability());

	// 변경할 정보 입력 후 확인버튼 누를 시	
	document.querySelector("button.btn-dark").addEventListener("click", async () => {
		let payload;

		switch (field) {
			case "password": {
				const newPassword = document.querySelector("#newPassword");
				const newPasswordConfirm = document.querySelector("#newPasswordConfirm");
				const cur = document.getElementById("password");
				const curPwMsg = document.getElementById("curPwMsg");
				const isInvalidPw = document.getElementById("isInvalidPw");
				const isPasswordMismatch = document.getElementById("isPasswordMismatch");

				// 현재 / 변경할 값이 없을 때
				if (!cur.value) { cur.focus(); return; }
				if (!newPassword.value) { newPassword.focus(); return; }
				if (!newPasswordConfirm.value) { newPasswordConfirm.focus(); return; }

				// 형식/비밀번호 일치하지 않을 때
				if (curPwMsg.textContent !== "") { cur.focus(); return; }
				if (isInvalidPw.textContent !== "") { newPassword.focus(); return; }
				if (isPasswordMismatch.textContent !== "") { newPasswordConfirm.focus(); return; }

				payload = newPassword.value.trim();
				break;
			}
			case "name": {
				const newName = document.querySelector("#newName");
				const isInvalidName = document.getElementById('isInvalidName');

				// 변경할 이름 값 없을 때
				if (!newName.value) { newName.focus(); return; }

				// 형식 일치하지 않을 때
				if (isInvalidName.textContent !== "") { newName.focus(); return; }

				payload = newName.value.trim();
				break;
			}
			case "nickname": {
				const newNickname = document.querySelector("#newNickname");
				if (!newNickname.value) { newNickname.focus(); return; }

				const ok = await checkNicknameAvailability();
				if (!ok) {
					newNickname.focus();
					return;
				}

				payload = newNickname.value.trim();
				break;
			}
			case "phone": {
				const newPhone = document.querySelector("#newPhone");
				const isInvalidPhone = document.getElementById('isInvalidPhone');

				// 변경할 이름 값 없을 때
				if (!newPhone.value) { newPhone.focus(); return; }

				// 형식 일치하지 않을 때
				if (isInvalidPhone.textContent !== "") { newPhone.focus(); return; }

				payload = newPhone.value.trim();
				break;
			}
			case "email": {
				const newEmail = document.querySelector("#newEmail");
				if (!newEmail.value) { newEmail.focus(); return; }

				const ok = await checkEmailAvailability();
				if (!ok) {
					newEmail.focus();
					return;
				}

				payload = newEmail.value.trim();
				break;
			}
			case "role": {
				const newRole = document.querySelector("#roleSelect");
				if (!newRole.value) { newRole.focus(); return; }

				payload = newRole.value.trim();
				break;
			}
			default:
				throw new Error("지원하지 않는 항목입니다.");
		}

		await fetchUserEdit(payload);
	});
})

// 회원 상세정보
async function fetchUserDetail() {
	const apiSuffix = isAdminPage ? `/api/users/admin/${id}` : `/api/users/${id}`;
	try {
		const res = await fetchWithAuth(apiSuffix);
		if (!res.ok) throw new Error("서버 오류 발생");
		const detail = await res.json();

		renderEditBody(detail);
	} catch (e) {
		console.error("에러:", e.message);
	}
}

// 수정 화면 렌더링
function renderEditBody(detail) {
	const title = document.getElementById("editTitle");
	const body = document.getElementById("editContainer");

	switch (field) {
		case "password":
			title.textContent = "비밀번호 변경";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="d-flex flex-column gap-4">
						<div>
							<label for="password" class="form-label fw-semibold">현재 비밀번호</label>
							<input type="password" id="password" class="form-control form-control-lg"
								placeholder="현재 비밀번호를 입력하세요" autofocus/>
							<div id="curPwMsg" class="mt-1 small"></div>
						</div>
						<div>
							<label for="newPassword" class="form-label fw-semibold">새 비밀번호</label>
							<input type="password" id="newPassword" class="form-control form-control-lg"
								placeholder="변경할 비밀번호를 입력하세요" />
							<div id="isInvalidPw" class="mt-1 small text-danger"></div>
						</div>
						<div>
							<label for="newPasswordConfirm" class="form-label fw-semibold">새 비밀번호 확인</label>
							<input type="password" id="newPasswordConfirm" class="form-control form-control-lg"
								placeholder="변경할 비밀번호를 다시 입력해주세요" />
							<div id="isPasswordMismatch" class="mt-1 small text-danger"></div>
						</div>
					</div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
			break;
		case "name":
			title.textContent = "이름 변경하기";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="d-flex flex-column gap-4">
						<div>
							<label for="name" class="form-label fw-semibold">기존 이름</label>
							<input type="text" id="name" class="form-control form-control-lg"
								value="${detail.name}" disabled/>
						</div>
						<div>
							<label for="newName" class="form-label fw-semibold">새 이름</label>
							<input type="text" id="newName" class="form-control form-control-lg"
								placeholder="변경할 이름을 입력하세요" autofocus/>
							<div id="isInvalidName" class="mt-1 small text-danger"></div>
						</div>
					</div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
			break;
		case "nickname":
			title.textContent = "닉네임 변경하기";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="d-flex flex-column gap-4">
						<div>
							<label for="nickname" class="form-label fw-semibold">기존 닉네임</label>
							<input type="text" id="nickname" class="form-control form-control-lg"
								value="${detail.nickname}" disabled/>
						</div>
						<div>
							<label for="newNickname" class="form-label fw-semibold">새 닉네임</label>
							<input type="text" id="newNickname" class="form-control form-control-lg"
								placeholder="변경할 닉네임을 입력하세요" autofocus/>
							<div id="isInvalidNickname" class="mt-1 small text-danger"></div>
							<div id="isDuplicationNickname" class="mt-1 small"></div>
						</div>
					</div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
			break;
		case "phone":
			title.textContent = "휴대폰번호 변경";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="d-flex flex-column gap-4">
						<div>
							<label for="phone" class="form-label fw-semibold">기존 휴대폰번호</label>
							<input type="tel" id="phone" class="form-control form-control-lg"
								value="${detail.pno}" disabled/>
						</div>
						<div>
							<label for="newPhone" class="form-label fw-semibold">새 휴대폰번호</label>
							<input type="tel" id="newPhone" class="form-control form-control-lg"
								placeholder="변경할 휴대폰번호를 입력하세요" autofocus/>
							<div id="isInvalidPhone" class="mt-1 small text-danger"></div>
						</div>
					</div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
			break;
		case "email":
			title.textContent = "이메일 변경하기";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="d-flex flex-column gap-4">
						<div>
							<label for="email" class="form-label fw-semibold">기존 이메일</label>
							<input type="email" id="email" class="form-control form-control-lg"
								value="${detail.email}" disabled/>
						</div>
						<div>
							<label for="newEmail" class="form-label fw-semibold">새 이메일</label>
							<input type="email" id="newEmail" class="form-control form-control-lg"
								placeholder="변경할 이메일을 입력하세요" autofocus/>
							<div id="isInvalidEmail" class="mt-1 small text-danger"></div>
							<div id="isDuplicationEmail" class="mt-1 small"></div>
						</div>
					</div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
			break;
		case "role":
			title.textContent = "권한 변경하기";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="d-flex flex-column gap-4">
						<div>
							<label for="role" class="form-label fw-semibold">기존 권한</label>
							<input type="text" id="role" class="form-control form-control-lg"
								value="${detail.role}" disabled/>
						</div>
						<div>
							<label for="newRole" class="form-label fw-semibold">새 권한</label>
							<select id="roleSelect" class="form-select">
						    	<option value="ROLE_USER">USER</option>
						    	<option value="ROLE_ADMIN">ADMIN</option>
					    	</select>
						</div>
					</div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
			break;
		default:
			title.textContent = "잘못된 수정 항목";
			body.innerHTML = `
				<div class="d-grid gap-5">
					<div class="text-danger">수정할 수 없는 항목입니다: <code>${field ?? ""}</code></div>
					<button type="button" class="btn btn-dark btn-lg py-3">확인</button>
				</div>
			`;
	}
}

// 기존 비밀번호 확인 메서드
async function checkCurrentPassword() {
	const cur = document.getElementById("password").value;
	const curPwMsg = document.getElementById("curPwMsg");
	// 비어있으면 메시지 숨김
	if (!cur) { curPwMsg.textContent = ""; curPwMsg.className = "mt-1 small"; return; }

	try {
		const res = await fetchWithAuth(`/api/users/${id}/password/check`, {
			method: "POST",
			body: JSON.stringify(cur)
		});

		if (!res.ok) {
			throw new Error("검증 실패");
		}

		const valid = await res.json();

		if (valid) {
			curPwMsg.textContent = "현재 비밀번호가 일치합니다.";
			curPwMsg.className = "mt-1 small text-success";
		} else {
			curPwMsg.textContent = "현재 비밀번호가 일치하지 않습니다.";
			curPwMsg.className = "mt-1 small text-danger";
		}
	} catch (e) {
		curPwMsg.textContent = "확인 중 오류가 발생했습니다.";
		curPwMsg.className = "mt-1 small text-danger";
	}
}

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

// 이메일 중복확인
async function checkEmailAvailability() {
	const email = document.getElementById("newEmail").value.trim();
	const isInvalidEmail = document.getElementById('isInvalidEmail');
	const isDuplicationEmail = document.getElementById('isDuplicationEmail');
	const regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;

	// 빈 값
	if (email === '') {
		isInvalidEmail.textContent = '';
		isDuplicationEmail.textContent = '';
		return false;
	}

	// 형식 검사
	const okFormat = regex.test(email);
	isInvalidEmail.textContent = okFormat ? '' : '이메일 형식에 맞게 입력 가능합니다.';
	if (!okFormat) return false;

	// 서버 중복 검사
	try {
		const message = await fetchUserEmail(email);
		const available = message.includes('가능한');
		isDuplicationEmail.textContent = message;
		if (available) {
			isDuplicationEmail.classList.remove('text-danger');
			isDuplicationEmail.classList.add('text-success');
			return available;
		} else {
			isDuplicationEmail.classList.add('text-danger');
			isDuplicationEmail.classList.remove('text-success');
			return false;
		}
	} catch {
		isDuplicationEmail.textContent = '확인 중 오류가 발생했습니다.';
		isDuplicationEmail.classList.add('text-danger');
		isDuplicationEmail.classList.remove('text-success');
		return false;
	}
}

// 닉네임 중복확인
async function checkNicknameAvailability() {
	const nickname = document.getElementById("newNickname").value.trim();
	const isInvalidNickname = document.getElementById('isInvalidNickname');
	const isDuplicationNickname = document.getElementById('isDuplicationNickname');
	const regex = /^[\p{L}\p{N}]{2,12}$/u;

	// 빈 값
	if (nickname === '') {
		isInvalidNickname.textContent = '';
		isDuplicationNickname.textContent = '';
		return false;
	}

	// 형식 검사
	const okFormat = regex.test(nickname);
	isInvalidNickname.textContent = okFormat ? '' : '이메일 형식에 맞게 입력 가능합니다.';
	if (!okFormat) return false;

	// 서버 중복 검사
	try {
		const message = await fetchUserNickname(nickname);
		const available = message.includes('가능한');
		isDuplicationNickname.textContent = message;
		if (available) {
			isDuplicationNickname.classList.remove('text-danger');
			isDuplicationNickname.classList.add('text-success');
			return available;
		} else {
			isDuplicationNickname.classList.add('text-danger');
			isDuplicationNickname.classList.remove('text-success');
			return false;
		}
	} catch {
		isDuplicationNickname.textContent = '확인 중 오류가 발생했습니다.';
		isDuplicationEmail.classList.add('text-danger');
		isDuplicationEmail.classList.remove('text-success');
		return false;
	}
}

// 이메일 중복여부
async function fetchUserEmail(email) {
	const response = await fetchWithAuth(`/api/users/exists/email/${email}`);

	if (!response.ok) {
		throw new Error("서버 오류 발생");
	}

	return response.text();
}

// 닉네임 중복여부
async function fetchUserNickname(nickname) {
	const response = await fetchWithAuth(`/api/users/exists/nickname/${nickname}`);

	if (!response.ok) {
		throw new Error("서버 오류 발생");
	}

	return response.text();
}

// 회원 정보 수정
async function fetchUserEdit(payload) {
	try {
		const res = await fetchWithAuth(`/api/users/${id}/${encodeURIComponent(field)}`, {
			method: "PUT",
			body: JSON.stringify(payload),
		});

		if (!res.ok) {
			throw new Error("검증 실패");
		}

		if (isAdminPage) {
			location.href = `/user/detail/admin/${id}?updated=true`;
		} else {
			location.href = `/user/detail/${id}?updated=true`;
		}

	} catch (e) {
		alert(e.message || "오류가 발생했어요.");
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