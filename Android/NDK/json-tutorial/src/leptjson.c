/**
 * leptjson 的实现文件（implementation file），含有内部的类型声明和函数实现。此文件会编译成库。
 */

/**
JSON 语法子集
    JSON-text = ws value ws             JSON 文本由 3 部分组成，首先是空白（whitespace），接着是一个值，最后是空白。
    ws = *(%x20 / %x09 / %x0A / %x0D)   所谓空白，是由零或多个空格符（space U+0020）、制表符（tab U+0009）、换行符（LF U+000A）、回车符（CR U+000D）所组成。
    value = null / false / true         我们现时的值只可以是 null、false 或 true，它们分别有对应的字面值（literal）
    null  = "null"                      
    false = "false"                     
    true  = "true"                      
*/

