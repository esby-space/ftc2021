package org.firstinspires.ftc.teamcode.teleOP;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOP New", group = "TeleOP")
public class TeleOPNew extends LinearOpMode {

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
        // set to true if using two controllers, gamepad 2 will control chassis, gamepad 1 everything else
        boolean twoPlayers = false;

        // motor power config
        double calmDownMecanum = 1; // how much the chassis just needs to chill
        double intakeSystemRate = 1; // how fast the intake system should run
        double duckWheelRate = 0.5; //how fast the duck wheel could spin
        double linearSlideLevelRate = 0.5; // calibrate to reach level 1, 2, 3
        double linearSlideBaseRate = 1; // how fast the linear slide goes up when holding down button

        // delivery system servo config
        double initialPosition = 0.7; // position when intake system running
        double movingPosition = 0.6; // position when holding piece and going up
        double droppingPosition = 0.3; // position when dropping piece

        // SETUP //
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        // frontLeft.setDirection(DcMotorSimple.Direction.REVERSE); // backwards gear, much love ryman <3

        // rising edge button detector setup
        boolean risingB = false;
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
        ElapsedTime linearSlideRuntime = new ElapsedTime();

        // MAIN LOOP //
        while (opModeIsActive()) {

            // MOVEMENT OF MECANUM WHEELS //
            // credit to game manual 0
            double move_y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double move_x = -gamepad1.left_stick_x * 1.0; // Counteract imperfect strafing
            double move_rotate = -gamepad1.right_stick_x;

            if (twoPlayers) {
                move_y = -gamepad2.left_stick_y;
                move_x = -gamepad2.left_stick_x * 1.1;
                move_rotate = -gamepad2.right_stick_x;
            }

            double denominator = Math.max(Math.abs(move_y) + Math.abs(move_x) + Math.abs(move_rotate), 1) * calmDownMecanum;
            frontLeft.setPower((move_y + move_x + move_rotate) / denominator);
            backLeft.setPower((move_y - move_x + move_rotate) / denominator);
            frontRight.setPower((move_y - move_x - move_rotate) / denominator);
            backRight.setPower((move_y + move_x - move_rotate) / denominator);

            // DELIVERY SYSTEM //
            if (!risingLeftBumper && gamepad1.left_bumper) {
                if (positionState != 0) positionState--;
            } else if (!risingRightBumper && gamepad1.right_bumper) {
                if (positionState != 2) positionState++;
            }

            switch (positionState) {
                case 0:
                    // ready to pick things up
                    deliverySystem.setPosition(initialPosition);
                    break;
                case 1:
                    // moving up with a piece
                    deliverySystem.setPosition(movingPosition);
                    break;
                case 2:
                    // dropping a piece
                    deliverySystem.setPosition(droppingPosition);
                    break;
            }
            // this is so ugly °~°

            risingLeftBumper = gamepad1.left_bumper;
            risingRightBumper = gamepad1.right_bumper;

            // INTAKE SYSTEM //
            if (gamepad1.dpad_left) {
                // spits piece out
                intakeSystem.setPower(-intakeSystemRate);
            } else if (gamepad1.dpad_right) {
                // eats piece in
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
            } else if (duckWheelCW > 0) {
                duckWheel.setPower(duckWheelCW);
            } else {
                duckWheel.setPower(0);
            }

            // LINEAR SLIDE
            double timeout = 0;

            // determine the number of seconds from the bottom the linear slide has to run for
            // move the delivery system servo to moving position
            if (!risingX && gamepad1.x) {
                // move to level 1
                positionState = 1;
                deliverySystem.setPosition(movingPosition);
                timeout = 0.4 * linearSlideLevelRate;
                linearSlideRuntime.reset();
            } else if (!risingY && gamepad1.y) {
                // move to level 2
                positionState = 1;
                deliverySystem.setPosition(movingPosition);
                timeout = 1.2 * linearSlideLevelRate;
                linearSlideRuntime.reset();

            } else if (!risingB && gamepad1.b) {
                // move to level 3
                positionState = 1;
                deliverySystem.setPosition(movingPosition);
                timeout = 2.0 * linearSlideLevelRate;
                linearSlideRuntime.reset();

            } else if (gamepad1.dpad_up) {
                // move up
                linearSlide.setPower(linearSlideBaseRate);

            } else if (gamepad1.dpad_down) {
                // move down
                linearSlide.setPower(-linearSlideBaseRate);
            }
            // this is also quite ugly °^°

            // run linear slide motor while current time < target time
            while (linearSlideRuntime.time() < timeout) {
                linearSlide.setPower(1);
            }
            linearSlide.setPower(0);

            risingB = gamepad1.b;
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
