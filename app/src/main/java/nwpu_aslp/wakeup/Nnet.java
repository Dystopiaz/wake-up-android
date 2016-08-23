package  nwpu_aslp.wakeup;
/**
 * Created by houjingyong on 2016/7/24.
 * Created by houjingyong on 2016/7/24.
 */
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Nnet {

    public boolean initNnet(Context con) {
        try {
            InputStream is = con.getResources().getAssets().open("param.txt");
            Reader in = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(in);
            String s = null;
            System.out.println("init dnn");
            // w0 b0
            for (int i = 0; i < INPUT_DIMEN; i++) {
                String[] s2 = br.readLine().split(" ");
                for (int j = 0; j < LAYER_DIMEN; j++) {
                    this.w0[i][j] = Float.valueOf(s2[j]).floatValue();
                }
            }
            for (int i = 0; i < LAYER_DIMEN; i++) {
                String s2 = br.readLine();
                this.b0[i] = Float.valueOf(s2).floatValue();
            }
            // w1 b1
            for (int i = 0; i < LAYER_DIMEN; i++) {
                String[] s2 = br.readLine().split(" ");
                for (int j = 0; j < LAYER_DIMEN; j++) {
                    this.w1[i][j] = Float.valueOf(s2[j]).floatValue();
                }
            }
            for (int i = 0; i < LAYER_DIMEN; i++) {
                String s2 = br.readLine();
                this.b1[i] = Float.valueOf(s2).floatValue();
            }
            // w2 b2
            for (int i = 0; i < LAYER_DIMEN; i++) {
                String[] s2 = br.readLine().split(" ");
                for (int j = 0; j < LAYER_DIMEN; j++) {
                    this.w2[i][j] = Float.valueOf(s2[j]).floatValue();
                }
            }
            for (int i = 0; i < LAYER_DIMEN; i++) {
                String s2 = br.readLine();
                this.b2[i] = Float.valueOf(s2).floatValue();
            }

            // w3 b3
            for (int i = 0; i < LAYER_DIMEN; i++) {
                String[] s2 = br.readLine().split(" ");
                for (int j = 0; j < OUTPUT_DIMEN; j++) {
                    this.w3[i][j] = Float.valueOf(s2[j]).floatValue();
                }
            }
            for (int i = 0; i < OUTPUT_DIMEN; i++) {
                String s2 = br.readLine();
                this.b3[i] = Float.valueOf(s2).floatValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private final int INPUT_DIMEN = 1640;
    private final int  LAYER_DIMEN = 128;
    private final int  OUTPUT_DIMEN = 3;
    //public final int NUMCHANS = 40;
    //public final int  LAYER = 4;

    public float[][] w0 = new float [INPUT_DIMEN][LAYER_DIMEN]; public float[] b0 = new float[LAYER_DIMEN];
    public float[][] w1= new float[LAYER_DIMEN][LAYER_DIMEN]; public float[] b1 = new float[LAYER_DIMEN];
    public float[][] w2 = new float[LAYER_DIMEN][LAYER_DIMEN]; public float[] b2 = new float[LAYER_DIMEN];
    public float[][] w3 = new float[LAYER_DIMEN][OUTPUT_DIMEN];public float[] b3 = new float[OUTPUT_DIMEN];
}
