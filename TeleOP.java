package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOP", group = "Disco Bot")
public class TeleOP extends LinearOpMode {

    @Override
    public void runOpMode() {
        // MOTORS AND SERVOS //
        // control hub
        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");

        // expansion hub
        DcMotor linearSlide = hardwareMap.dcMotor.get("linearSlide");
        DcMotor intakeSystem = hardwareMap.dcMotor.get("intakeSystem");
        DcMotor duckWheel = hardwareMap.dcMotor.get("duckSystem");

        // servo
        Servo deliverySystem = hardwareMap.servo.get("deliverySystem");

        // CONFIGURATION //
        // motor power config
        double intakeSystemRate = 1; // how fast the intake system should run
        double duckWheelRate = 0.5; //how fast the duck wheel could spin
        double linearSlideLevelRate = 1; // calibrate to reach level 1, 2, 3
        double linearSlideBaseRate = 1; // how fast the linear slide goes up when holding down button

        // delivery system servo config
        double initialPosition = 0.7; // position when intake system running
        double movingPosition = 0.5; // position when holding piece and going up
        double droppingPosition = 0.3; // position when dropping piece

        // SETUP //
//        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE); // backwards gear, much love ryman <3
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // rising edge button detector setup
        boolean risingA = false;
        boolean risingX = false;
        boolean risingY = false;
        boolean risingLeftBumper = false;
        boolean risingRightBumper = false;

        // START //
        waitForStart();
        telemetry.addData("Start", "Hello, World!");
        telemetry.update();

        // set servo position to initial position
        int positionState = 0;
        deliverySystem.setPosition(initialPosition);

        // timer for linear slide
        ElapsedTime motorTimer = new ElapsedTime();
        double startTime = 0;

        // MAIN LOOP //
        while (opModeIsActive()) {

            // MOVEMENT OF MECANUM WHEELS //
            // credit to game manual 0
            double move_x = gamepad2.left_stick_y; // Remember, this is reversed!
            double move_y = -gamepad2.left_stick_x * 1.1; // Counteract imperfect strafing
            double move_rotate = gamepad2.right_stick_x;

            double denominator = Math.max(Math.abs(move_y) + Math.abs(move_x) + Math.abs(move_rotate), 1);
            double frontLeftPower = (move_y + move_x + move_rotate) / denominator;
            double backLeftPower = (move_y - move_x + move_rotate) / denominator;
            double frontRightPower = (move_y - move_x - move_rotate) / denominator;
            double backRightPower = (move_y + move_x - move_rotate) / denominator;

            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            // DELIVERY SYSTEM //
            if (!risingLeftBumper && gamepad1.left_bumper) {
                if (positionState != 0) positionState--;
            } else if (!risingRightBumper && gamepad1.right_bumper) {
                if (positionState != 2) positionState++;
            }

            switch (positionState) {
                case 0:
                    deliverySystem.setPosition(initialPosition);
                    break;
                case 1:
                    deliverySystem.setPosition(movingPosition);
                    break;
                case 2:
                    deliverySystem.setPosition(droppingPosition);
                    break;
            }
            // this is so ugly °~°

            risingLeftBumper = gamepad1.left_bumper;
            risingRightBumper = gamepad1.right_bumper;

            // INTAKE SYSTEM //
            if (gamepad1.dpad_up) {
                intakeSystem.setPower(-intakeSystemRate);
            } else if (gamepad1.dpad_down) {
                intakeSystem.setPower(intakeSystemRate);
            } else {
                intakeSystem.setPower(0);
            }

            // DUCK WHEEL //
            // limit the speed of the duck wheel if necessary
            double duckWheelCCW = Math.min(gamepad1.left_trigger, duckWheelRate);
            double duckWheelCW = Math.min(gamepad1.right_trigger, duckWheelRate);

            if (duckWheelCCW > 0) {
                duckWheel.setPower(-duckWheelCCW);
                telemetry.addData("Duck Wheel", "Counter-clockwise");
                telemetry.update();
            } else if (duckWheelCW > 0) {
                duckWheel.setPower(duckWheelCW);
                telemetry.addData("Duck Wheel", "Clockwise");
                telemetry.update();
            } else {
                duckWheel.setPower(0);
            }

            // LINEAR SLIDE
            double seconds = 0;

            // determine the number of seconds from the bottom the linear slide has to run for
            if (!risingY && gamepad1.y) {
                // move to level 1
                seconds = 0.4 / linearSlideLevelRate;
                startTime = motorTimer.time();
                telemetry.addData("Linear Slide", "Going to level 1");

            } else if (!risingX && gamepad1.x) {
                // move to level 2
                seconds = 1.2 / linearSlideLevelRate;
                startTime = motorTimer.time();
                telemetry.addData("Linear Slide", "Going to level 2");

            } else if (!risingA && gamepad1.a) {
                // move to level 3
                seconds = 2.0 / linearSlideLevelRate;
                startTime = motorTimer.time();
                telemetry.addData("Linear Slide", "Going to level 3");

            } else if (gamepad1.b) {
                // move down when b is pressed
                linearSlide.setPower(-linearSlideBaseRate);
                telemetry.addData("Linear Slide", "Going down");

            } else if (gamepad1.dpad_left) {
                // secret debug in case things go wrong
                linearSlide.setPower(linearSlideBaseRate);
                telemetry.addData("Linear Slide", "Going up");
            }
            telemetry.update();
            // this is also quite ugly °-°

            // run linear slide motor while current time < target time
            while (motorTimer.time() < startTime + seconds) {
                linearSlide.setPower(1);
            }
            linearSlide.setPower(0);

            risingA = gamepad1.a;
            risingX = gamepad1.x;
            risingY = gamepad1.y;

            idle();
        }
    }
}

// 14413!

// /\__/\
// (=o.o=)
// |/--\|
// (")-(")
