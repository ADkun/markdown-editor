
// 最后光标对象
let lastEditRange;
let $editor = $('.editor');
let serverPath = 'http://127.0.0.1:8080';

$(function() {
    // 编辑器点击事件
    $editor.click(function (e) { 
        // 获取选定对象
        let selection = getSelection()
        // 设置最后光标对象
        lastEditRange = selection.getRangeAt(0);
    });

    // 编辑器按键事件
    $editor.keyup(function (e) { 
        let selection = getSelection();
        lastEditRange = selection.getRangeAt(0);
    });

    // 编辑器内容改变事件
    $editor.on('input propertychange', sendMd);
});

/**
 * 将编辑器的内容发送到服务器
 */
function sendMd() {
    let md = parseMd($editor.html());
    let abc = 0;
    $.ajax({
        type: "get",
        url: serverPath + '/parse',
        data: {"md": md},
        xhrFields: {withCredentials: true},
        success: function (response) {
            if (response.status == '0') {
                // 成功
                $('.parsed').html(response.data);
            } else {
            }
        }
    });
}

function parseMd(html) {
    let res = html;
    res = res.replace(/\<div\>\<br\>\<\/div\>/g, "\n");
    res = html.replace(/\<div\>/g, "\n");
    res = res.replace(/\<\/div\>/g, "");
    res = res.replace(/\&nbsp;/g, " ");
    return res;
}
