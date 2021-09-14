var dj = {};
dj.os = {};
dj.os.isIOS = /ios|iPhone|iPad|iPod/i.test(navigator.userAgent);
dj.os.isAndroid = !dj.os.isIOS;
dj.callbackname = function(){
    return "djapi_callback_"+(new Date()).getTime()+"_"+Math.floor(Math.random()*10000);
};
dj.callbacks = {};
dj.addCallback = function(name,func,userdata){
    delete dj.callbacks[name];
    dj.callbacks[name] = {callback:func,userdata:userdata};
};
dj.callback = function(para){
    var callbackobject = dj.callbacks[para.callbackname];
    if(callbackobject !== undefined){
        if(callbackobject.userdata !== undefined){
            callbackobject.userdata.callbackData = para;
        }
        if(callbackobject.callback != undefined){
            var ret = callbackobject.callback(para,callbackobject.userdata);
            if(ret === false){
              return;
            }
            delete dj.callbacks[para.callbackname];
        }
    }
};

dj.post =function(cmd,para){
    window.webview.post(cmd,JSON.stringify(para));
}

dj.postWithCallback = function(cmd,para,callback,ud){
    var callbackname = dj.callbackname();
    dj.addCallback(callbackname,callback,ud);
    para.callback = callbackname;
    console.log(JSON.stringify(para))
    window.webview.post(cmd,JSON.stringify(para));
}
dj.dispatchEvent = function(para){
  if(!para){
       para = {"name":"webviewLoadComplete"};
   }
   var evt = {};
   try{
    evt = new Event(para.name);
    evt.para = para.para;
   }catch(e){
    evt = document.createEvent("HTMLEvents");
    evt.initEvent(para.name,false,false);
   }
   window.dispatchEvent(evt);
}
dj.toast = function(msg){
   dj.post("showToast",{message:msg});
}
dj.alert = function(para){
  var title = para.title || ""
  var content = para.content || ""
  dj.postWithCallback("showDialog",{title:title,content:content,buttons:[{title:"知道了"}]},function(paras){
  })
};
window.dj = dj;
console.log("dj load success");