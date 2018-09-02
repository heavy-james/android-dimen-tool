# android-dimen-tool

this is a tool to auto generate android dimenssion resource for different resolution screens. 

and you can use it to replace your dimen reference in xml files.

this is an idea project, you can compile the jars yourself or just run it with code, or just use jar in the root directory.

all these tools may not be useful to you if you didn't use the screen adapt resolution as the article describes which link is show below.

[Android屏幕适配方案](https://blog.csdn.net/lmj623565791/article/details/45460089)

## create different resources

java -jar ResourceGenerate.jar -b 1920 1080 -t 777 888 -t 888 999

* -b : base design dimenssion, width height.
* -t : target screen dimenssion, width height.

## change design dimenssion

java -jar TextReplace.jar -p yourpath -f .xml -s .java -o 1280 600 -t 1920 1080

* -p : path, your target root path
* -f : positive file name fiter
* -s : negative file name filter
* -o : original desgin dimenssion, width height
* -t : target screen dimenssion, width height.
