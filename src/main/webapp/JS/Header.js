function myFunction(){document.getElementById("myDropdown").classList.toggle("header-show")}function toggleMenu(){document.getElementById("lista").classList.toggle("header-showlista")}window.onload=function(){!0===Logged&&(document.getElementById("utente").innerHTML=`
                    <form action="areaUtenteServlet" method="post">
                        <button type="submit" class="header-custom_button">Area Personale</button>
                    </form>
                `)},window.onclick=function(e){if(!e.target.matches(".header-dropbutton")){let t=document.getElementsByClassName("header-dropdown-content");for(let o=0;o<t.length;o++){let n=t[o];n.classList.contains("header-show")&&n.classList.remove("header-show")}}};