var currentDir = 0;
//===========================================
//    自己定义的一些js函数
//===========================================

/**
 * 页面中的业务逻辑表单提交等，待修改
 */
$(function () {
    $('#login').keydown(function (event) {
        if (event.keyCode == 13) {
            $('#submit').click();
        }
    });
});

//======================================================
//    登录注册相关函数
//======================================================

// 提交登录信息
function login_submit() {
    var username = $('#username').val();
    var password = $.md5($('#password').val());//对密码进行MD5加密
    $.post('login.do', 'username=' + username + "&password=" + password,
        function (data) {
            if (data == 1) {
                self.location = "filelist.jsp";
            } else {
                UIkit.notify("用户名或密码错误！", {timeout: 800});
                $('#password').val('');
            }
        });
}

// 提交修改密码的信息
function chpwd_submit() {
    var username = $('#username').val();
    var oldpwd = $.md5($('#oldpwd').val());//对密码进行MD5加密
    var newpwd_1 = $('#newpwd_1').val();
    var newpwd_2 = $('#newpwd_2').val();
    if (newpwd_1.length < 5 || newpwd_2.length < 5) {
        UIkit.notify("密码必须大于5位！", {timeout: 800});
        return;
    }
    if (newpwd_1 == newpwd_2) {
        $.post('login.do', 'username=' + username + "&password=" + oldpwd,
            function (data) {
                if (data == 1) {
                    var password = $.md5($('#newpwd_1').val());
                    $.post('modifypwd.do', 'username=' + username + "&password=" + password,
                        function (data) {
                            if (data == 1) {
                                UIkit.notify("修改成功！即将返回登录页面。", {timeout: 800});
                                setTimeout(function () {
                                    self.location = "login.jsp"
                                }, 1000);
                            } else {
                                UIkit.notify("系统忙，请稍后再试！", {timeout: 800});
                            }
                        });
                } else {
                    UIkit.notify("初始用户名或密码错误！", {timeout: 800});
                    $('#password').val('');
                }
            });
    } else {
        UIkit.notify("两次密码不一致！", {timeout: 800});
    }
}

//提交注册信息
function regist_submit() {
    var username = $('#username').val();
    var pwd_1 = $('#pwd_1').val();
    var pwd_2 = $('#pwd_2').val();
    if (pwd_1.length < 5 || pwd_2.length < 5 || username.length < 5) {
        UIkit.notify("用户名或密码必须大于5位！", {timeout: 800});
        return;
    }
    if (pwd_1 == pwd_2) {
        $.post('hasname.do', 'username=' + username,
            function (data) {
                if (data == 1) {
                    var password = $.md5($('#pwd_1').val());
                    $.post('adduser.do', 'username=' + username + "&password=" + password,
                        function (data) {
                            if (data == 1) {
                                UIkit.notify("注册成功！即将返回登录页面。", {timeout: 800});
                                setTimeout(function () {
                                    self.location = "login.jsp"
                                }, 1000);
                            } else {
                                UIkit.notify("系统忙，请稍后再试！", {timeout: 800});
                            }
                        });
                } else {
                    UIkit.notify("用户名已经存在！", {timeout: 800});
                }
            });
    } else {
        UIkit.notify("两次密码不一致！", {timeout: 800});
    }
}

//======================================================
//    以上是登录注册相关函数
//======================================================


//上传文件
var IntervalID;
function do_upload() {
    if ($('#file').val() != "") {
        $('#upload').submit();
        $('#updiv').addClass('uk-hidden');
        $('#progress').removeClass('uk-hidden');
        IntervalID = setInterval(function () {
            do_getprogress();
        }, 500);
    } else {
        UIkit.notify("请选择一个文件！", {timeout: 800});
    }
}

