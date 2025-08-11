import { fetchWithAuth, setAccessToken } from "/js/core/fetchWithAuth.js";

const pathParts = window.location.pathname.split('/');
const id = pathParts[5];
const field = pathParts[6];
document.addEventListener("DOMContentLoaded", async () => {
	await fetchUserDetail();
})

async function fetchUserDetail() {
	try {
		const res = await fetchWithAuth(`/api/users/admin/${id}`);
		if (!res.ok) throw new Error("서버 오류 발생");
		const detail = await res.json();

		renderEditBody(detail);
	} catch (e) {
		console.error("에러:", e.message);
	}
}

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
								placeholder="현재 비밀번호를 입력하세요" />
						</div>
						<div>
							<label for="newPassword" class="form-label fw-semibold">새 비밀번호</label>
							<input type="password" id="newPassword" class="form-control form-control-lg"
								placeholder="변경할 비밀번호를 입력하세요" />
						</div>
						<div>
							<label for="newPasswordConfirm" class="form-label fw-semibold">새 비밀번호 확인</label>
							<input type="password" id="newPasswordConfirm" class="form-control form-control-lg"
								placeholder="변경할 비밀번호를 다시 입력해주세요" />
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
								placeholder="변경할 이름을 입력하세요" />
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
								placeholder="변경할 닉네임을 입력하세요" />
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
								placeholder="변경할 휴대폰번호를 입력하세요" />
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
								placeholder="변경할 이메일을 입력하세요" />
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