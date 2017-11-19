function orders_search_response() {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
      if (httpRequest.status === 200) {
        alert(httpRequest.responseText);
        orders_search_response_table(httpRequest.responseText);
      } else {
        alert('There was a problem with the request.');
      }
    }
  }
  function orders_search() {
	httpRequest = new XMLHttpRequest();
	    if (!httpRequest) {
	      alert('Giving up :( Cannot create an XMLHTTP instance');
	      return false;
	    }
	    httpRequest.onreadystatechange = orders_search_response;
	    var goodsid=document.getElementById("goodsid").value;
	    var suppliername=document.getElementById("suppliername").value;
	    var par="";
	    if(goodsid.length>0){
	    	par=par+"&goodsid="+goodsid;
	    }
	    if(suppliername.length>0){
	    	par=par+"&suppliername="+suppliername;
	    }
	    if(par.length>0){
	    	par = "?"+par.substr(1,par.length);
	    	alert("in if"+par);
	    }
	    var request="http://10.21.0.187:8081/orders/search"+par;
	    alert(request);
	    httpRequest.open('GET', request,true);
	    //httpRequest.open('GET', 'http://10.21.0.187:8081/orders/search',true);
	    httpRequest.send();
	  }
  //orders_search();
function show_orders_form(){
	var form="<form id='display_order_search_form'>GoodsId<br><input type='text' name='goodsid' id='goodsid'>SupplierName<input type='text' name='suppliername' id='suppliername'><label onclick='orders_search()'>SUBMIT</label></form>";
	document.getElementById("mid").innerHTML = form;
}
function orders_search_response_table(json_string){
	var json=JSON.parse(json_string);
	//alert(Object.keys(json).length);
	var table="<table id=functionalities><tr>";
	var keys = ['id','GoodsId','Quantity','Supplier_Name','PriceperUnit','PurchaseDate'];
	for(var i=0;i<keys.length;i++){
		table+="<th>"+keys[i]+"</th>";
	}
	table+="</tr>";
	for (var i = 0; i < json.length; i++) {
		var rows="";
		for(var key=0;key<keys.length;key++){
			rows+="<td>"+json[i][keys[key]]+"</td>";
		}
		table+="<tr>"+rows+"</tr>";
	}
	table+="</table>";
	table+="<div id='orders_update_button'><button onclick='shift_show_update_form()'>UPDATE</button><div>";
	alert(table);
	document.getElementById("mid-right").innerHTML=table;
}
function shift_show_update_form(){
	document.getElementById("mid-right").style.float="left";
	document.getElementById("mid").style.float="right";
	show_final_update_form();
}
function orders_update_post() {
	
	var updateRequest = new XMLHttpRequest();
    if (!updateRequest) {
      alert('Giving up :( Cannot create an XMLHTTP instance');
      return false;
    }
    updateRequest.onreadystatechange = orders_update_response;
    //geting elements from form
    var form_elements=document.getElementById("final_update_form");
    var param="";
    var parm_name=["id","goodsid","quantity","suppliername","priceperunit","purchasedate"];
     for(var i=0;i<6;i++){
      	if(form_elements[i].value.length>0){
      		param+="&"+parm_name[i]+"="+form_elements[i].value;
      	}
      	//alert(form_elements[i].value);
      }
      if(param.length>0){
      	param=param.substring(1,param.length);
      }
      alert(param);
	  var request="http://10.21.0.187:8081/orders/update";
	   
	  updateRequest.open('POST', request,true);
	  updateRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	    // //httpRequest.open('GET', 'http://10.21.0.187:8081/orders/search',true);
	    updateRequest.send(param);
	  }


function show_final_update_form(){
	table="<form id='final_update_form'>ID<br><input type='text' name='id'>GoodsId<input type='text' name='goodsid'>Quantity<input type='text' name='quantity'>Supplier_Name<input type='text' name='suppliername'>PriceperUnit<input type='text' name='priceperunit'>PurchaseDate<input type='text' name='purchasedate'><label onclick='orders_update_post()'>SUBMIT</label></form>";
	document.getElementById("mid").innerHTML=table;
}
function orders_update_response() {
    if (updateRequest.readyState === XMLHttpRequest.DONE) {
      if (updateRequest.status === 200) {
        alert(updateRequest.responseText);
        //orders_search_response_table(updateRequest.responseText);
      } else {
        alert('There was a problem with the request.');
      }
    }
  }