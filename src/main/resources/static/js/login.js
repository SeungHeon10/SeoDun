// /oauth2/authorization/kakao
document.getElementById("kakao_login").addEventListener("click" , function(event) {
	event.preventDefault();
	
	location.href = "/oauth2/authorization/kakao";
	
});

document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const username = document.querySelector('input[name="username"]').value;
    const password = document.querySelector('input[name="password"]').value;
	
	const loginDTO = {
		"id" : username,
		"password" : password 
	}

    const response = await fetch("/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginDTO)
    });

    if (!response.ok) {
        throw new Error("서버 오류 발생");
    }
	
	const token = await response.json();
	localStorage.setItem("accessToken", token.token);
	
	location.href = "/";

});
