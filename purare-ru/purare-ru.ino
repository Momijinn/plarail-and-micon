#include <stdlib.h>
int motor1 = 7;
int motor2 = 8;
int motorP = 6;
//uno 0~255
//nano 

char stop[10] = "stop";
char inString[10];
int i = 0;

void setup() {
    Serial.begin(9600);
    pinMode(motor1, OUTPUT);
    pinMode(motor2, OUTPUT);

    digitalWrite(motor1, LOW);
    digitalWrite(motor2, LOW);
}

/**
*モーターのセットアップ
**/
void redymotor(){
    int j = 0;
    for(j=0; j<10; j++){
        //比較する文字がなかったらforを抜ける
        if(inString[j] == '\0'){
            break;
        }

        //文字が異なっていたらSucsessする
        if(inString[j] != stop[j]){
        Serial.println("succses");
        gomotor(atoi(inString)); //速度をもってくる
        break;
        }else{
            //緊急停止時の処理
            Serial.print(stop[j]);
            Serial.println(inString[j]);
            gomotor(0);
        }
    }
}

/**
*走らせる速度など
**/
void gomotor(int speed){
    //停止処理
    if(speed == 0){
        digitalWrite(motor1, LOW);
        digitalWrite(motor2, LOW);
        analogWrite(motorP, speed);
    }else{
        //走らせる処理
        digitalWrite(motor1, HIGH);
        digitalWrite(motor2, LOW);
        analogWrite(motorP, speed);
    }
}

void loop() {
    if(Serial.available()){
        inString[i] = Serial.read();
        if(i > 10 || inString[i] == '/'){
            inString[i] = '\0';
            i = 0;
            Serial.println(inString);
            redymotor(); //モーターへ移行
        }
        else{
            i++;
        }
    }
}
