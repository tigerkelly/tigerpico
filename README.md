# tigerpico
A qui for openocd, running under RPI.

This is a javafx gui for the openocd program and the Raspberry Pi Pico board.

![alt text](/images/tigerpico.png?raw=true)

# About development setup.

When I develop for RPI Pico I use a RPI 4 64bit and 4gig of memory.  I also use a USB SSD drive
to boot from which is much faster then the SD card. I would rather use C over C++ or python becasue I
am old and set in my ways.  I rarely program in anything other than C and/or Java.

# Setup

[Install OS on USB SSD](https://linuxhint.com/how_to_boot_raspberry_pi_4_from_usb_ssd/)
*NOTE:* When installing the RPI OS be sure and select the 64bit full version.

Once done installing the 64bit OS onto the USB SSD drive, you will need to install a 64bit Java version.
Go to [BellSoft](https://bell-sw.com/) and download the Java JDK full 18 version for linux.
Click on Downloads->Liberica JDK then scroll down and select JDK 18 current, then scroll down to
Linux 64bit, click ARM radio button and then for the Package type select 'Full JDK' in the drop down box.

The file downloaded should have the .deb extension and look something like
*bellsoft-jdk18.0.2+10-linux-aarch64-full.deb*

To install the BellSoft java use **sudo apt install ./bellsoft-jdk18.0.2+10-linux-aarch64-full.deb**
Your version maybe different.

# Install Pico SDK and other programs for C development.

Use the very nice video from Shawn Hymel to setup your RPI 4 for C development of Pico.
[Video](https://www.youtube.com/watch?v=B5rQSoOmR5w)

# Install TigerPico

The TigerPico program is installed either by compiling the java source or using the tar file in the source.

In the pi home directory
	tar xvzf ../TigerPico.tgz

The *bin* directory will contain a shell script to run the TigerPico GUI program

# Using Eclipse to build and/or modify source code.

The *tigerpcio* directory is an export of the Eclipse workspace for TigerPico. Import this directory into
Eclipse.
