function createCachedFetcher(apiUrl){
	let cachedData = null;
	
	async function fetchData(){
		if(cachedData) return cachedData;
		try{
			const response = await fetch(apiUrl);
			
			if(!response.ok){
				throw new Error("서버 오류 발생");
			}
			
			const data = await response.json();
			cachedData = data;
			return data;
		} catch(e){
			console.error("데이터 처리중 에러 발생" , e);
		}
	}
	
	function clearCache(){
		cachedData = null;
	}
	
	return { fetchData , clearCache };
}

export const userFetcher = createCachedFetcher('/user/id/list');