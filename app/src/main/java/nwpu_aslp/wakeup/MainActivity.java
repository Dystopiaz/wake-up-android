package  nwpu_aslp.wakeup;
/**
 * Created by houjingyong on 2016/7/24.
 * Created by houjingyong on 2016/7/24.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
    Button record, play, stopservice;
    //创建语音数据队列
    final public BlockingQueue<short[]> queue = new LinkedBlockingQueue<short[]>(200);
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("语音唤醒演示系统");

        final Intent intent = new Intent(this, MyService.class);
        startService(intent);
        Toast.makeText(MainActivity.this, "hi!", Toast.LENGTH_SHORT).show();




    }

}