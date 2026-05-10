const PI = 3.14;
let circumstances;
let radius;

document.getElementById("submit").onclick=function(){
    radius=document.getElementById("input").value;
    radius=Number(radius);
    circumstances=2 * PI * radius;
    document.getElementById("circum").textContent = "The circumstances of circle is : "+ circumstances + " cm";
}