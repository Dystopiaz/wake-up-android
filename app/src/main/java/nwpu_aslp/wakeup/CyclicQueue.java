package  nwpu_aslp.wakeup;
/**
 * Created by houjingyong on 2016/7/24.
 * Created by houjingyong on 2016/7/24.
 */
public class CyclicQueue {
    private float []datas;
    private int max_data_size;
    private int head;
    private int tail;
    private float max_data;
    private float sum_data;
    private boolean is_null=true;
    private boolean is_full=false;

    public CyclicQueue(int max_data_size) {

        this.max_data_size = max_data_size;
        this.datas = new float[max_data_size];
        this.max_data = -999999;
        this.sum_data = 0;
        this.head = 0;
        this.tail = 0;
        this.is_null = true;
        this.is_full = false;
    }

    private void reset_max_data() {
        this.max_data=this.datas[this.head];
        int max_index=0;
        if(this.tail<this.head) {
            max_index=this.tail+this.max_data_size;
        } else if(this.tail<this.head) {
            this.max_data=-999999;
        }
        else {
            max_index=this.tail;
        }
        for(int i=this.head; i < max_index; i++) {
            if (this.max_data < this.datas[i%this.max_data_size]) {
                this.max_data=this.datas[i%this.max_data_size];
            }
        }
    }

    public boolean push(float data) {
        if (!this.is_full) {
            this.datas[tail]=data;
            this.tail+=1;
            this.tail=this.tail%this.max_data_size;
            if(data > this.max_data) {
                this.max_data=data;
            }
            this.sum_data+=data;
            if(this.tail == this.head){
                this.is_full=true;
            }
            this.is_null=false;

        } else {
            this.pop();
            //this.is_full=false;
            this.push(data);
        }
        return true;
    }

    public float pop() {
        float data=0;
        if (!this.is_null) {
            this.sum_data-=this.datas[this.head];
            data=this.datas[this.head];
            this.head += 1;
            this.head = this.head %this.max_data_size;
            if(Math.abs(data-this.max_data) < 0.000001) {
                reset_max_data();
            }
            if (this.head==this.tail) {
                this.is_null=true;
            }
            this.is_full=false;
            return data;
        } else {
            return -1;
        }

    }
    public float get_max_data() {
        return this.max_data;
    }

    public float get_sum_data() {
        return this.sum_data;
    }

    public float get_mean_data(){
        return this.sum_data/this.get_data_size();
    }

    public float get_data_size() {
        if (this.tail > this.head){
            return this.tail-this.head;
        } else if(this.tail ==this.head) {
            if(this.is_full) {
                return this.max_data_size;
            } else {
                return 0;
            }

        } else {
            return this.tail+this.max_data_size-this.head;
        }

    }
}

