# Trinity Robotics FTC 2021

### Team ID: 14413

## how to edit and run code

1. download the [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) for freight frenzy
2. copy and paste these files in the TeamCode/src/main/java/.../teamcode folder and open the project in [Android Studio](https://developer.android.com/studio/)
3. plug your computer into the control hub, select REV control hub from device manager, and press the play button next to it
4. wait for a "launch successful" at the bottom
5. unplug from the robot, connect the driver hub to control hub via wifi
6. robot go beep boop, select configuration and OP mode, initialize, and press play!

### files

1. `AutopOP.java` - robot code for 30 second autonomous period
2. `TeleOP.java` - robot code for driver control
3. `TensorFlowOP.java` - game object detection testing code
4. `...New.java` - experimental file that probably doesn't work
5. `...Old.java` - old version of file in case current one doesn't work

## TeleOP

| gamepad              | function             |
| -------------------- | -------------------- |
| left joystick up     | move forward         |
| left joystick down   | move backward        |
| left joystick left   | strafe left          |
| left joystick right  | strafe right         |
| right joystick left  | rotate left          |
| right joystick right | rotate right         |
| y button             | linear slide level 1 |
| x button             | linear slide level 2 |
| a button             | linear slide level 3 |
| b button             | linear slide down    |
| d-pad left           | linearslide up       |
| left bumper          | delivery system ccw  |
| right bumper         | delivery system cw   |
| d-pad up             | intake system out    |
| d-pad down           | intake system in     |
| left trigger         | duck wheel ccw       |
| right trigger        | duck wheel cw        |

-   cw - clockwise
-   ccw - counterclockwise
-   let us know of any suggestions to make this better!

## AutonOP

TBD

## TensorFlowOP

**Important:** see the software section of the discord server for vuforia key, it should be a long string of random looking letters and numbers. Paste that into the variable called `VUFORIA_KEY` inside the double quotes.

---

## i would love your help!

check the team discord for an ongoing list of programming projects that still need completion.
even without the control hub there's plenty of research and coding to be done!

## the software team!

captain: Lake

-   email: patience.gifford23@trinityschoolnyc.org