//获取上传进度
function do_getprogress() {
    $.post('uploadstate.do', function (data) {
        if (data == 200) {
            clearInterval(IntervalID);
            $('#updiv').removeClass('uk-hidden');
            $('#progress').addClass('uk-hidden');
            $('#progressbar').css('width', 0);
            UIkit.notify("文件已经存在！请更改文件名。", {timeout: 800});
            setTimeout(function () {
                do_showall(currentDir);
            }, 100);
            return;
        }
        if (data == 300) {
            clearInterval(IntervalID);
            $('#updiv').removeClass('uk-hidden');
            $('#progress').addClass('uk-hidden');
            $('#progressbar').css('width', 0);
            UIkit.notify("上传成功！", {timeout: 800});
            setTimeout(function () {
                do_showall(currentDir);
            }, 100);
            return;
        }
        if (data == 400) {
            clearInterval(IntervalID);
            $('#updiv').removeClass('uk-hidden');
            $('#progress').addClass('uk-hidden');
            $('#progressbar').css('width', 0);
            UIkit.notify("系统忙，请稍后再试！", {timeout: 800});
            setTimeout(function () {
                do_showall(currentDir);
            }, 100);
            return;
        }
        if (data < 100) {
            var curwidth = parseInt(data) * ($('#progress').width() / 100);
            $('#progressbar').css('width', curwidth);
        }
    });
}
// 搜索
//============================================
//    以下为新增代码
//============================================

//根据权限隐藏上传功能
//显示全部文件
function do_search() {
    $('#filetable').load('showfile.do', 'searchcode=' + $('#searchcode').val());
    showview();
}


function showview() {
    $.post('showview.do', function (data) {
        if (data == 0) {
            $('#uploadview').addClass('uk-hidden');
        }
    });
}

function do_showall(id) {
    // type = 0 代表显示子目录
    // type = 1 代表显示文件
    $('#filetable').load('showfile.do', {searchcode: 0, type: 1, directory: id});
    $('#sorttable').load('showfile.do', {searchcode: 0, type: 0, directory: id});
    // $('#filetable_small').load('showfilesmall.do');
    showview();
}

function do_my_click(id, type) {
    UIkit.notify("id是" + id + "，类型为" + type, {timeout: 800});
    if (type == '0') {
        do_showall(id);
        $('#my-navi').load('updatenav.do', {newid: id});
        currentDir = id;
    }
}

function new_sort_dialog() {
    UIkit.notify("添加新分类", {timeout: 800});
    $('#new_sort_position').html('<form id="new_sort">' +
        '<input id="sortname" name="sortname" placeholder="新分类名称"/>' +
        '<a onclick="do_new_sort();" id="sort_submit">确定</a>' +
        '<a onclick="cancel_new_sort();" id="sort_submit">取消</a>' +
        '</form>');
}

function do_new_sort() {
    var name = $('#sortname').val();
    UIkit.notify("确定添加：" + currentDir + "，" + name, {timeout: 800});
    $.post('newsort.do', {sortname: name, dir: currentDir}, function (code) {
        if (code == 1) {
            UIkit.notify("添加成功！", {timeout: 800});
            setTimeout(function () {
                $('#sorttable').load('showfile.do', {searchcode: 0, type: 0, directory: currentDir});
                showview();
            }, 100);
            return;
        }
        if (code == 0) {
            UIkit.notify("系统忙请稍后再试！", {timeout: 800});
            setTimeout(function () {
                $('#sorttable').load('showfile.do', {searchcode: 0, type: 0, directory: currentDir});
                showview();
            }, 100);
            return;
        }
    })
}

function cancel_new_sort() {
    $('#sorttable').load('showfile.do', {searchcode: 0, type: 0, directory: currentDir});
    showview();
}

//删除文件或目录
function do_delete(filename, fileid) {
    if (confirm("确定要删除 " + filename + " 吗？")) {
        $.post('clearfile.do', {fileid: fileid}, function (data) {
            if (data == 1) {
                UIkit.notify("删除成功！", {timeout: 800});
                setTimeout(function () {
                    do_showall(currentDir);
                }, 100);
                return;
            }
            if (data == 0) {
                UIkit.notify("系统忙请稍后再试！", {timeout: 800});
                setTimeout(function () {
                    do_showall(currentDir);
                }, 100);
                return;
            }
        });
    }
}

function do_mofidy() {

}
