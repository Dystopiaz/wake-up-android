package  nwpu_aslp.wakeup;
/**
 * Created by houjingyong on 2016/7/24.
 * Created by houjingyong on 2016/7/24.
 */
public class NdkJniUtils {

    public native float[] pcm2fbank(short[] source,int length);
    public native boolean initDnn(float[][] w0, float[][] w1, float[][] w2, float[][] w3,
                                  float[] b0, float[] b1, float[] b2, float[] b3);
    public native float[] getdnn(float[] InMat);
    static {
        System.loadLibrary("GetJniLibName");
    }
}