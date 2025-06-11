import { fetchWithAuth } from "./fetchWithAuth.js";

function createCachedFetcher() {
	const cache = new Map();

	async function fetchData(key) {
		if (cache.has(key)) return cache.get(key);

		try {
			const response = await fetchWithAuth(key);  // key = apiUrl

			if (!response.ok) {
				throw new Error("서버 오류 발생");
			}

			const data = await response.json();
			cache.set(key, data);
			return data;
		} catch (e) {
			console.error("데이터 처리 중 에러 발생", e);
		}
	}

	function clearCache(key) {
		if (key) {
			cache.delete(key);
		} else {
			cache.clear();
		}
	}

	return { fetchData, clearCache };
}

export const cachedFetcher = createCachedFetcher();