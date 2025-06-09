let accessToken = null;

function setAccessToken(token) {
	accessToken = token;
}

async function fetchWithAuth(input, init = {}) {
	const authInit = {
		...init,
		headers: {
			...(init.headers || {}),
			Authorization: accessToken ? `Bearer ${accessToken}` : "",
			"Content-Type": "application/json"
		},
		credentials: "include"
	};

	const response = await fetch(input, authInit);

	if (response.status === 401) {
		const refreshRes = await fetch("/token", {
			method: "POST",
			credentials: "include"
		});

		if (refreshRes.ok) {
			const data = await refreshRes.json();
			const newToken = data.token;
			setAccessToken(newToken);

			const retryInit = {
				...init,
				headers: {
					...(init.headers || {}),
					Authorization: `Bearer ${newToken}`,
					"Content-Type": "application/json"
				},
				credentials: "include"
			}

			return await fetch(input, retryInit);
		} else {
			throw new Error("서버 오류 발생");
		}
	}

	return response;
}

export { fetchWithAuth, setAccessToken };