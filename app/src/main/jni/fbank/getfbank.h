//
// Created by zzd on 2016/6/22.
//

#ifndef _GETMFC_H_
#define _GETMFC_H_

#define PI   3.14159265358979
#define TPI  6.28318530717959     /* PI*2 */
#define NUMCHANS 40
#define jINPUT_DIMEN 1640
#define jLAYER_DIMEN 128
#define jOUTPUT_DIMEN 3

void getfbank(short source[],int source_len,float fbank[],int fbank_len);

#endif //MYAPPLICATION2_GETMFC_H
