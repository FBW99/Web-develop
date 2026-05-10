function sayHello(){
    alert("Assalamualeykum warahmatulahi wabarakatuhu !!!")
}
let Person="Hello,I am web developer student";
let x=3;
let y=10;

console.log(typeof x);
console.log(5==="5");

let age1=20;
let result=(age1>=20)? "Adult": "minor";
console.log(result);

document.getElementById("demo").innerHTML="Welcome to JavaScript Programming <br>" + (++x * ++y +x );
  
let Name="Faruk Bati"
console.log(`your name is ${Name}`)
let UserName;
document.getElementById("mySubmit").onclick=function(){
    UserName=document.getElementById("myText").value;
    document.getElementById("welcomeMessage").innerHTML=`Welcome ${UserName} to JavaScript Programming`
} 
const PI=3.14;
let radius;
let circumstances;
document.getElementById("myresult").onclick= function(){
    radius= document.getElementById("Myradius").value;
    circumstances= 2 *  PI * radius ;
    document.getElementById("h3").textContent="Circumstances of circle is : " + circumstances;
}

let randomNUmber;
let min=1;
let max=9;
document.getElementById("myb1").onclick=function(){
    randomNUmber=Math.floor(Math.random() * max) + min;
    document.getElementById("mylabel").textContent="Random Number : "+ randomNUmber;
}
const myage=document.getElementById("myage");
const ageSubmit=document.getElementById("ageSubmit");
const resultElement=document.getElementById("resultElement");
let age;
ageSubmit.onclick=function(){
    age=myage.value;
    age=Number(age);

    if(age>=100){     
        resultElement.textContent= "You are TOO old  to enter this site";
    }
    else if(age>=18){
        resultElement.textContent= `You are old enough to enter this site`;
    }
    else if(age<18){
         resultElement.textContent= `You must be 18+ to enter this site`;
    }
    else if(age==0){
        resultElement.textContent= `You can't enter, you were just born`;
    }
    else {
        resultElement.textContent= `You can't enter,your age can't below 0`;
    }
}

const mycheck=document.getElementById("mycheck");
const paypalbtn=document.getElementById("paypalbtn");
const visabtn=document.getElementById("visabtn");
const submitbtn=document.getElementById("submitbtn");
const seeresult=document.getElementById("seeresult");
let results1;
let Payments;

submitbtn.onclick=function(){
    if(mycheck.checked){
       results1=`you are SUBSCRIBED !`
    }
    else{
        results1=`you are not SUBSCRIBED !`
    }

    if(paypalbtn.checked){
        Payments= `you are paying with PAYPAL !`
    }
    else if(visabtn.checked){
        Payments= `you are paying with VISA !`
    }
    else{
       Payments=`you MUST select one of the payment method !`
    }

    seeresult.textContent=results1 + " " + Payments;
}

let man="Jamal huseen"
//    man=man.trim();
  man=man.slice(0,man.indexOf("l")+3)

 console.log(man);

//  let UName=window.prompt("Enter your username : ");
 
//  UName=UName.trim();
//  let letter=UName.charAt(0);
//  letter=letter.toUpperCase();
 
//  let character=UName.slice(1);
//  character=character.toLowerCase();

//  UName=letter + character;
//  console.log(UName);

//  UName=UName.trim().charAt(0).toUpperCase() + UName.trim().slice(1).toLowerCase();
//  console.log(UName);

let loggedIn=false;
let username;
let password;

while(!loggedIn){
    username=window.prompt("Enter your username : ");
    password=window.prompt("Enter your password : ");
    if(username==="admin" && password==="password"){
        loggedIn=true;
        console.log("Login successful! Welcome, admin.");
    }
    else{
        console.log("Invalid username or password. Please try again.");

    }
}