# ycbSocketServer
基于netty硬件字符流通信

#maven 打jar包问题注意：
遇到问题1 ：javax.net.ssl.SSLException: java.lang.RuntimeException: Could not generate DH keypair
    搜索提示是jdk 安全问题，但是别的系统运行Http get 是没错的。说明不是jdk的问题
更换打包方式，将依赖打进jar包
遇到问题2 ： Configuration problem: Unable to locate Spring NamespaceHandler for XML schema namespace 
最终参照： http://blog.csdn.net/xiao__gui/article/details/47341385 最后一个方法解决