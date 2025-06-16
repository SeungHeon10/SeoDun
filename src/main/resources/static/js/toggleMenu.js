document.addEventListener("DOMContentLoaded", () => {
	const wrapper = document.querySelector(".wrapper");
	const menuToggle = document.getElementById("menuToggle");

	menuToggle.addEventListener("click", () => {
		wrapper.classList.toggle("sidebar-open");
	});

	document.addEventListener("click", (e) => {
		const sidebar = document.getElementById("sidebar");
		if (
			wrapper.classList.contains("sidebar-open") &&
			!sidebar.contains(e.target) &&
			!menuToggle.contains(e.target)
		) {
			wrapper.classList.remove("sidebar-open");
		}
	});
});