let accessToken = null;

const savedToken = localStorage.getItem("accessToken");
if (savedToken) {
	accessToken = savedToken;
}

function setAccessToken(token) {
	accessToken = token || null;
	if (token) {
		localStorage.setItem("accessToken", token);
	} else {
		localStorage.removeItem("accessToken");
	}
}

function isAuthenticated() {
	return !!accessToken;
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

	const skipRefresh = init.skipRefresh === true;

	let response = await fetch(input, authInit);

	// ★ 수정: 401이어도 skipRefresh면 재발급 안 함
	if (response.status === 401 && !skipRefresh) {
		const refreshRes = await fetch("/token", {
			method: "POST",
			credentials: "include"
		});

		if (refreshRes.ok) {
			const data = await refreshRes.json();
			const newToken = data.token;
			setAccessToken(newToken);

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
			return response;
		}
	}

	return response;
}

export { fetchWithAuth, setAccessToken, isAuthenticated };