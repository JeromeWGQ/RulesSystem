<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en-US" encoding="utf-8">

<head>

  <meta http-equiv="Content-Type" content="text/html;charset=utf-8">

  <title>Modal Login Window Demo</title>
    <link rel="stylesheet" type="text/css" href="css/Huploadify.css"/>
  <link rel="stylesheet" type="text/css" media="all" href="css/style_changetxt.css">
  <link rel="stylesheet" type="text/css" media="all" href="css/bootstrap.min.css">

  <script type="text/javascript" src="js/jquery-2.2.3.min.js"></script>

  <script type="text/javascript" charset="utf-8" src="js/jquery.leanModal.min.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/bootstrap.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/bootstrap.dialog.js"></script>
    <script type="text/javascript" src="js/jquery.Huploadify.js"></script>
    <!--添加版本-->
    <script type="text/javascript">
        var blockNum=10;
        $(document).ready(function(){
            var parentDom=$('#father').find("form"),oriDom=parentDom.children(":first");
            $('#btnAdd').click(function(){

                var clLength=parentDom.children().length;
                if(blockNum>clLength){
                    var nowDom=oriDom.clone();
                    nowDom.children(":first").text('版本'+(clLength+1));
                    parentDom.append(nowDom);
                }
                else return false;
            });
            $('.btnSub').click(function(){
                $('.conform').submit();
            });
        });
    </script>
    <!--上传文件-->
    <script type="text/javascript">
        $(function(){
            var up = $('#upload').Huploadify({
                auto:false,
                fileTypeExts:'*.*',
                multi:true,
                formData:{key:123456,key2:'vvvv'},
                fileSizeLimit:99999999999,
                showUploadedPercent:true,
                showUploadedSize:true,
                removeTimeout:9999999,
                uploader:'upload.php',
                onUploadStart:function(file){
                    console.log(file.name+'开始上传');
                },
                onInit:function(obj){
                    console.log('初始化');
                    console.log(obj);
                },
                onUploadComplete:function(file){
                    console.log(file.name+'上传完成');
                },
                onCancel:function(file){
                    console.log(file.name+'删除成功');
                },
                onClearQueue:function(queueItemCount){
                    console.log('有'+queueItemCount+'个文件被删除了');
                },
                onDestroy:function(){
                    console.log('destroyed!');
                },
                onSelect:function(file){
                    console.log(file.name+'加入上传队列');
                },
                onQueueComplete:function(queueData){
                    console.log('队列中的文件全部上传完成',queueData);
                }
            });

            $('#btn2').click(function(){
                up.upload('*');
            });
            $('#btn3').click(function(){
                up.cancel('*');
            });
            $('#btn4').click(function(){
                //up.disable();
                up.Huploadify('disable');
            });
            $('#btn5').click(function(){
                up.ennable();
            });
            $('#btn6').click(function(){
                up.destroy();
            });
        });
    </script>
    <!--规章废止-->
    <script type="text/javascript">
      var dialog;
      function Add() {
          OpenDialog(function (doc) {
              dialog.Close();
          });
      }

      function OpenDialog(callback, completeCallBack) {
          dialog = BootStrapDialog({
              SureId: 'btn_role_submit',
              CancelId: 'btn_role_cancel',
              Sure: function () {
                  var doc;
                  if (window.top.document == undefined) {
                      doc = document;
                  }
                  else {
                      doc = window.top.document;
                  }
                  return callback(doc);
              },
              Complete: function () {
                  if (typeof completeCallBack == 'function') {
                      var doc;
                      if (window.top.document == undefined) {
                          doc = document;
                      }
                      else {
                          doc = window.top.document;
                      }
                      completeCallBack(doc);
                  }
              }
          }).Layer("score_modal");
      }
      function Z_Alert() {
          dialog = BootStrapDialog({
              title: '提示框'
          }).Alert("这是个提示！");
      }

      function Confirm() {
          dialog = BootStrapDialog({
              title: "确认框",
              Sure: function () {
                  console.log("确认");
              },
              Cancel: function () {
                  console.log("取消");
              }
          }).Confirm("确认要废止该文件吗？");
      }
  </script>

</head>
<body>
      <a href="#loginmodal" class="flatbtn" id="modaltrigger">规章修订</a>
      <button class="flatbtn" id="cancelfile" onclick="Confirm()">规章废止</button>
      <div id="loginmodal" style="display:none;">
    <h1>规章版本修订</h1>
      <button id="btnAdd"   class="btn btn-primary" >添加新版本</button>
      <div id="father" style="position:relative;height:500px; overflow:auto">
        <form action="" method="post" name="conform" class="conform" >
          <fieldset >
            <!--<label class="btn btn-info">-->
              <!--<input type="radio" name="options" id="option1">-->
            <!--</label> -->
            <legend for="version">版本1</legend>
              <div class="input-block-level" >
              <span class="input-group-addon">规章文件修订备注：</span>
              <input type="text" class="input-xxlarge" placeholder="改动摘要" style="width:60%;">
                  <span class="input-group-btn">
                    <button type='button' class='btn btn-default'  onclick = "$(this).parent().parent().parent().remove();"/>删除该版本</button>
                    <button type='button' class='btn btn-default'  onclick = ""/>使用该版本</button>
                    </span>
              </div>
              <div id="upload"> 上传规章文件：</div>
          </fieldset>
        </form>
      </div>
        <button id="btn_role_submit" class="btn btn-danger" data-dismiss="modal">确认</button>
        <button id="btn_role_cancel" class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
  </div>
<script type="text/javascript">
$(function(){
  $('#loginform').submit(function(e){
    return false;
  });
  $('#modaltrigger').leanModal({ top: 30, overlay: 0.45, closeButton: ".hidemodal" });
});

</script>





</body>

</html>