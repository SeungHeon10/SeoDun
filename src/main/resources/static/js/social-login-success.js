console.log("✅ social-login-success.js loaded as module");
import { setAccessToken } from "/js/fetchWithAuth.js";
const token = window.name;

if (token) {
	setAccessToken(token);
	window.name = "";
	location.href = "/";
} else {
	alert("로그인 중 문제가 발생했습니다.");
}