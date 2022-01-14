# Trinity Robotics FTC 2021

### Team ID: 14413

## how to edit and run code

1. download the [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) for freight
   frenzy
2. copy and paste these files in the TeamCode/src/main/java/.../teamcode folder and open the project
   in [Android Studio](https://developer.android.com/studio/)
3. plug your computer into the control hub, select REV control hub from device manager, and press
   the play button next to it (make sure control hub is on!)
4. wait for a "launch successful" at the bottom
5. unplug from the robot, connect the driver hub to control hub via wifi
6. robot go beep boop, select configuration and OP mode, initialize, and press play!

### files

1. `AutonOP` - files for 30s autonomous period
    - `AutopOPSimple.java` - current working file, only goes forward
    - `AutopOPFull.java` - auton integrated with object detection
    - `AutonOP.java` - more complex field navigation, including strafing and turning
    - `AutoOPNew.java` - untested, includes code for lienar slide
2. `TeleOP` - files for 2min driver control period
    - `TeleOPNew.java` - current file, mostly complete, tweaking needed
    - `TeleOP.java` - old file in case new one doesn't work
3. `VuforiaOP` - files for object detection and field navigation, to be integrated with autonomous code
    - `TensorFlowOP.java` - for detecting balls, cubes, ducks, and markers on the field
    - `VuforiaOP.java` - for field navigation and positioning using pictures on the field

## TeleOP

### mapping

| gamepad              | function             |
| -------------------- | -------------------- |
| left joystick up     | move forward         |
| left joystick down   | move backward        |
| left joystick left   | strafe left          |
| left joystick right  | strafe right         |
| right joystick left  | rotate left          |
| right joystick right | rotate right         |
| x button             | linear slide level 1 |
| y button             | linear slide level 2 |
| b button             | linear slide level 3 |
| d-pad up             | linearslide up       |
| d-pad down           | linearslide down     |
| d-pad left           | intake system out    |
| d-pad right          | intake system in     |
| left bumper          | delivery system in   |
| right bumper         | delivery system out  |
| left trigger         | duck wheel ccw       |
| right trigger        | duck wheel cw        |

-   cw - clockwise
-   ccw - counterclockwise
-   let us know of any suggestions to make this better!

### tweaking

-   see `// CONFIGURATION //` section of the file to configure TeleOP behaviour

## AutonOP

TBD

---

## i would love your help!

check the team discord for an ongoing list of programming projects that still need completion. even
without the control hub there's plenty of research and coding to be done!

## the software team!

captain: Lake

-   email: patience.gifford23@trinityschoolnyc.org
