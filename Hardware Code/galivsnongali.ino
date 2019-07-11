// This #include statement was automatically added by the Particle IDE.
#include <InternetButton.h>

InternetButton b = InternetButton();
int rValue = 0;
int gValue = 0;
int bValue = 0;
bool lightsColorChanged = false;

void setup() {

    // 1. Setup the Internet Button
    b.begin();
    Particle.function("colors", controlColor);
}

void loop(){
    
    if(b.buttonOn(2)){
        Particle.publish("answer","1",60,PUBLIC);
        delay(500);
    }
    
    if(b.buttonOn(4)){
        Particle.publish("answer","2",60,PUBLIC);
        delay(500);
    }
}

int controlColor(String command){
    
    int commaIndex = command.indexOf(",");
    
    String r = command.substring(0, commaIndex);
    Particle.publish("red", r);
    String r_str = command.substring(commaIndex+1);
   // Particle.publish("remaining", r_str);
    int remaining = r_str.indexOf(",");
   // Particle.publish("remaining comma", String(remaining));
    String g = r_str.substring(0, remaining);
    Particle.publish("green", g);
    String bl = r_str.substring(remaining+1);
    Particle.publish("blue", bl);
    rValue = atoi(r.c_str());
    gValue = atoi(g.c_str());
    bValue = atoi(bl.c_str());
    
    lightsColorChanged = true;
    
    b.allLedsOff();
    b.allLedsOn(rValue,gValue,bValue);
    delay(2000);
    b.allLedsOff();
    
    return 1;
}
