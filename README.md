# wake-up-android
2016/08/23
语音唤醒
##界面
MainActivity.java完成欢迎界面
唤醒时有信息框和通知栏
##后台
MyService.java完成后台调度
* 多线程完成录音/特征提取/神经网络前向的同步
* 队列结构实现数据调度同步
##JNI
JNI-NDK实现特征提取代码
同wake-up-pc
JNI-NDK实现神经网络前向
