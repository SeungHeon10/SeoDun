let accessToken = null;

function setAccessToken(token) {
	accessToken = token;
}

async function fetchWithAuth(input, init = {}) {
	const isFormData = init.body instanceof FormData;

	const headers = {
		...(init.headers || {}),
		Authorization: accessToken ? `Bearer ${accessToken}` : ""
	};

	// FormData가 아니면 Content-Type 명시
	if (!isFormData) {
		headers["Content-Type"] = "application/json";
	}

	const authInit = {
		...init,
		headers,
		credentials: "include"
	};

	let response = await fetch(input, authInit);

	// AccessToken 만료 → RefreshToken으로 재요청
	if (response.status === 401) {
		const refreshRes = await fetch("/token", {
			method: "POST",
			credentials: "include"
		});

		if (refreshRes.ok) {
			const data = await refreshRes.json();
			const newToken = data.token;
			setAccessToken(newToken);

			// 새 토큰으로 재시도
			const retryHeaders = {
				...(init.headers || {}),
				Authorization: `Bearer ${newToken}`
			};

			if (!isFormData) {
				retryHeaders["Content-Type"] = "application/json";
			}

			const retryInit = {
				...init,
				headers: retryHeaders,
				credentials: "include"
			};

			response = await fetch(input, retryInit);
		} else {
			throw new Error("서버 오류 발생");
		}
	}

	return response;
}

export { fetchWithAuth, setAccessToken };