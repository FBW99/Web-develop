        
        /*======== Nav-bar and header responsive ========*/

        const menuBtn =  document.getElementById("menuBtn");
        const closeBtn =  document.getElementById("closeBtn");
        const navlist =  document.getElementById("navlist");


        function showMenu() {
            navlist.classList.add("active");
            closeBtn.style.display = 'block';
            menuBtn.style.display = 'none';
        }

        function closeMenu() {
            navlist.classList.remove("active");
            closeBtn.style.display = 'none';
            menuBtn.style.display = 'block';
        }

             /*=========== Scroll To Top Button Script ========== */

const btn = document.getElementById("scrollTopBtn");

window.onscroll = function () {
    if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
        btn.style.display = "block";
    } else {
        btn.style.display = "none";
    }
};

btn.onclick = function () {
    window.scrollTo({ top: 0, behavior: "smooth" });
};




