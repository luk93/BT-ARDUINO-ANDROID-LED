#include <SoftwareSerial.h>
#define bt_tx 10
#define bt_rx 11
#define led 6
char command;
String string;
boolean ledON = false;

SoftwareSerial bt = SoftwareSerial(bt_rx, bt_tx);
 
void setup() {
  pinMode(bt_rx, INPUT);
  pinMode(bt_tx, OUTPUT);
  pinMode(led, OUTPUT);
  Serial.begin(9600); 
  bt.begin(9600);
}
 
void loop() 
{
  // listen for the data
  if (bt.available() > 0 ) 
  {
    string = "";
  }
  while (bt.available() > 0)
  {
    command = ((byte)bt.read());
    if (command == ":")
    {
      break;   
    }
    else
    {
      string += command;
    }
    delay(1);
  }
  if(string == "ON")
  {
    ledOn();
    ledON = true;
  }
  if(string == "OFF")
  {
    ledOff();
    ledON = false;
    Serial.println(string);
  }
  if ((string.toInt()>=0)&&(string.toInt()<=255))
  {
    if (ledON==true)
    {
      analogWrite(led, string.toInt());
      Serial.println(string); //debug
      delay(10);
    }
  }
}
void ledOn()
{
analogWrite(led, 255);
delay(10);
}
void ledOff()
{
analogWrite(led, 0);
delay(10);
}


