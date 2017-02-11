var map = new Map();
var BootStrapDialog = function (params) {
    var obj = this;
    var guid = new GUID().newGUID().replace("-", "");
    obj.guid = guid;
    params = params == undefined ? params = {
        isMask: true,
        title: '提醒'
    } : params;
    var isMask = params.isMask == undefined ? true : params.isMask;
    var title = params.title == undefined ? "提醒" : params.title;
    obj.Sure = function () {
        if (typeof params.Sure == 'function') {
            if (params.Sure() == false)
                return;
        }
        obj.Close();
    }
    obj.Cancel = function () {
        if (typeof params.Cancel == 'function') {
            params.Cancel();
        }
        obj.Close();
    }
    obj.Complete = function () {
        if (typeof params.Complete == 'function') {
            params.Complete();
        }
    }
    obj.Layer = function (id) {
        obj.getMask();
        var modal = document.getElementById(id);
        if (!modal && map.get(id)) {
            modal = map.get(id);
        }
        else {
            if (modal) {
                map.set(id,modal);
                modal.remove();
            }
        }
        var div = document.createElement("div");
        div.id = "dv_" + guid;
        div.className = modal.className + " in"; //"modal fade in";
        div.style.width = modal.style.width;
        div.innerHTML = modal.innerHTML;
        var doc;
        if (window.top.document == undefined) {
            doc = document;
        }
        else {
            doc = window.top.document;
        }
        var body = doc.body;
        body.style.overflowY = "auto";
        body.style.height = "100%";
        body.style.minWidth = "900px";
        body.className = "modal-open";
        body.appendChild(div);
        var btn_sure = doc.getElementById(params.SureId);
        btn_sure.onclick = obj.Sure;
        var btn_cancel = doc.getElementById(params.CancelId);
        btn_cancel.onclick = obj.Cancel;
        obj.Complete();
        return obj;
    }
    obj.Alert = function (content) {
        content = content == undefined ? "" : content;
        obj.getMask();
        var div = document.createElement("div");
        div.id = "dv_" + guid;
        div.className = "modal small fade in";
        var html = "";
        html += "<div class=\"modal-header\">";
        html += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"false\"></button><h3>{0}</h3>".format(title);
        html += "</div>";
        html += "<div class=\"modal-body\">";
        html += "<i class=\"icon-warning-sign modal-icon\"></i><p>{0}</p>".format(content);
        html += "</div>";
        var doc;
        if (window.top.document == undefined) {
            doc = document;
        }
        else {
            doc = window.top.document;
        }
        var body = doc.body;
        div.innerHTML = html;
        body.style.overflowY = "auto";
        body.style.height = "100%";
        body.style.minWidth = "900px";
        body.className = "modal-open";
        body.appendChild(div);
        obj.Complete();
        setTimeout(function () {
            obj.Close();
            window.location.reload();
        }, 900);
        return obj;
    },
    obj.Confirm = function (content) {
        content = content == undefined ? "" : content;
        obj.getMask();
        var div = document.createElement("div");
        div.id = "dv_" + guid;
        div.className = "modal small fade in";
        var html = "";
        html += "<div class=\"modal-header\">";
        html += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"false\"></button><h3>{0}</h3>".format(title);
        html += "</div>";
        html += "<div class=\"modal-body\">";
        html += "<i class=\"icon-warning-sign modal-icon\"></i><p>{0}</p>".format(content);
        html += "</div>";
        html += "<div class=\"modal-footer\">";
        html += "<button id=\"btn_sure_" + guid + "\" class=\"btn btn-danger\" data-dismiss=\"modal\">确认</button>";
        html += "<button class=\"btn\" id=\"btn_cancel_" + guid + "\" data-dismiss=\"modal\" aria-hidden=\"true\">取消</button>";
        html += "</div>";
        div.innerHTML = html;
        var doc;
        if (window.top.document == undefined) {
            doc = document;
        }
        else {
            doc = window.top.document;
        }
        var body = doc.body;
        body.style.overflowY = "auto";
        body.style.height = "100%";
        body.style.minWidth = "900px";
        body.className = "modal-open";
        body.appendChild(div);
        var btn_sure = doc.getElementById("btn_sure_" + guid);
        btn_sure.onclick = obj.Sure;
        var btn_cancel = doc.getElementById("btn_cancel_" + guid);
        btn_cancel.onclick = obj.Cancel;
        obj.Complete();
        return obj;
    }

    obj.Close = function () {
        var doc;
        if (window.top.document == undefined) {
            doc = document;
        }
        else {
            doc = window.top.document;
        }
        var mask = doc.getElementById("mask_" + guid);
        doc.body.className = "";
        var div = doc.getElementById("dv_" + guid);
        if (div) { div.remove(); }
        if (mask) {
            mask.remove();
        }
    }

    obj.getMask = function () {
        if (isMask) {
            var div = document.createElement("div");
            div.className = "modal-backdrop fade in";
            div.id = "mask_" + guid;
            var body = window.top.document.body;
            if (body == undefined) {
                body = document.body;
            }
            body.appendChild(div);
            return div;
        }
        return document.body;
    }

    String.prototype.format = function (args) {
        var result = this;
        if (arguments.length > 0) {
            if (arguments.length == 1 && typeof (args) == "object") {
                for (var key in args) {
                    if (args[key] != undefined) {
                        var reg = new RegExp("({" + key + "})", "g");
                        result = result.replace(reg, args[key]);
                    }
                }
            }
            else {
                for (var i = 0; i < arguments.length; i++) {
                    if (arguments[i] != undefined) {
                        var reg = new RegExp("({)" + i + "(})", "g");
                        result = result.replace(reg, arguments[i]);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取字符串的哈希值
     * @param {String} str
     * @param {Boolean} caseSensitive
     * @return {Number} hashCode
     */
    function getHashCode(str, caseSensitive) {
        if (!caseSensitive) {
            str = str.toLowerCase();
        }
        // 1315423911=b'1001110011001111100011010100111'
        var hash = 1315423911, i, ch;
        for (i = str.length - 1; i >= 0; i--) {
            ch = str.charCodeAt(i);
            hash ^= ((hash << 5) + ch + (hash >> 2));
        }

        return (hash & 0x7FFFFFFF);
    }

    return obj;
}

function GUID() {
    this.date = new Date();
    /* 判断是否初始化过，如果初始化过以下代码，则以下代码将不再执行，实际中只执行一次 */
    if (typeof this.newGUID != 'function') {
        /* 生成GUID码 */
        GUID.prototype.newGUID = function () {
            this.date = new Date();
            var guidStr = '';
            sexadecimalDate = this.hexadecimal(this.getGUIDDate(), 16);
            sexadecimalTime = this.hexadecimal(this.getGUIDTime(), 16);
            for (var i = 0; i < 9; i++) {
                guidStr += Math.floor(Math.random() * 16).toString(16);
            }
            guidStr += sexadecimalDate;
            guidStr += sexadecimalTime;
            while (guidStr.length < 32) {
                guidStr += Math.floor(Math.random() * 16).toString(16);
            }
            return this.formatGUID(guidStr, 'N');
        }

        /*
         * 功能：获取当前日期的GUID格式，即8位数的日期：19700101
         * 返回值：返回GUID日期格式的字条串
         */
        GUID.prototype.getGUIDDate = function () {
            return this.date.getFullYear() + this.addZero(this.date.getMonth() + 1) + this.addZero(this.date.getDay());
        }

        /*
         * 功能：获取当前时间的GUID格式，即8位数的时间，包括毫秒，毫秒为2位数：12300933
         * 返回值：返回GUID日期格式的字条串
         */
        GUID.prototype.getGUIDTime = function () {
            return this.addZero(this.date.getHours()) + this.addZero(this.date.getMinutes()) + this.addZero(this.date.getSeconds()) + this.addZero(parseInt(this.date.getMilliseconds() / 10));
        }

        /*
        * 功能: 为一位数的正整数前面添加0，如果是可以转成非NaN数字的字符串也可以实现
         * 参数: 参数表示准备再前面添加0的数字或可以转换成数字的字符串
         * 返回值: 如果符合条件，返回添加0后的字条串类型，否则返回自身的字符串
         */
        GUID.prototype.addZero = function (num) {
            if (Number(num).toString() != 'NaN' && num >= 0 && num < 10) {
                return '0' + Math.floor(num);
            } else {
                return num.toString();
            }
        }

        /* 
         * 功能：将y进制的数值，转换为x进制的数值
         * 参数：第1个参数表示欲转换的数值；第2个参数表示欲转换的进制；第3个参数可选，表示当前的进制数，如不写则为10
         * 返回值：返回转换后的字符串
         */
        GUID.prototype.hexadecimal = function (num, x, y) {
            if (y != undefined) {
                return parseInt(num.toString(), y).toString(x);
            } else {
                return parseInt(num.toString()).toString(x);
            }
        }

        /*
         * 功能：格式化32位的字符串为GUID模式的字符串
         * 参数：第1个参数表示32位的字符串
         * 返回值：标准GUID格式的字符串
         */
        GUID.prototype.formatGUID = function (guidStr, format) {
            switch (format) {
                case "N":
                    var str1 = guidStr.slice(0, 8),
                    str2 = guidStr.slice(8, 12),
                    str3 = guidStr.slice(12, 16),
                    str4 = guidStr.slice(16, 20),
                    str5 = guidStr.slice(20);
                    return str1 + str2 + str3 + str4 + str5;
                    break;
                default:
                    var str1 = guidStr.slice(0, 8) + '-',
                    str2 = guidStr.slice(8, 12) + '-',
                    str3 = guidStr.slice(12, 16) + '-',
                    str4 = guidStr.slice(16, 20) + '-',
                    str5 = guidStr.slice(20);
                    return str1 + str2 + str3 + str4 + str5;
                    break;
            }

        }
    }
}

function Map() {
    this.keys = new Array();
    this.data = new Array();
    //添加键值对
    this.set = function (key, value) {
        if (this.data[key] == null) {//如键不存在则身【键】数组添加键名
            this.keys.push(value);
        }
        this.data[key] = value;//给键赋值
    };
    //获取键对应的值
    this.get = function (key) {
        return this.data[key];
    };
   
    //去除键值，(去除键数据中的键名及对应的值)
    this.remove = function (key) {
        this.keys.remove(key);
        this.data[key] = null;
    };
    //判断键值元素是否为空
    this.isEmpty = function () {
        return this.keys.length == 0;
    };
    //获取键值元素大小
    this.size = function () {
        return this.keys.length;
    };
}
