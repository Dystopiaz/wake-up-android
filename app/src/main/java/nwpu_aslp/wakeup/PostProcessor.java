package  nwpu_aslp.wakeup;
/**
 * Created by houjingyong on 2016/7/24.
 * Created by houjingyong on 2016/7/24.
 */
import android.util.Log;
public class PostProcessor {

    //variable used by state machine
    public enum WakeUpState {
        filler, filler_wakeupword, wakeupword;
    }
    private WakeUpState current_state=WakeUpState.filler;
    private int filler_num=0;
    private int wakeupword_num=0;
    final private int FILLER_HOLD=5 ;
    final private int WAKEUPWORD_HOLD = 5;

    //constant variable
    final private float THRESHOLD = new Float(0.2);
    final private int OUTPUT_LEN = 3;
    final private int SMOOTH_W = 6;
    final private int CONFIDENCE_W = 20;

    //those variable used to cache result and count final score
    //smoothing used variable
    private CyclicQueue [] dnn_result = new CyclicQueue[OUTPUT_LEN-1];

    //scoring used varivable
    private CyclicQueue [] smooth_result = new CyclicQueue[OUTPUT_LEN-1];

    private float[] final_score = new float[100];

    public PostProcessor(){
        int i=0;
        for (i=0; i<this.OUTPUT_LEN-1; i++) {
            dnn_result[i]=new CyclicQueue(SMOOTH_W);
        }
        for (i=0; i<this.OUTPUT_LEN-1; i++) {
            smooth_result[i]=new CyclicQueue(CONFIDENCE_W);
        }

    }

    private void stateMachine(boolean beyond_thtreshold) {
        switch(this.current_state){
            case filler :
                if(beyond_thtreshold) {
                    wakeupword_num+=1;
                    filler_num=0;
                } else {
                    filler_num+=1;
                    wakeupword_num=0;
                }
                if (wakeupword_num > FILLER_HOLD) {
                    this.current_state=WakeUpState.filler_wakeupword;
                }
                break;
            case filler_wakeupword:
                current_state=WakeUpState.wakeupword;
                break;
            case wakeupword:
                if(beyond_thtreshold) {
                    wakeupword_num+=1;
                    filler_num=0;
                } else {
                    filler_num+=1;
                    wakeupword_num=0;
                }
                if (filler_num > WAKEUPWORD_HOLD) {
                    this.current_state=WakeUpState.filler;
                }
                break;
            default :
        }
    }

    private float []smooth(float DNN_result[]) {
        float [] smooth_score=new float [OUTPUT_LEN-1];
        for(int i=0;i< this.OUTPUT_LEN-1; i++){
            this.dnn_result[i].push(DNN_result[i]);
            smooth_score[i]=dnn_result[i].get_mean_data();
        }
        return smooth_score;
    }

    private float confidence(float smooth_score[]) {
        float final_score=1;
        for(int i=0;i< this.OUTPUT_LEN-1; i++){
            this.smooth_result[i].push(smooth_score[i]);
            final_score *= smooth_result[i].get_max_data();
        }
        final_score = (float)Math.pow((double)final_score, 1.0/(this.OUTPUT_LEN-1));
        return final_score;
    }

    public boolean isWakeup(float DNN_result[]) {
        float[] smooth_score = smooth(DNN_result);
        float current_score=confidence(smooth_score);
        stateMachine(current_score > THRESHOLD);
        if(this.current_state==WakeUpState.filler_wakeupword) {
            Log.v("PostProcessor.java","current_score(wakeup): "+current_score);//20160822 to print log
            return true;
        }
        return false;
    }
}
