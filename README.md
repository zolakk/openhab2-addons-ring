# <bindingName> Alarmclock

This is an experimental binding to a virtual alarm clock. I know there are different opinions to have an alarmclock like this as a binding rather than a trigger for a rule engine, but I think nevertheless that it makes some sense. Please let me explain.

The openHAB system is very strong in supporting sitemaps on a variety of devices, including mobile Android and iOS devices. It is a nice advantage if day-to-day operation of the openHAB system can be done on all those devices. This implies using only the limited set of widgets a sitemap supports, like Text, Frame, Switch, Slider, Setpoint etc.   

Suppose you have an openHAB rule to switch off some group of lights at a certain time, but you want to change the setting from time to time. When having the actual alarmclock available as a binding, you could include the setting feature to a standard sitemap. This is exactly what this binding offers. Please share different thoughts about this subject, because I realize from an architectural point of view this is kind of a strange binding. 

## Supported Things

The binding uses the system clock for the current time, and checks every minute if one of the trigger times (either on or off) are reached. It supports an alarmclock, settable with a precision of 1 minute for both on and off times. 
It als supports a timer, that counts down in seconds from a certain settable starting point.

## Discovery

Auto-discovery is not applicable to this binding. Default on and off times may be specified using e.g. the Paper UI. Just add a thing from the Alarmclock binding, choose the Alarmclock thing, and specify the on and off hours and minutes.

## Binding Configuration

There is no binding configuration necessary. Place the alarmclock jar file into the addons directory of openHAB and the binding will be supported.

## Thing Configuration

Configuring the alarmclock thing is quite straightforward. When creating the thing with the Paper UI you are prompted for entering the thing name, ontime (hour and minutes) and offtime (hour and minutes).  

## Channels

The channels can be retrieved from the Paper UI after configuring. They include:

Todo:


## Full Example

Todo: _Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Any custom content here!

Todo: _Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
