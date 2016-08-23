//
// Created by dystopia on 2016/7/18.
//
#include "getdnn.h"
#include "./../utils/android_log_print.h"
#include<math.h>
//#include <stdio.h>

int i,j;
float ReLU(float x)
{
    return x >= 0 ? x : 0;
}
void softmax(float x[jOUTPUT_DIMEN])
{
    double max = -999999.0;
    double sum = 0.0;
    for (i = 0; i < jOUTPUT_DIMEN; i++)
    {
        if (max < x[i]) max = x[i];
    }
    //printf("max:%f", max);
    for (i = 0; i< jOUTPUT_DIMEN; i++)
    {
        x[i] = exp(x[i] - max);
        sum += x[i];
    }
    //printf("sum:%f\n", sum);
    for (i = 0; i < jOUTPUT_DIMEN; i++)
    {
        x[i] /= sum;
    }
}
void Input2Layer(float x[jINPUT_DIMEN], float w[jINPUT_DIMEN][jLAYER_DIMEN], float b[jLAYER_DIMEN], float y[jLAYER_DIMEN])
{
    for (i = 0; i < jLAYER_DIMEN; i++)
    {
        y[i] = 0;
        for (j = 0; j < jINPUT_DIMEN; j++)
        {
            y[i] += x[j] * w[j][i];
        }
        y[i] += b[i];
        y[i] = ReLU(y[i]);
    }
}

void Layer2Layer(float x[jLAYER_DIMEN], float w[jLAYER_DIMEN][jLAYER_DIMEN], float b[jLAYER_DIMEN], float y[jLAYER_DIMEN])
{
    for (i = 0; i < jLAYER_DIMEN; i++)
    {
        y[i] = 0;
        for (j = 0; j < jLAYER_DIMEN; j++)
        {
        if(x[j]){
            y[i] += x[j] * w[j][i];
        }
        }
        y[i] += b[i];
        y[i] = ReLU(y[i]);

    }
}
void Layer2Output(float x[jLAYER_DIMEN], float w[jLAYER_DIMEN][jOUTPUT_DIMEN], float b[jOUTPUT_DIMEN], float y[jOUTPUT_DIMEN])
{
    for (i = 0; i < jOUTPUT_DIMEN; i++)
    {
        y[i] = 0;
        for (j = 0; j < jLAYER_DIMEN; j++)
        {
        if(x[j]){
            y[i] += x[j] * w[j][i];
        }
        }
        y[i] += b[i];
        //y[i] = ReLU(y[i]);
    }
    softmax(y);
    //printf("%f %f %f\n",y[0], y[1], y[2]);
}
void getdnn(
        float w0[jINPUT_DIMEN][jLAYER_DIMEN], float b0[jLAYER_DIMEN],
        float w1[jLAYER_DIMEN][jLAYER_DIMEN], float b1[jLAYER_DIMEN],
        float w2[jLAYER_DIMEN][jLAYER_DIMEN], float b2[jLAYER_DIMEN],
        float w3[jLAYER_DIMEN][jOUTPUT_DIMEN], float b3[jOUTPUT_DIMEN],
        float InMat[jINPUT_DIMEN],float OutMat[jOUTPUT_DIMEN]){
    float layerout0[jLAYER_DIMEN];
    float layerout1[jLAYER_DIMEN];
    float layerout2[jLAYER_DIMEN];
    int count = 0;
    int row=0;
    Input2Layer(InMat, w0, b0, layerout0);
    Layer2Layer(layerout0, w1, b1, layerout1);
    Layer2Layer(layerout1, w2, b2, layerout2);
    Layer2Output(layerout2, w3, b3, OutMat);
}