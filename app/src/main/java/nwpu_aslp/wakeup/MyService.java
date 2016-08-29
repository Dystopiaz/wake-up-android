package nwpu_aslp.wakeup;
/**
 * Created by houjingyong on 2016/7/24.
 * Created by houjingyong on 2016/7/24.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MyService extends Service {
    //process buffer
    final private BlockingQueue<short[]> audio_queue = new ArrayBlockingQueue<short[]>(10,true);
    final private BlockingQueue<float[]> fbank_queue = new ArrayBlockingQueue<float[]>(1000,true);
    //final public BlockingQueue<Integer> result_queue = new ArrayBlockingQueue<Integer>(10,true);

    //wakeup counter
    private int wakeup_num=0;
    //state parameter
    private boolean wakeuptext = false;
    //private boolean stopThread = false;
    private boolean stoprecord = false;
    private boolean stopfbank = false;
    private boolean stopdnn= false;

    //util objects
    final private NdkJniUtils jni_util = new NdkJniUtils();
    final private Nnet nnet = new Nnet();
    final private PostProcessor post_processor = new PostProcessor();

    //useful constant
    final private int INPUT_DIMEN=1640;
    final private int NUMCHANS=40;

    /**
     * this method is used to record pcm audios and push the data in the audio_queue
     */
    private void record() {
        int frequency = 16000;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        try {
            // Create a new AudioRecord object to record the audio.
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            int buffernumber = 10;
            short[] buffer = new short[bufferSize];
            short[] sumbuffer = new short[bufferSize*buffernumber];
            int offset;
            audioRecord.startRecording();
            while(!stoprecord) {
                offset = 0;
                for (int i = 0; i < buffernumber; i++) {
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    System.arraycopy(buffer, 0, sumbuffer, offset, bufferSize);
                    offset += bufferSize;
                }
                audio_queue.put(sumbuffer);
            }
        } catch (Throwable t) {
            Log.v("录音失败"+t.toString());
            System.out.println("录音失败"+t.toString());
        }
    }

    private void calculateFbank() {
        try {
            int tracknum = 0;
            short[] music;
            int musicLength;
            float[] fbank;
            float[] fbank_buf;
            while (!stopfbank) {

                //System.out.print(audio_queue.size()+"calculateFbank:audio_queue.size\n");
                music = audio_queue.take();
                //System.out.println("calculateFbank "+music[0]+"size"+audio_queue.size());
                musicLength = music.length;
                // System.out.print(musicLength+" musicLength\n"+audio_queue.size()+"audio_queue.size\n");
                fbank = this.jni_util.pcm2fbank(music,musicLength);
                //System.out.println();
                for(int offset = 0;offset<fbank.length;)
                {
                    fbank_buf = new float[NUMCHANS];
                    System.arraycopy(fbank,offset,fbank_buf,0,NUMCHANS);
                    if(fbank_queue.size()==0) {
                        for(int i=0;i<30;i++) {
                            fbank_queue.put(fbank_buf);
                            //System.out.println("push "+fbank_buf[0]+"size"+fbank_queue.size());
                        }
                    }
                    fbank_queue.put(fbank_buf);
                    //System.out.println("push "+fbank_buf[0]+"size"+fbank_queue.size());
                    offset += NUMCHANS;
                }
            }
        } catch (Throwable t) {
            Log.v("特征计算失败"+t.toString());
            System.out.println("特征计算失败"+t.toString());
        }
    }

    private void dnn(){
        try {
            while(fbank_queue.size()<50) {
            }
            float[] DNNInMat = new float[1640];
            float[] DNN_buf = new float[1640];
            int k = 0;
            float[] bufff;
            for(int offset=0;offset<DNNInMat.length;) {
                bufff = fbank_queue.take();
                System.arraycopy(bufff, 0, DNNInMat, offset, NUMCHANS);
                offset += NUMCHANS;
                //System.out.println("take "+bufff[0]+"size"+fbank_queue.size());
            }
            int interval=5;
            float[] DNN_result;
            while(!stopdnn){
                if(fbank_queue.size()>1) {
                    if(interval==0) {
                        DNN_result = this.jni_util.getdnn(DNNInMat);
                        //TODO("early dnn")
                        //float[] DNN_result2 = todnn2.calculate_dnn(DNNInMat);
                        //System.out.print(DNN_result[0]);//System.out.print(DNN_result2[0]);
                        //System.out.print(DNN_result[1]);//System.out.print(DNN_result2[1]);
                        //System.out.print(DNN_result[2]);//System.out.print(DNN_result2[2]);
                        if (DNN_result[0] > (DNN_result[1] + DNN_result[2])) {
                            System.out.println("hello" + fbank_queue.size());
                        } else if (DNN_result[1] > (DNN_result[2] + DNN_result[0]))
                            System.out.println("小瓜" + fbank_queue.size());
                        else {
                            System.out.println("NULL" + fbank_queue.size());
                        }
                        if (post_processor.isWakeup(DNN_result)) {
                            //System.out.print("huanxing1");
                            //wakeup_num+=1;
                            //wakeuptext = true;
                            new ToastMessageTask().execute("唤醒成功！");
                            NotificationManager noteManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                            //API Level >= 4 (Android 1.6) && API Level < 16 (Android 4.1)
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this) ;
                            builder.setSmallIcon(R.drawable.icon);
                            builder.setTicker("this is ticker");
                            builder.setContentTitle("This is content title");
                            builder.setContentText("This is content text");
                            builder.setAutoCancel(true);
                            builder.setDefaults(Notification.DEFAULT_ALL);

                            builder.setWhen(System.currentTimeMillis());
                            Notification note =builder.getNotification(); //调用builder.getNotification()来生成Notification

                            Intent intent2 = new Intent(this, MainActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
                            // API Level >= 11 (Android 3.0)
                            builder.setContentIntent(pi);

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(0, builder.build());
                        }
                        interval = 5;
                    }
                    interval-=1;
                    System.arraycopy(DNNInMat,0,DNN_buf,0,INPUT_DIMEN);
                    System.arraycopy(DNN_buf,NUMCHANS,DNNInMat,0,INPUT_DIMEN-NUMCHANS);

                    bufff = fbank_queue.take();
                    //System.out.println("take "+bufff[0]+"size"+fbank_queue.size());
                    System.arraycopy(bufff,0,DNNInMat,INPUT_DIMEN-NUMCHANS,NUMCHANS);
                    /*for(int i=0;i<DNNInMat.length;i+=NUMCHANS)
                    {
                        System.out.print(DNNInMat[i]+" ");
                    }
                    System.out.println();*/
                }
            }
        } catch (Throwable t) {
            Log.v("DNN失败"+t.toString());
            System.out.println("DNN失败"+t.toString());
        }
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
    // A class that will run Toast messages in the main GUI context
    private class ToastMessageTask extends AsyncTask<String, String, String> {
        String toastMessage;

        @Override
        protected String doInBackground(String... params) {
            toastMessage = params[0];
            return toastMessage;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
        // This is executed in the context of the main GUI thread
        protected void onPostExecute(String result){
            Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

// to use:

    @Override
    public void onCreate()
    {
        //比较耗时的必要的初始操作在线程开始之前都完成，不要在线程中做过多的申请内存的操作
        //read parameter from parameter file
        this.nnet.initNnet(this);
        //pass the parameter to jni dnn
        this.jni_util.initDnn(nnet.w0, nnet.w1, nnet.w2, nnet.w3, nnet.b0, nnet.b1, nnet.b2, nnet.b3);
        new ToastMessageTask().execute("初始化完成");
/*
        new Thread(){
            public void run(){
                while(!stoprecord) {
                    if (wakeuptext) {
                        new ToastMessageTask().execute("hello 小瓜！唤醒: " + String.valueOf(wakeup_num) + " 次!");
                        wakeuptext = false;
                    }
                }
            }
        }.start();
*/
        new Thread() {
            public void run() {
                while (!stoprecord) {
                    record();
                }
            }
        }.start();

        new Thread()
        {
            public void run() {
                while (!stopfbank) {
                    calculateFbank();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                while (!stopdnn) {
                    dnn();
                }
            }
        }.start();
    }
    @Override
    public void onDestroy()
    {
        stoprecord = true;
        stopfbank = true;
        stopdnn = true;
        super.onDestroy();
    }

}


