package org.firstinspires.ftc.teamcode.autonOP;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

@Autonomous(name = "AutonOP Simple", group = "Auton")
public class AutonOPSimple extends LinearOpMode {

    // MOTORS AND SERVOS//
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    static final int MOTOR_TICK_COUNT = 1440;
    static final double MOTOR_DIAMETER = 3.93;

    @Override
    public void runOpMode() {

        // GET MOTORS //
        // control hub
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // START //
        waitForStart();

        // reset encoder
        backLeft.setMode(RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(RunMode.STOP_AND_RESET_ENCODER);

        // math to get driving target
        double inchesToMove = 18;

        double circumference = 3.1415 * MOTOR_DIAMETER;
        double rotationsNeeded = inchesToMove / circumference;
        int encoderDrivingTarget = (int)(rotationsNeeded * MOTOR_TICK_COUNT);

        // goals
        backLeft.setTargetPosition(encoderDrivingTarget);
        backRight.setTargetPosition(encoderDrivingTarget);

        // start!
        backLeft.setPower(0.5);
        backRight.setPower(0.5);
        backLeft.setMode(RunMode.RUN_TO_POSITION);
        backRight.setMode(RunMode.RUN_TO_POSITION);

        // do nothing while moving
        while (frontLeft.isBusy() || frontRight.isBusy()) {
            // do nothing
            telemetry.addData("Path 1", "driving to 18 in");
            telemetry.update();
        }

        // turn off when arrived
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}