#!/bin/sh

cp="WEB-INF/classes/"
for lib in `ls -1 WEB-INF/lib/*.jar`;do
    cp=$cp:$lib
done

echo $cp

java -cp $cp com.threeio.airplanenoise.server.AirplaneNoiseServer 

