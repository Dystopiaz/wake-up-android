//
// Created by dystopia on 2016/7/18.
//

#ifndef _GETDNN_H_
#define _GETDNN_H_

#include "../fbank/getfbank.h"

void getdnn(
        float w0[jINPUT_DIMEN][jLAYER_DIMEN], float b0[jLAYER_DIMEN],
        float w1[jLAYER_DIMEN][jLAYER_DIMEN], float b1[jLAYER_DIMEN],
        float w2[jLAYER_DIMEN][jLAYER_DIMEN], float b2[jLAYER_DIMEN],
        float w3[jLAYER_DIMEN][jOUTPUT_DIMEN], float b3[jOUTPUT_DIMEN],
        float InMat[jINPUT_DIMEN],float OutMat[jOUTPUT_DIMEN]);

#endif //MYAPPLICATION2_GETDNN_H
