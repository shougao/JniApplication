# JniApplication
android studio jni demo。

算法so库 ，动态更新的方案：
利用system loadlibrary加载时加载新的库 。
过程是，so下载好，copy到Android规定好的目录，
当app进程重新启动时生效。
我们在下载好后提示用户需要重启app，用户点确定我们就kill掉process。再启动app自然就是新so
